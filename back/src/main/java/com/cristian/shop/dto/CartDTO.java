package com.cristian.shop.dto;

import com.cristian.shop.Model.CartItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CartDTO {
    private Long id;
    private Integer items;
    private Set<CartItem> cartItems;
    private Double total;
    private LocalTime createdAt;
}
