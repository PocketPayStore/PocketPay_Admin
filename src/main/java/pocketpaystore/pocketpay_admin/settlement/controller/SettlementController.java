package pocketpaystore.pocketpay_admin.settlement.controller;

import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import pocketpaystore.pocketpay_admin.settlement.dto.SettlementSearchCondition;
import pocketpaystore.pocketpay_admin.settlement.dto.SettlementSummaryResponse;
import pocketpaystore.pocketpay_admin.settlement.service.SettlementService;

@Validated
@RestController
@RequestMapping("/api/settlements")
@RequiredArgsConstructor
public class SettlementController {

	private final SettlementService settlementService;

	@GetMapping
	public List<SettlementSummaryResponse> findSettlements(@Valid @ModelAttribute SettlementSearchCondition condition) {
		return settlementService.findSettlements(condition);
	}
}
