package com.cristian.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ProductDto {

    private Long id;

    @NotEmpty(message = "Name is required")
    @NotNull(message = "Name is required")
    private String name;

    @NotEmpty(message = "Description is required")
    @NotNull(message = "Description is required")
    private String description;

    @NotEmpty(message = "Category is required")
    @NotNull(message = "Category is required")
    private String category;

    @Min(value = 1, message = "The value must be greater than 0")
    @NotNull(message = "Amount is required")
    private Integer amount;

    @Min(value = 1, message = "The value must be greater than 0")
    @NotNull(message = "Price is required")
    private Double price;

    @NotEmpty(message = "Waist is required")
    @NotNull(message = "Waist is required")
    private String waist;

    @NotEmpty(message = "Img is required")
    @NotNull(message = "Img is required")
    private String img;
}
