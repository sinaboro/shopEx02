package com.shop.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_item")
@Setter
@Getter
@ToString(exclude = "order")
public class OrderItem extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")  //외래키 설정
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    private int orderPrice; //가격
    private int count; //  수량

    public static OrderItem createOrderItem(Item item, int count) {
        OrderItem orderItem = new OrderItem();

        orderItem.setItem(item);
        orderItem.setCount(count);

        orderItem.setOrderPrice(item.getPrice());

        item.removeStock(count);

        return orderItem;
    }
    
    //해당 상품 총 가격
    public int getTotalPrice(){
        return orderPrice * count;
    }

    public void cancel(){
        this.getItem().addStock(count);
    }
}


















