package com.kboticket.repository.order;

import com.kboticket.dto.order.OrderDto;
import com.kboticket.dto.order.OrderSearchDto;

import java.util.List;

public interface OrderCustomRepository {

    List<OrderDto> getByCursor(OrderSearchDto orderSearchDto, String cursor, int limit);
}
