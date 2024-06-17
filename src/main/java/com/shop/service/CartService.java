package com.shop.service;

import com.shop.dto.CartDetailDto;
import com.shop.dto.CartItemDto;
import com.shop.dto.CartOrderDto;
import com.shop.dto.OrderDto;
import com.shop.entity.Cart;
import com.shop.entity.CartItem;
import com.shop.entity.Item;
import com.shop.entity.Member;
import com.shop.repository.CartItemRepository;
import com.shop.repository.CartRepository;
import com.shop.repository.ItemRepository;
import com.shop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class CartService {

    private final CartRepository cartRepository;
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final CartItemRepository cartItemRepository;

    private final OrderService orderService;

    public Long addCart(CartItemDto cartItemDto,  String email){
        Item item = itemRepository.findById(cartItemDto.getItemId())
                .orElseThrow(EntityNotFoundException::new);

        Member member = memberRepository.findByEmail(email);

        Cart cart = cartRepository.findByMemberId(member.getId());

        //카트가 없다면 카트 생성
        if(cart == null){
           cart = Cart.createCart(member);
           cartRepository.save(cart);
        }

        //장바구니 상품 존재 여부 ?
        CartItem savedCartItem =
                cartItemRepository.findByCartIdAndItemId( cart.getId(), item.getId());

        if(savedCartItem != null){
            savedCartItem.addCount(cartItemDto.getCount());
            return savedCartItem.getId();
        }else{
            CartItem cartItem =
                    CartItem.createCartItem(cart, item, cartItemDto.getCount());

            cartItemRepository.save(cartItem);

            return cartItem.getId();
        }

    }


    public List<CartDetailDto> getCartList(String email){

        /*
        List<CartDetailDto> cartDetailDtoList = new ArrayList<>();

        Member member = memberRepository.findByEmail(email);

        Cart cart = cartRepository.findByMemberId(member.getId());

        if(cart == null){
            return cartDetailDtoList;
        }


        cartDetailDtoList =
                cartItemRepository.findCartDetailDtoList(cart.getId());

        return cartDetailDtoList;
        */

        Member member = memberRepository.findByEmail(email);

        Cart cart = cartRepository.findByMemberId(member.getId());

        if(cart == null){
            return new ArrayList<>();
        }

        return cartItemRepository.findCartDetailDtoList(cart.getId());
    }
    
    
    //작성자만 장바구니 수량 변경 가능
    @Transactional(readOnly = true)
    public boolean validateCartItem(Long cartItemId, String email){
        Member curMember = memberRepository.findByEmail(email);

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);

        Member savedMember = cartItem.getCart().getMember();
        
        if(!StringUtils.equals(curMember, savedMember)){
            return false;
        }
        return true;
    }

    public void updateCartItemCount(Long cartItemId, int count){
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);

        cartItem.upateCount(count);   //터티체킹으로 저장
    }

    //장바구니 삭제
    public void deleteCartItem(Long cartItemId){
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);

        cartItemRepository.delete(cartItem);
    }

    /*
       1. 사용자가 장바구니에 담은 상품 리스트를 받아 각 상품의 정보를 OrderDto 객체로 변환
       2. 변환된 주문 항목 리스트를 orderService를 통해 주문을 생성
       3. 생성된 주문의 ID를 반환받은 후, 장바구니에서 해당 항목들을 삭제(주문했으면 더 이상 장바구니 담겨있을 필요성 없기때문)
       4. 생성된 주문의 ID를 반환
    */
    public Long orderCartItem(List<CartOrderDto> cartOrderDtoList, String email){
        List<OrderDto> orderDtoList = new ArrayList<>();

        //장바구니 있는 상품을 -> 주문(order)로 옮기는 과정
        for (CartOrderDto cartOrderDto : cartOrderDtoList) {
            CartItem cartItem = cartItemRepository
                    .findById(cartOrderDto.getCartItemId())
                    .orElseThrow(EntityNotFoundException::new);

            OrderDto orderDto = new OrderDto();
            orderDto.setItemId(cartItem.getItem().getId());
            orderDto.setCount(cartItem.getCount());
            orderDtoList.add(orderDto);
        }

        Long orderId = orderService.orders(orderDtoList, email);
        for (CartOrderDto cartOrderDto : cartOrderDtoList) {
            CartItem cartItem = cartItemRepository
                    .findById(cartOrderDto.getCartItemId())
                    .orElseThrow(EntityNotFoundException::new);
            cartItemRepository.delete(cartItem);
        }

        return orderId;
    }
}
