package pocketpaystore.pocketpay_admin.payment.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pocketpaystore.pocketpay_admin.payment.domain.PaymentStatus;

@Getter
@AllArgsConstructor
public class AttentionPaymentResponse {
	private Long id;
	private String orderNumber;
	private PaymentStatus status;
	private Long amount;
	private String failureCode;
	private String failureMessage;
	private LocalDateTime updatedAt;
}
