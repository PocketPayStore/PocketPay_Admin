package pocketpaystore.pocketpay_admin.common.response;

import lombok.Builder;
import lombok.Getter;
import pocketpaystore.pocketpay_admin.common.exception.errorcode.ErrorCode;

@Getter
@Builder
public class ErrorResponse {
	private final String code;
	private final String message;

	public static ErrorResponse from(ErrorCode errorCode) {
		return ErrorResponse.builder()
				.code(errorCode.getCode())
				.message(errorCode.getMessage())
				.build();
	}
}
