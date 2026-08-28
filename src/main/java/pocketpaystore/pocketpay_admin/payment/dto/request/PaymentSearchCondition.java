package pocketpaystore.pocketpay_admin.payment.dto.request;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import pocketpaystore.pocketpay_admin.payment.domain.PaymentStatus;

@Getter
@Setter
public class PaymentSearchCondition {
	@PositiveOrZero
	private Long lastId = 0L;

	@Min(1)
	@Max(100)
	private int size = 20;

	private PaymentStatus status;
	private String orderNumber;

	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private LocalDateTime from;

	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private LocalDateTime to;
}
