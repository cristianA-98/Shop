package com.cristian.shop.dto;

import com.cristian.shop.Model.CartItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CartDTO {
    private Long id;
    private Integer items;
    private List<CartItem> cartItems;
    private Double total;
    private LocalTime createdAt;
}
