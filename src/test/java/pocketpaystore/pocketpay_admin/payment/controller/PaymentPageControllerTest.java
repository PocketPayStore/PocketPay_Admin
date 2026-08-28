package pocketpaystore.pocketpay_admin.payment.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PaymentPageController.class)
class PaymentPageControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Test
	void 결제_관리_화면을_반환한다() throws Exception {
		mockMvc.perform(get("/admin/payments"))
				.andExpect(status().isOk())
				.andExpect(view().name("payment/list"))
				.andExpect(content().string(containsString("PocketPay")))
				.andExpect(content().string(containsString("결제 관리")));
	}
}
