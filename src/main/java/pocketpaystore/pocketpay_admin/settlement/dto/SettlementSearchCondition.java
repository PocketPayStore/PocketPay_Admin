package pocketpaystore.pocketpay_admin.settlement.dto;

import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;

@Getter
public class SettlementSearchCondition {
	@PositiveOrZero
	private final Long lastId;

	@Min(1) @Max(100)
	private final int size;

	private final String vendorName;

	private final String status;

	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private final LocalDate periodStart;

	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private final LocalDate periodEnd;

	public SettlementSearchCondition(Long lastId, Integer size, String vendorName, String status,
			LocalDate periodStart, LocalDate periodEnd) {
		this.lastId = lastId == null ? 0L : lastId;
		this.size = size == null ? 20 : size;
		this.vendorName = vendorName;
		this.status = status;
		this.periodStart = periodStart;
		this.periodEnd = periodEnd;
	}
}
