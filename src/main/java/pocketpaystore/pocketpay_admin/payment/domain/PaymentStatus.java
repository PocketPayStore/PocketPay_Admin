package pocketpaystore.pocketpay_admin.payment.domain;

public enum PaymentStatus {
	READY,
	IN_PROGRESS,
	DONE,
	FAILED,
	CANCELED,
	PARTIAL_CANCELED,
	TIMEOUT_UNKNOWN
}
