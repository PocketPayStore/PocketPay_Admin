package pocketpaystore.pocketpay_admin.payment.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pocketpaystore.pocketpay_admin.payment.domain.PaymentStatus;

@Getter
@AllArgsConstructor
public class PaymentStatusHistoryResponse {
	private Long id;
	private PaymentStatus status;
	private LocalDateTime createdAt;
}
