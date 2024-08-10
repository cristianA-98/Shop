package com.cristian.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CartItemDTO {
    private Long id;
    private Long productId;
    private String name;
    private String description;
    private String category;
    private Integer amount;
    private Double price;
    private String waist;
    private String img;
}
