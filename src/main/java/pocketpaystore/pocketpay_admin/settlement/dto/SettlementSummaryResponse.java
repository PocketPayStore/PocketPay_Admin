package pocketpaystore.pocketpay_admin.settlement.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SettlementSummaryResponse {
	private Long id;
	private Long vendorId;
	private String vendorName;
	private LocalDate periodStart;
	private LocalDate periodEnd;
	private Long originalAmount;
	private Long pgFeeAmount;
	private Long platformFeeAmount;
	private Long finalAmount;
	private Long settlementCount;
	private String status;
	private LocalDateTime updatedAt;
}
