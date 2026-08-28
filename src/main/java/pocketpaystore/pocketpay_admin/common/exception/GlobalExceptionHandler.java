package pocketpaystore.pocketpay_admin.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import pocketpaystore.pocketpay_admin.common.response.ErrorResponse;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(CustomException.class)
	public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
		return ResponseEntity
				.status(e.getErrorCode().getHttpStatus())
				.body(ErrorResponse.from(e.getErrorCode()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
		String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
		return ResponseEntity.badRequest()
				.body(ErrorResponse.builder().code("VALIDATION_ERROR").message(message).build());
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException e) {
		return ResponseEntity.badRequest()
				.body(ErrorResponse.builder().code("VALIDATION_ERROR").message(e.getMessage()).build());
	}

	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<ErrorResponse> handleNoResourceFound(NoResourceFoundException e) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(ErrorResponse.builder()
						.code("NOT_FOUND")
						.message("요청한 리소스를 찾을 수 없습니다.")
						.build());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleException(Exception e) {
		log.error("처리되지 않은 예외", e);
		return ResponseEntity.internalServerError()
				.body(ErrorResponse.builder()
						.code("INTERNAL_SERVER_ERROR")
						.message("서버 내부 오류가 발생했습니다.")
						.build());
	}
}
