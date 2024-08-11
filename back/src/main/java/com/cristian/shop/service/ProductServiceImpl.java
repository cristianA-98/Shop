package com.cristian.shop.service;

import com.cristian.shop.Model.Product;
import com.cristian.shop.Model.User;
import com.cristian.shop.config.exceptionControll.ResponseException;
import com.cristian.shop.dto.ProductDto;
import com.cristian.shop.repository.ProductRepository;
import com.cristian.shop.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
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
    private final ModelMapper mapper;

    public ProductDto getProduct(Long id) {
        Product product = existProduct(id);
        return mapper.map(product, ProductDto.class);
    }

    public List<ProductDto> allProduct() {
        List<ProductDto> products = productRepository.findAll().stream()
                .map(product -> mapper.map(product, ProductDto.class)).collect(Collectors.toList());

        if (products.isEmpty())
            throw new ResponseException("404", "No Products", HttpStatus.NOT_FOUND);

        return products;
    }


    public void addProduct(ProductDto productDto) {
        Product product = mapper.map(productDto, Product.class);
        product.setAdmin(getAdminContext());
        productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        existProduct(id);
        productRepository.deleteById(id);

    }

    @Transactional
    public void patchProduct(ProductDto productDto) {
        existProduct(productDto.getId());
        productRepository.patch(
                productDto.getName(),
                productDto.getDescription(),
                productDto.getCategory(),
                productDto.getAmount(),
                productDto.getPrice(),
                productDto.getWaist(),
                productDto.getImg(),
                getAdminContext().getId(),
                productDto.getId());


    }

    private Product existProduct(Long id) {
        return productRepository
                .findById(id)
                .orElseThrow(() -> new ResponseException("404", "Product ID NOT FOUND", HttpStatus.NOT_FOUND));
    }

    //Extract email del contextHolder and find user
    private User getAdminContext() {
        final String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).orElseThrow(
                () -> new ResponseException("505", "INTERNAL SERVER ERROR", HttpStatus.INTERNAL_SERVER_ERROR)
        );
    }


}
