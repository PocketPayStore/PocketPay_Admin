package pocketpaystore.pocketpay_admin.payment.repository;

import static pocketpaystore.pocketpay_admin.order.domain.QOrder.order;
import static pocketpaystore.pocketpay_admin.payment.domain.QPayment.payment;
import static pocketpaystore.pocketpay_admin.payment.domain.QPaymentStatusHistory.paymentStatusHistory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import pocketpaystore.pocketpay_admin.payment.domain.PaymentStatus;
import pocketpaystore.pocketpay_admin.payment.dto.request.PaymentSearchCondition;
import pocketpaystore.pocketpay_admin.payment.dto.response.PaymentDetailResponse;
import pocketpaystore.pocketpay_admin.payment.dto.response.PaymentStatusHistoryResponse;
import pocketpaystore.pocketpay_admin.payment.dto.response.PaymentStatisticsResponse;
import pocketpaystore.pocketpay_admin.payment.dto.response.PaymentSummaryResponse;

@RequiredArgsConstructor
public class PaymentRepositoryCustomImpl implements PaymentRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public List<PaymentSummaryResponse> findPayments(PaymentSearchCondition condition) {
		return queryFactory
				.select(Projections.constructor(PaymentSummaryResponse.class,
						payment.id, order.orderNumber, payment.status, payment.amount, payment.updatedAt))
				.from(payment)
				.join(order).on(order.id.eq(payment.orderId))
				.where(
						payment.id.gt(condition.getLastId()),
						payment.deleted.isFalse(),
						statusEq(condition.getStatus()),
						orderNumberContains(condition.getOrderNumber()),
						createdAtGoe(condition.getFrom()),
						createdAtLt(condition.getTo()))
				.orderBy(payment.id.asc())
				.limit(condition.getSize())
				.fetch();
	}

	@Override
	public Optional<PaymentDetailResponse> findPayment(Long paymentId) {
		PaymentDetailResponse response = queryFactory
				.select(Projections.constructor(PaymentDetailResponse.class,
						payment.id, payment.orderId, order.orderNumber, payment.paymentMethod,
						payment.pgProvider, payment.pgTransactionId, payment.idempotencyKey,
						payment.amount, payment.usedPointAmount, payment.refundableAmount,
						payment.status, payment.failureCode, payment.failureMessage,
						payment.approvedAt, payment.createdAt, payment.updatedAt))
				.from(payment)
				.join(order).on(order.id.eq(payment.orderId))
				.where(payment.id.eq(paymentId), payment.deleted.isFalse())
				.fetchOne();
		return Optional.ofNullable(response);
	}

	@Override
	public List<PaymentStatusHistoryResponse> findPaymentStatusHistories(Long paymentId) {
		return queryFactory
				.select(Projections.constructor(PaymentStatusHistoryResponse.class,
						paymentStatusHistory.id, paymentStatusHistory.status, paymentStatusHistory.createdAt))
				.from(paymentStatusHistory)
				.where(paymentStatusHistory.paymentId.eq(paymentId), paymentStatusHistory.deleted.isFalse())
				.orderBy(paymentStatusHistory.id.asc())
				.fetch();
	}

	@Override
	public PaymentStatisticsResponse findPaymentStatistics(LocalDateTime todayStart, LocalDateTime tomorrowStart) {
		return queryFactory
				.select(Projections.constructor(PaymentStatisticsResponse.class,
						payment.id.count(),
						new CaseBuilder()
								.when(payment.createdAt.goe(todayStart).and(payment.createdAt.lt(tomorrowStart)))
								.then(1L).otherwise(0L).sum().coalesce(0L),
						new CaseBuilder()
								.when(payment.createdAt.goe(todayStart).and(payment.createdAt.lt(tomorrowStart)))
								.then(payment.amount).otherwise(0L).sum().coalesce(0L),
						new CaseBuilder()
								.when(payment.createdAt.goe(todayStart)
										.and(payment.createdAt.lt(tomorrowStart))
										.and(payment.status.eq(PaymentStatus.DONE)))
								.then(1L).otherwise(0L).sum().coalesce(0L),
						new CaseBuilder()
								.when(payment.createdAt.goe(todayStart)
										.and(payment.createdAt.lt(tomorrowStart))
										.and(payment.status.in(PaymentStatus.FAILED, PaymentStatus.TIMEOUT_UNKNOWN)))
								.then(1L).otherwise(0L).sum().coalesce(0L)))
				.from(payment)
				.where(payment.deleted.isFalse())
				.fetchOne();
	}

	private BooleanExpression statusEq(PaymentStatus status) {
		return status == null ? null : payment.status.eq(status);
	}

	private BooleanExpression orderNumberContains(String orderNumber) {
		return orderNumber == null || orderNumber.isBlank()
				? null
				: order.orderNumber.containsIgnoreCase(orderNumber);
	}

	private BooleanExpression createdAtGoe(LocalDateTime from) {
		return from == null ? null : payment.createdAt.goe(from);
	}

	private BooleanExpression createdAtLt(LocalDateTime to) {
		return to == null ? null : payment.createdAt.lt(to);
	}
}
