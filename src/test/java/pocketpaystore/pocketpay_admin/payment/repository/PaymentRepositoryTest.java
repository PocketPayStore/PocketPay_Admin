package pocketpaystore.pocketpay_admin.payment.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import pocketpaystore.pocketpay_admin.common.config.QuerydslConfig;
import pocketpaystore.pocketpay_admin.payment.domain.PaymentStatus;
import pocketpaystore.pocketpay_admin.payment.dto.request.PaymentSearchCondition;

@DataJpaTest
@Import(QuerydslConfig.class)
class PaymentRepositoryTest {
	@Autowired
	private PaymentRepository paymentRepository;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@BeforeEach
	void setUp() {
		LocalDateTime now = LocalDateTime.now();
		insertOrder(1L, "ORDER-001", now.minusMinutes(2));
		insertOrder(2L, "ORDER-002", now.minusMinutes(1));
		insertPayment(1L, 1L, "READY", now.minusMinutes(2));
		insertPayment(2L, 2L, "DONE", now.minusMinutes(1));
		insertHistory(1L, 2L, "READY", now.minusSeconds(30));
		insertHistory(2L, 2L, "IN_PROGRESS", now.minusSeconds(20));
		insertHistory(3L, 2L, "DONE", now.minusSeconds(10));
	}

	@Test
	void 커서와_검색_조건으로_결제_목록을_조회한다() {
		PaymentSearchCondition condition = new PaymentSearchCondition();
		condition.setLastId(1L);
		condition.setSize(10);
		condition.setStatus(PaymentStatus.DONE);
		condition.setOrderNumber("002");

		var responses = paymentRepository.findPayments(condition);

		assertThat(responses).hasSize(1);
		assertThat(responses.get(0).getId()).isEqualTo(2L);
		assertThat(responses.get(0).getOrderNumber()).isEqualTo("ORDER-002");
		assertThat(responses.get(0).getStatus()).isEqualTo(PaymentStatus.DONE);
	}

	@Test
	void 결제_상세를_조회한다() {
		var response = paymentRepository.findPayment(2L);

		assertThat(response).isPresent();
		assertThat(response.get().getOrderNumber()).isEqualTo("ORDER-002");
		assertThat(response.get().getPgTransactionId()).isEqualTo("PG-2");
		assertThat(response.get().getAmount()).isEqualTo(20_000L);
	}

	@Test
	void 결제_상태_이력을_발생_순서대로_조회한다() {
		var responses = paymentRepository.findPaymentStatusHistories(2L);

		assertThat(responses)
				.extracting("status")
				.containsExactly(PaymentStatus.READY, PaymentStatus.IN_PROGRESS, PaymentStatus.DONE);
	}

	@Test
	void 전체와_오늘_결제_통계를_조회한다() {
		LocalDate today = LocalDate.now();

		var response = paymentRepository.findPaymentStatistics(
				today.atStartOfDay(), today.plusDays(1).atStartOfDay());

		assertThat(response.getTotalCount()).isEqualTo(2L);
		assertThat(response.getTodayCount()).isEqualTo(2L);
		assertThat(response.getTodayAmount()).isEqualTo(30_000L);
		assertThat(response.getTodayDoneCount()).isEqualTo(1L);
		assertThat(response.getTodayAttentionCount()).isZero();
	}

	private void insertOrder(Long id, String orderNumber, LocalDateTime createdAt) {
		jdbcTemplate.update("""
				insert into orders (id, order_number, created_at, updated_at, is_deleted)
				values (?, ?, ?, ?, ?)
				""", id, orderNumber, Timestamp.valueOf(createdAt), Timestamp.valueOf(createdAt), false);
	}

	private void insertPayment(Long id, Long orderId, String status, LocalDateTime createdAt) {
		jdbcTemplate.update("""
				insert into payment (
					id, order_id, payment_method, pg_provider, pg_transaction_id, idempotency_key,
					amount, used_point_amount, refundable_amount, status, failure_code, failure_message,
					approved_at, created_at, updated_at, is_deleted
				) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
				""", id, orderId, "CARD", "MOCK_PG", "PG-" + id, "KEY-" + id,
				id * 10_000L, 0L, id * 10_000L, status, null, null,
				Timestamp.valueOf(createdAt), Timestamp.valueOf(createdAt), Timestamp.valueOf(createdAt), false);
	}

	private void insertHistory(Long id, Long paymentId, String status, LocalDateTime createdAt) {
		jdbcTemplate.update("""
				insert into payment_status_history (id, payment_id, status, created_at, updated_at, is_deleted)
				values (?, ?, ?, ?, ?, ?)
				""", id, paymentId, status, Timestamp.valueOf(createdAt), Timestamp.valueOf(createdAt), false);
	}
}
