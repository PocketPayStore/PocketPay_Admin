package pocketpaystore.pocketpay_admin.sse.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.RequiredArgsConstructor;
import pocketpaystore.pocketpay_admin.sse.service.PaymentSseService;

@RestController
@RequestMapping("/api/payments/events")
@RequiredArgsConstructor
public class SseController {
	private final PaymentSseService paymentSseService;

	@GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public SseEmitter subscribe() {
		return paymentSseService.subscribe();
	}
}
