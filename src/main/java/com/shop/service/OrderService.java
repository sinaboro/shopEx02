package com.shop.service;

import com.shop.dto.OrderDto;
import com.shop.dto.OrderHistDto;
import com.shop.dto.OrderItemDto;
import com.shop.entity.*;
import com.shop.repository.ItemImgRepository;
import com.shop.repository.ItemRepository;
import com.shop.repository.MemberRepository;
import com.shop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class OrderService {

    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final ItemImgRepository itemImgRepository;

    //장바구니 주문
     /*
       1. 이메일을 통해 Member 객체를 조회
       2. 각 OrderDto 객체를 순회하면서, Item 객체를 조회하고 OrderItem 객체를 생성하여 리스트에 추가
       3. OrderItem 리스트와 Member 객체를 사용하여 Order 객체를 생성
       4. 생성된 Order 객체를 DB 저장하고, 생성된 주문의 ID를 반환
     */
    public Long orders(List<OrderDto> orderDtoList, String email){

        Member member = memberRepository.findByEmail(email);
        List<OrderItem> orderItemList = new ArrayList<>();

        for (OrderDto orderDto : orderDtoList) {
            Item item = itemRepository.findById(orderDto.getItemId())
                    .orElseThrow(EntityNotFoundException::new);

            OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());
            orderItemList.add(orderItem);
        }

        Order order = Order.createOrder(member, orderItemList);
        orderRepository.save(order);

        return order.getId();
    }

    public Long order(OrderDto orderDto, String email) {

//        Optional<Item> results = itemRepository.findById(orderDto.getItemId());
//        Item item = results.orElseThrow(() -> new EntityNotFoundException());

        Item item = itemRepository.findById(orderDto.getItemId())
                .orElseThrow(EntityNotFoundException::new);

        Member member = memberRepository.findByEmail(email);
        log.info("member =====> : " + member);
        List<OrderItem> orderItemList = new ArrayList<>();

        OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());

        orderItemList.add(orderItem);

        Order order = Order.createOrder(member, orderItemList);

        orderRepository.save(order);

        return order.getId();
    }

    @Transactional(readOnly = true)
    public Page<OrderHistDto> getOrderList(String email, Pageable pageable) {

        //주문 목록 조회(유저당)
        List<Order> orders = orderRepository.findOrders(email, pageable); //개인당 구매 리스트
        Long totalCount = orderRepository.countOrder(email);  // 개인당 총구매 횟수

        List<OrderHistDto> orderHistDtoList = new ArrayList<>();

        for (Order order : orders) {
            OrderHistDto orderHistDto = new OrderHistDto(order);

            List<OrderItem> oderItems = order.getOderItems();
            for (OrderItem oderItem : oderItems) {
                ItemImg itemImg = itemImgRepository.findByItemIdAndRepimgYn(oderItem.getItem().getId(), "Y");

                OrderItemDto orderItemDto = new OrderItemDto(oderItem, itemImg.getImgUrl());

                orderHistDto.addOrderItemDto(orderItemDto);
            }

            orderHistDtoList.add(orderHistDto);
        }

        return new PageImpl<>(orderHistDtoList, pageable, totalCount);
    }


    //현재 로그인 사용자와  구매이력있는 구매자와 같은 경우 true, 아니면 false
    @Transactional(readOnly = true)
    public boolean validateOrder(Long orderId, String email) {

        Member curMember = memberRepository.findByEmail(email);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);

        Member savedMember = order.getMember();

        if(!StringUtils.equals(curMember.getEmail(), savedMember.getEmail())){
            return false;
        }
        return true;
    }

    //취소 로직
    public void cancelOrder(Long orderId){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);

        order.cancelOrder();
    }



}














