package pocketpaystore.pocketpay_admin.payment.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pocketpaystore.pocketpay_admin.payment.domain.PaymentStatus;

@Getter
@AllArgsConstructor
public class PaymentDetailResponse {
	private Long id;
	private Long orderId;
	private String orderNumber;
	private String paymentMethod;
	private String pgProvider;
	private String pgTransactionId;
	private String idempotencyKey;
	private Long amount;
	private Long usedPointAmount;
	private Long refundableAmount;
	private PaymentStatus status;
	private String failureCode;
	private String failureMessage;
	private LocalDateTime approvedAt;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
