package pocketpaystore.pocketpay_admin.settlement.repository;

import java.util.List;
import pocketpaystore.pocketpay_admin.settlement.dto.SettlementSearchCondition;
import pocketpaystore.pocketpay_admin.settlement.dto.SettlementSummaryResponse;

public interface SettlementSummaryRepositoryCustom {
	List<SettlementSummaryResponse> search(SettlementSearchCondition condition);
}
