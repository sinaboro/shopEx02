package com.shop.dto;

import com.shop.constant.OrderStatus;
import com.shop.entity.Order;
import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

//주문정보 담을 클래스
@Getter@Setter
public class OrderHistDto {

    private Long orderId;
    private String orderDate;
    private OrderStatus orderStatus;

    //주문 상품 리스트
    private List<OrderItemDto> orderItemDtoList = new ArrayList<OrderItemDto>();

    public OrderHistDto(Order order) {
        this.orderId = order.getId();
        this.orderDate = order.getOrderDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        this.orderStatus = order.getOrderStatus();
    }
    public void addOrderItemDto(OrderItemDto orderItemDto) {
        orderItemDtoList.add(orderItemDto);
    }


}
