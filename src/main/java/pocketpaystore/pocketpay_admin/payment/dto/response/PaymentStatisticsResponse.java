package pocketpaystore.pocketpay_admin.payment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentStatisticsResponse {
	private Long totalCount;
	private Long todayCount;
	private Long todayAmount;
	private Long todayDoneCount;
	private Long todayAttentionCount;
}
