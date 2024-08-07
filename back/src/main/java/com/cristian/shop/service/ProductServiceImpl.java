package com.cristian.shop.service;

import com.cristian.shop.Model.Product;
import com.cristian.shop.Model.User;
import com.cristian.shop.config.exceptionControll.ResponseException;
import com.cristian.shop.dto.ProductDto;
import com.cristian.shop.mapper.ProductManager;
import com.cristian.shop.repository.ProductRepository;
import com.cristian.shop.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductServiceImpl {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ProductManager mapper;

    public ProductDto getProduct(Long id) {
        Product product = productRepository
                .findById(id)
                .orElseThrow(
                        () -> new ResponseException("404", "Product ID NOT FOUND", HttpStatus.NOT_FOUND)
                );
        System.out.println(product.getName());
        return mapper.toDto(product);
    }

    public List<ProductDto> allProduct() {
        List<ProductDto> products = productRepository.findAll().stream()
                .map(mapper::toDto).collect(Collectors.toList());
        return products;
    }

    @Transactional
    public void addProduct(ProductDto productDto) {
        Product product = mapper.toProduct(productDto);
        product.setAdmin(getAdminContext());
        productRepository.save(product);
    }

    private User getAdminContext() {
        final String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).orElseThrow(
                () -> new ResponseException("505", "INTERNAL SERVER ERROR", HttpStatus.INTERNAL_SERVER_ERROR)
        );
    }


}
