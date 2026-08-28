package pocketpaystore.pocketpay_admin.payment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AttentionPaymentStatisticsResponse {
	private Long totalCount;
	private Long failedCount;
	private Long timeoutUnknownCount;
	private Long staleCount;
}
