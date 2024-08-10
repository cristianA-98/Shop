package com.cristian.shop.service;

import com.cristian.shop.Model.*;
import com.cristian.shop.config.exceptionControll.ResponseException;
import com.cristian.shop.dto.CartDTO;
import com.cristian.shop.dto.CartItemDTO;
import com.cristian.shop.enum_.StatusOrder;
import com.cristian.shop.repository.CartRepository;
import com.cristian.shop.repository.OrderSRepository;
import com.cristian.shop.repository.ProductRepository;
import com.cristian.shop.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderSRepository orderSRepository;
    private final ModelMapper mapper;

    public void addCartItem(CartItemDTO cartItemDTO) {

        //check product exist
        Product product = productRepository.findById(cartItemDTO.getProductId())
                .orElseThrow(() -> new ResponseException("404", "Product Not found", HttpStatus.NOT_FOUND));

        if (product.getAmount() < 0)
            throw new ResponseException("404", "Product out of stock", HttpStatus.NOT_FOUND);

        if (!Objects.equals(product.getPrice(), cartItemDTO.getPrice()))
            throw new ResponseException("404", "Invalid Price", HttpStatus.NOT_FOUND);

        //check cart finish buy.
        Cart activeCart = activeCart();

        // create cart
        if (Objects.isNull(activeCart)) {

            Cart cart = Cart.builder()
                    .cartItems(List.of(mapper.map(cartItemDTO, CartItem.class)))
                    .user(getUserContext())
                    .total(cartItemDTO.getPrice())
                    .items(1)
                    .isFinishBuy(false)
                    .build();
            cartRepository.save(cart);
            return;
        }

        // add item in cart
        activeCart.getCartItems().add(mapper.map(cartItemDTO, CartItem.class));
        activeCart.setTotal(activeCart.getCartItems().stream().mapToDouble(CartItem::getPrice).sum());
        activeCart.setItems(activeCart().getItems() + 1);

        cartRepository.save(activeCart);
    }

    public void deleteCartItem(Long id) {
        Cart activeCart = activeCart();

        // create cart
        if (Objects.isNull(activeCart))
            throw new ResponseException("404", "No active cart found", HttpStatus.NOT_FOUND);

        CartItem cartItem = activeCart.getCartItems().stream()
                .filter((x) -> Objects.equals(x.getId(), id)).findFirst()
                .orElse(null);

        if (Objects.isNull(cartItem))
            throw new ResponseException("404", "Product ID not found in the active Cart", HttpStatus.NOT_FOUND);

//        List<CartItem> cartList = activeCart.getCartItems().stream().filter(x -> x.getId() != id).collect(Collectors.toList());
//        activeCart.setCartItems(cartList);

        activeCart.setCartItems(
                activeCart.getCartItems().stream().
                        filter(x -> !Objects.equals(x.getId(), id)).
                        collect(Collectors.toList()));

        activeCart.setTotal(activeCart.getTotal() - cartItem.getPrice());
        activeCart.setItems(activeCart().getItems() - 1);
        cartRepository.save(activeCart);

    }

    public CartDTO getCart() {
        Cart activeCart = activeCart();
        if (Objects.isNull(activeCart))
            throw new ResponseException("404", "No active cart found", HttpStatus.NOT_FOUND);

        return mapper.map(activeCart, CartDTO.class);
    }

    public void finishBuy(Long id) {
        User user = getUserContext();
        Cart activeCart = activeCart();

        if (Objects.isNull(user.getInformation()))
            throw new ResponseException("404", "Information missing for shipping", HttpStatus.NOT_FOUND);

        if (Objects.isNull(activeCart))
            throw new ResponseException("404", "No active cart found", HttpStatus.NOT_FOUND);

        if (!Objects.equals(activeCart.getId(), id))
            throw new ResponseException("404", "Id Cart not found", HttpStatus.NOT_FOUND);

        cartRepository.finishBuy(id);

        Orders order = Orders.builder()
                .cart(activeCart)
                .orderFinish(false)
                .status(StatusOrder.PREPARING)
                .user(user)
                .build();

        orderSRepository.save(order);
    }

    private Cart activeCart() {
        return cartRepository.findAll()
                .stream()
                .filter(cart -> cart.getUser().getId().equals(getUserContext().getId()))
                .filter(cart -> !cart.getIsFinishBuy())
                .findFirst()
                .orElse(null);
    }

    //Extract email del contextHolder and find user
    private User getUserContext() {
        final String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).orElseThrow(
                () -> new ResponseException("505", "INTERNAL SERVER ERROR", HttpStatus.INTERNAL_SERVER_ERROR)
        );
    }


}
