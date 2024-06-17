package com.shop.entity;

import com.shop.constant.OrderStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Setter@Getter
@ToString(exclude = "oderItems")
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "order" ,cascade = CascadeType.ALL
            ,orphanRemoval = true, fetch = FetchType.LAZY)  //외래키 설정 하지않는다.
    private List<OrderItem> oderItems = new ArrayList<>();

    private LocalDateTime orderDate;   //주문일

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    public void addOrderItem(OrderItem orderItem) {
        oderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public static Order createOrder(Member member, List<OrderItem> orderItems) {

        Order order = new Order();

        order.setMember(member);  //주문자

        for(OrderItem orderItem : orderItems) {  //주문 상품 목록
            order.addOrderItem(orderItem);
        }

        order.setOrderDate(LocalDateTime.now());  //주문 시간
        order.setOrderStatus(OrderStatus.ORDER); //주문 상태

        return order;
    }

    //총 주문 금액
    public int getTotalPrice(){
        int totalPrice = 0;

        for(OrderItem orderItem : oderItems) {
            totalPrice += orderItem.getTotalPrice();
        }

        return totalPrice;
    }

    //주문 상태 취소로 변경, 주문 수량을 상품 재고 더해주기
    public void cancelOrder(){
        this.orderStatus = OrderStatus.CANCEL;

        for(OrderItem orderItem : oderItems) {
            orderItem.cancel();
        }
    }

}


















