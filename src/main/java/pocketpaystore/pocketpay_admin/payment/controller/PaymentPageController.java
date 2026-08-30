package pocketpaystore.pocketpay_admin.payment.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PaymentPageController {
	@GetMapping({"/", "/admin/payments"})
	public String payments() {
		return "payment/list";
	}

	@GetMapping("/admin/payments/attention")
	public String attentionPayments() {
		return "payment/attention";
	}

	@GetMapping("/admin/settlements")
	public String settlements() {
		return "settlement/list";
	}
}
