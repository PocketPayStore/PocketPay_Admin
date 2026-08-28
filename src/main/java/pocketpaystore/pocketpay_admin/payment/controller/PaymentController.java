package pocketpaystore.pocketpay_admin.payment.controller;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import pocketpaystore.pocketpay_admin.payment.dto.request.PaymentSearchCondition;
import pocketpaystore.pocketpay_admin.payment.dto.response.AttentionPaymentResponse;
import pocketpaystore.pocketpay_admin.payment.dto.response.AttentionPaymentStatisticsResponse;
import pocketpaystore.pocketpay_admin.payment.dto.response.PaymentDetailResponse;
import pocketpaystore.pocketpay_admin.payment.dto.response.PaymentStatusHistoryResponse;
import pocketpaystore.pocketpay_admin.payment.dto.response.PaymentStatisticsResponse;
import pocketpaystore.pocketpay_admin.payment.dto.response.PaymentSummaryResponse;
import pocketpaystore.pocketpay_admin.payment.service.PaymentService;

@Validated
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
	private final PaymentService paymentService;

	@GetMapping
	public List<PaymentSummaryResponse> findPayments(@Valid @ModelAttribute PaymentSearchCondition condition) {
		return paymentService.findPayments(condition);
	}

	@GetMapping("/statistics")
	public PaymentStatisticsResponse findPaymentStatistics() {
		return paymentService.findPaymentStatistics();
	}

	@GetMapping("/attention")
	public List<AttentionPaymentResponse> findAttentionPayments(
			@RequestParam(defaultValue = "0") @PositiveOrZero Long lastId,
			@RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
		return paymentService.findAttentionPayments(lastId, size);
	}

	@GetMapping("/attention/statistics")
	public AttentionPaymentStatisticsResponse findAttentionPaymentStatistics() {
		return paymentService.findAttentionPaymentStatistics();
	}

	@GetMapping("/{paymentId}")
	public PaymentDetailResponse findPayment(@PathVariable @Positive Long paymentId) {
		return paymentService.findPayment(paymentId);
	}

	@GetMapping("/{paymentId}/histories")
	public List<PaymentStatusHistoryResponse> findPaymentStatusHistories(@PathVariable @Positive Long paymentId) {
		return paymentService.findPaymentStatusHistories(paymentId);
	}
}
