package pocketpaystore.pocketpay_admin.settlement.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import pocketpaystore.pocketpay_admin.settlement.dto.SettlementSearchCondition;
import pocketpaystore.pocketpay_admin.settlement.dto.SettlementSummaryResponse;
import pocketpaystore.pocketpay_admin.settlement.repository.SettlementSummaryRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SettlementService {
	private final SettlementSummaryRepository repository;
	public List<SettlementSummaryResponse> findSettlements(SettlementSearchCondition c) {
		return repository.search(c);
	}
}
