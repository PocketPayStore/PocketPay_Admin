package pocketpaystore.pocketpay_admin.settlement.repository;

import static pocketpaystore.pocketpay_admin.settlement.domain.QSettlementSummary.settlementSummary;

import java.util.List;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import pocketpaystore.pocketpay_admin.settlement.dto.SettlementSearchCondition;
import pocketpaystore.pocketpay_admin.settlement.dto.SettlementSummaryResponse;

@RequiredArgsConstructor
public class SettlementSummaryRepositoryCustomImpl implements SettlementSummaryRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public List<SettlementSummaryResponse> search(SettlementSearchCondition condition) {
		return queryFactory.select(Projections.constructor(SettlementSummaryResponse.class,
				settlementSummary.id, settlementSummary.vendorId,
				settlementSummary.vendorName,
				settlementSummary.periodStart, settlementSummary.periodEnd, settlementSummary.originalAmount,
				settlementSummary.pgFeeAmount, settlementSummary.platformFeeAmount, settlementSummary.finalAmount,
				settlementSummary.settlementCount, Expressions.constant("SETTLED"), settlementSummary.updatedAt))
				.from(settlementSummary)
				.where(settlementSummary.id.gt(condition.getLastId()),
						vendorNameContains(condition.getVendorName()),
						periodStartGoe(condition), periodEndLoe(condition))
				.orderBy(settlementSummary.id.asc())
				.limit(condition.getSize() + 1)
				.fetch();
	}

	private BooleanExpression vendorNameContains(String vendorName) {
		return vendorName == null || vendorName.isBlank() ? null : settlementSummary.vendorName.containsIgnoreCase(vendorName);
	}

	private BooleanExpression periodStartGoe(SettlementSearchCondition condition) {
		return condition.getPeriodStart() == null ? null : settlementSummary.periodStart.goe(condition.getPeriodStart());
	}

	private BooleanExpression periodEndLoe(SettlementSearchCondition condition) {
		return condition.getPeriodEnd() == null ? null : settlementSummary.periodEnd.loe(condition.getPeriodEnd());
	}
}
