package pocketpaystore.pocketpay_admin.payment.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import pocketpaystore.pocketpay_admin.common.exception.CustomException;
import pocketpaystore.pocketpay_admin.common.exception.errorcode.PaymentErrorCode;
import pocketpaystore.pocketpay_admin.payment.dto.request.PaymentSearchCondition;
import pocketpaystore.pocketpay_admin.payment.dto.response.PaymentDetailResponse;
import pocketpaystore.pocketpay_admin.payment.dto.response.PaymentStatusHistoryResponse;
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
}
