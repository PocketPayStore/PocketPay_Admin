package pocketpaystore.pocketpay_admin.settlement.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Formula;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Immutable
@Table(name = "vendor_settlement_summary")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SettlementSummary {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "vendor_id", nullable = false)
	private Long vendorId;
	@Formula("(SELECT v.name FROM vendor v WHERE v.id = vendor_id)")
	private String vendorName;
	@Column(name = "period_start", nullable = false)
	private LocalDate periodStart;
	@Column(name = "period_end", nullable = false)
	private LocalDate periodEnd;
	@Column(name = "original_amount", nullable = false)
	private Long originalAmount;
	@Column(name = "pg_fee_amount", nullable = false)
	private Long pgFeeAmount;
	@Column(name = "platform_fee_amount", nullable = false)
	private Long platformFeeAmount;
	@Column(name = "final_amount", nullable = false)
	private Long finalAmount;
	@Column(name = "settlement_count", nullable = false)
	private Long settlementCount;
	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;
}
