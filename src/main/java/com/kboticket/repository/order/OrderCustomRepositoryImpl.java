package com.kboticket.repository.order;

import com.kboticket.domain.QOrder;
import com.kboticket.domain.QOrderSeat;
import com.kboticket.dto.order.OrderDto;
import com.kboticket.dto.order.OrderSearchDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Repository
@AllArgsConstructor
public class OrderCustomRepositoryImpl implements OrderCustomRepository {

    private final JPAQueryFactory queryFactory;
    private static final QOrder order = QOrder.order;
    private static final QOrderSeat orderSeat = QOrderSeat.orderSeat;

    @Override
    public List<OrderDto> getByCursor(OrderSearchDto orderSearchDto, String cursor, int limit) {
        LocalDateTime now = LocalDateTime.now();

        String periodType = orderSearchDto.getPeriodType() != null ? orderSearchDto.getPeriodType() : "3";

        Integer year = orderSearchDto.getYear() != null ? orderSearchDto.getYear() : now.getYear();
        Integer month = orderSearchDto.getMonth() != null ? orderSearchDto.getMonth() : now.getMonthValue();

        BooleanExpression periodCondition = createPeriodCondition(periodType, now);
        BooleanExpression monthCondition = createMonthCondition(year, month);

        return  queryFactory
                .select(Projections.constructor(OrderDto.class,
                        order.id,
                        order.name,
                        order.game.gameDate,
                        order.orderDate,
                        order.status,
                        orderSeat.id.count().as("cnt")
                ))
                .from(order)
                .leftJoin(orderSeat).on(order.id.eq(orderSeat.order.id))
                .where(periodCondition.and(monthCondition)
                        .and(order.status.eq(orderSearchDto.getOrderStatus())))
                .groupBy(order.id, order.name, order.orderDate, order.status, order.game.gameDate)
                .fetch();
    }

    private BooleanExpression createPeriodCondition(String periodType, LocalDateTime now) {
        LocalDateTime startDateTime;
        LocalDateTime endDateTime = now;

        switch (periodType) {
            case "15":
                startDateTime = now.minusDays(15);
                break;
            case "1":
                startDateTime = now.minusMonths(1);
                break;
            case "2":
                startDateTime = now.minusMonths(2);
                break;
            case "3":
                startDateTime = now.minusMonths(3);
                break;
            default:
                startDateTime = now.minusMonths(3);
        }

        return order.orderDate.between(startDateTime, endDateTime);
    }

    private BooleanExpression createMonthCondition(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);

        LocalDateTime startDateTime = LocalDateTime.of(yearMonth.atDay(1), LocalDateTime.MIN.toLocalTime());
        LocalDateTime endDateTime = LocalDateTime.of(yearMonth.atEndOfMonth(), LocalDateTime.MAX.toLocalTime());

            return order.orderDate.between(startDateTime, endDateTime);
    }

}