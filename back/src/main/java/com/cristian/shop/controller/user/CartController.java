package com.cristian.shop.controller.user;

import com.cristian.shop.dto.CartDTO;
import com.cristian.shop.dto.CartItemDTO;
import com.cristian.shop.service.CartService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/user/cart/")
@AllArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("")
    public ResponseEntity<Map<String, CartDTO>> getCart() {
        Map<String, CartDTO> body = new HashMap<>();
        body.put("Cart", cartService.getCart());
        return new ResponseEntity<>(body, HttpStatus.CREATED);
    }

    @PostMapping("addItem")
    public ResponseEntity<Map<String, String>> addItemCart(@Valid @RequestBody CartItemDTO cartItemDTO) {
        cartService.addCartItem(cartItemDTO);
        return new ResponseEntity<>(Map.of("Cart", "Item added to Cart"), HttpStatus.CREATED);
    }


    @DeleteMapping("deleteItemCart/{id}")
    public ResponseEntity<Map<String, String>> deleteItemCart(@PathVariable Long id) {
        cartService.deleteCartItem(id);
        return new ResponseEntity<>(Map.of("Cart", "Item delete to Cart"), HttpStatus.CREATED);
    }

    @PatchMapping("finishBuy/{id}")
    public ResponseEntity<Map<String, String>> finishBuyCart(@PathVariable("id") Long id) {
        cartService.finishBuy(id);
        return new ResponseEntity<>(Map.of("Cart", "Cart finish"), HttpStatus.CREATED);
    }

}
