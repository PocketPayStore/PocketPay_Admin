package pocketpaystore.pocketpay_admin.payment.controller;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import pocketpaystore.pocketpay_admin.payment.dto.request.PaymentSearchCondition;
import pocketpaystore.pocketpay_admin.payment.dto.response.PaymentDetailResponse;
import pocketpaystore.pocketpay_admin.payment.dto.response.PaymentStatusHistoryResponse;
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

	@GetMapping("/{paymentId}")
	public PaymentDetailResponse findPayment(@PathVariable @Positive Long paymentId) {
		return paymentService.findPayment(paymentId);
	}

	@GetMapping("/{paymentId}/histories")
	public List<PaymentStatusHistoryResponse> findPaymentStatusHistories(@PathVariable @Positive Long paymentId) {
		return paymentService.findPaymentStatusHistories(paymentId);
	}
}
