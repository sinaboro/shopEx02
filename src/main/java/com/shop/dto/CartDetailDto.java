package com.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Getter @Setter
@Log4j2
public class CartDetailDto {

    private Long cartItemId; //장바구니 상품 아이디  cart_item

    private String itemNm; //상품명   item,

    private int price; //상품 금액   item

    private int count; //수량    item

    private String imgUrl; //상품 이미지 경로  itemImg

    public CartDetailDto(Long cartItemId, String itemNm, int price, int count, String imgUrl){
        log.info("-------------CartDetailDto 생성자 --------------------");

        this.cartItemId = cartItemId;
        this.itemNm = itemNm;
        this.price = price;
        this.count = count;
        this.imgUrl = imgUrl;
    }

}