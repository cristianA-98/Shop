package com.cristian.shop.controller.admin;


import com.cristian.shop.dto.ProductDto;
import com.cristian.shop.service.ProductServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/admin/product/")
public class ProductController {
    private final ProductServiceImpl productService;


    @GetMapping("{id}")
    public ResponseEntity<Map<String, ProductDto>> getProduct(@PathVariable Long id) {
        Map<String, ProductDto> body = new HashMap<>();
        body.put("Product", productService.getProduct(id));
        return new ResponseEntity<>(body, HttpStatus.ACCEPTED);
    }

    @GetMapping("")
    public ResponseEntity<Map<String, List<ProductDto>>> allProduct() {
        Map<String, List<ProductDto>> body = new HashMap<>();
        body.put("Products", productService.allProduct());
        return new ResponseEntity<>(body, HttpStatus.ACCEPTED);
    }


    @PostMapping("add")
    public ResponseEntity<Map<String, String>> addProduct(@Valid @RequestBody ProductDto productDto) {
        productService.addProduct(productDto);
        return new ResponseEntity<>(Map.of("Product", "Save..."), HttpStatus.CREATED);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Map<String, String>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return new ResponseEntity<>(Map.of("Product", "Delete..."), HttpStatus.ACCEPTED);
    }

    @PatchMapping("")
    public ResponseEntity<Map<String, String>> patchProduct(@Valid @RequestBody ProductDto productDto) {
        productService.patchProduct(productDto);
        return new ResponseEntity<>(Map.of("Product", "Patch"), HttpStatus.OK);
    }


}
