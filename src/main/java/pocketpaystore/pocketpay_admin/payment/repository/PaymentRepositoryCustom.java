package pocketpaystore.pocketpay_admin.payment.repository;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

import pocketpaystore.pocketpay_admin.payment.dto.request.PaymentSearchCondition;
import pocketpaystore.pocketpay_admin.payment.dto.response.PaymentDetailResponse;
import pocketpaystore.pocketpay_admin.payment.dto.response.PaymentStatusHistoryResponse;
import pocketpaystore.pocketpay_admin.payment.dto.response.PaymentStatisticsResponse;
import pocketpaystore.pocketpay_admin.payment.dto.response.PaymentSummaryResponse;

public interface PaymentRepositoryCustom {
	List<PaymentSummaryResponse> findPayments(PaymentSearchCondition condition);

	Optional<PaymentDetailResponse> findPayment(Long paymentId);

	List<PaymentStatusHistoryResponse> findPaymentStatusHistories(Long paymentId);

	PaymentStatisticsResponse findPaymentStatistics(LocalDateTime todayStart, LocalDateTime tomorrowStart);
}
