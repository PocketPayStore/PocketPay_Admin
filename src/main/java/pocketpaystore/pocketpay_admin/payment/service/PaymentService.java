package pocketpaystore.pocketpay_admin.payment.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import pocketpaystore.pocketpay_admin.common.exception.CustomException;
import pocketpaystore.pocketpay_admin.common.exception.errorcode.PaymentErrorCode;
import pocketpaystore.pocketpay_admin.payment.dto.request.PaymentSearchCondition;
import pocketpaystore.pocketpay_admin.payment.dto.response.AttentionPaymentResponse;
import pocketpaystore.pocketpay_admin.payment.dto.response.AttentionPaymentStatisticsResponse;
import pocketpaystore.pocketpay_admin.payment.dto.response.PaymentDetailResponse;
import pocketpaystore.pocketpay_admin.payment.dto.response.PaymentStatusHistoryResponse;
import pocketpaystore.pocketpay_admin.payment.dto.response.PaymentStatisticsResponse;
import pocketpaystore.pocketpay_admin.payment.dto.response.PaymentSummaryResponse;
import pocketpaystore.pocketpay_admin.payment.repository.PaymentRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {
	private final PaymentRepository paymentRepository;

	public List<PaymentSummaryResponse> findPayments(PaymentSearchCondition condition) {
		return paymentRepository.findPayments(condition);
	}

	public PaymentDetailResponse findPayment(Long paymentId) {
		return paymentRepository.findPayment(paymentId)
				.orElseThrow(() -> new CustomException(PaymentErrorCode.PAYMENT_NOT_FOUND));
	}

	public List<PaymentStatusHistoryResponse> findPaymentStatusHistories(Long paymentId) {
		if (!paymentRepository.existsById(paymentId)) {
			throw new CustomException(PaymentErrorCode.PAYMENT_NOT_FOUND);
		}
		return paymentRepository.findPaymentStatusHistories(paymentId);
	}

	public PaymentStatisticsResponse findPaymentStatistics() {
		LocalDate today = LocalDate.now();
		return paymentRepository.findPaymentStatistics(today.atStartOfDay(), today.plusDays(1).atStartOfDay());
	}

	public List<AttentionPaymentResponse> findAttentionPayments(Long lastId, int size) {
		return paymentRepository.findAttentionPayments(lastId, size);
	}

	public AttentionPaymentStatisticsResponse findAttentionPaymentStatistics() {
		return paymentRepository.findAttentionPaymentStatistics(LocalDateTime.now().minusMinutes(10));
	}
}
