package pocketpaystore.pocketpay_admin.settlement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pocketpaystore.pocketpay_admin.settlement.domain.SettlementSummary;

public interface SettlementSummaryRepository extends JpaRepository<SettlementSummary, Long>, SettlementSummaryRepositoryCustom {
}
