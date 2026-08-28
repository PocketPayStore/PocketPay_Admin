package pocketpaystore.pocketpay_admin.common.exception.errorcode;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentErrorCode implements ErrorCode {
	PAYMENT_NOT_FOUND("PAYMENT_001", "존재하지 않는 결제입니다.", HttpStatus.NOT_FOUND);

	private final String code;
	private final String message;
	private final HttpStatus httpStatus;
}
