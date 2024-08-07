package com.cristian.shop.mapper;

import com.cristian.shop.Model.Product;
import com.cristian.shop.dto.ProductDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductManager {
    ProductDto toDto(Product product);

    Product toProduct(ProductDto productDto);
}
