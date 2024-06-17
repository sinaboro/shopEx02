package com.shop.entity;

import com.shop.constant.ItemSellStatus;
import com.shop.dto.ItemFormDto;
import com.shop.exception.OutOfStockException;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Setter
@Getter
@ToString
@Entity
@Table(name = "item")
public class Item extends BaseEntity {

    @Id
    @Column(name = "item_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String itemNm;   //item_Nm

    @Column(name = "price", nullable = false)
    private  int price;   //price

    @Column(nullable = false)
    private  int stockNumber; //stoack_number

    @Lob
    @Column(nullable = false)
    private String itemDetail;

    @Enumerated(EnumType.STRING)
    private ItemSellStatus itemSellStatus;

    public void updateItem(ItemFormDto itemFormDto){
        this.itemNm = itemFormDto.getItemNm();
        this.price = itemFormDto.getPrice();
        this.stockNumber = itemFormDto.getStockNumber();
        this.itemDetail = itemFormDto.getItemDetail();
        this.itemSellStatus = itemFormDto.getItemSellStatus();
    }

    //상품 재고 수량 변경
    public void removeStock(int stockNumber){

        int restStock = this.stockNumber - stockNumber;

        if(restStock < 0){
            throw new OutOfStockException("상품 재고가 부족 합니다. (현재 재고 수량 :" + this.stockNumber + ")");
        }

        this.stockNumber = restStock;
    }

    //상품 취소하면 취소 숫자만큼 원상 복구
    public void addStock(int stockNumber){
        this.stockNumber += stockNumber;
    }

}
















