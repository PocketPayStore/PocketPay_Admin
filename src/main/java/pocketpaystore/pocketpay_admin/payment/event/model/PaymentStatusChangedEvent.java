package pocketpaystore.pocketpay_admin.payment.event.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pocketpaystore.pocketpay_admin.payment.domain.PaymentStatus;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentStatusChangedEvent {
	private String eventId;
	private Long paymentId;
	private Long orderId;
	private String orderNumber;
	private PaymentStatus status;
	private LocalDateTime updatedAt;
}
