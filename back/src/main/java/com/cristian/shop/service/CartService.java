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
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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
//        Product product = productRepository.findById(cartItemDTO.getProductId())
//                .orElseThrow(() -> new ResponseException("404", "Product Not found", HttpStatus.NOT_FOUND));
//
//        if (product.getAmount() < 0)
//            throw new ResponseException("404", "Product out of stock", HttpStatus.NOT_FOUND);
//
//        if (!Objects.equals(product.getPrice(), cartItemDTO.getPrice()))
//            throw new ResponseException("404", "Invalid Price", HttpStatus.NOT_FOUND);

        //check cart finish buy.
        Cart activeCart = activeCart();

        // create cart
//        if (Objects.isNull(activeCart)) {
//
//            Cart cart = Cart.builder()
//                    .cartItems(List.of(mapper.map(cartItemDTO, CartItem.class)))
//                    .user(getUserContext())
//                    .total(cartItemDTO.getPrice())
//                    .items(1)
//                    .isFinishBuy(false)
//                    .build();
//            cartRepository.save(cart);
//            return;
//        }

        if (Objects.isNull(activeCart)) {
            createNewCart(cartItemDTO);
            return;
        }


        // add item in cart
//        activeCart.getCartItems().add(mapper.map(cartItemDTO, CartItem.class));
//        activeCart.setTotal(activeCart.getCartItems().stream().mapToDouble(CartItem::getPrice).sum());
//        activeCart.setItems(activeCart().getItems() + 1);
//        cartRepository.save(activeCart);

        addItemOnCartActive(activeCart, cartItemDTO);
    }

    public void deleteCartItem(Long id) {
        Cart activeCart = activeCart();

        if (Objects.isNull(activeCart))
            throw new ResponseException("404", "No active cart found", HttpStatus.NOT_FOUND);


//
//                activeCart.getCartItems().stream()
//                .filter((x) -> Objects.equals(x.getId(), id)).findFirst()
//                .orElse(null);
//
//        if (Objects.isNull(cartItem))
//            throw new ResponseException("404", "Product ID not found in the active Cart", HttpStatus.NOT_FOUND);

//        List<CartItem> cartList = activeCart.getCartItems().stream().filter(x -> x.getId() != id).collect(Collectors.toList());
//        activeCart.setCartItems(cartList);

//        activeCart.setCartItems(
//                activeCart.getCartItems().stream().
//                        filter(x -> !Objects.equals(x.getId(), id)).
//                        collect(Collectors.toList()));
//
//        activeCart.setTotal(activeCart.getTotal() - cartItem.getPrice());
//        activeCart.setItems(activeCart().getItems() - 1);

        cartRepository.save(removeItemfromCart(activeCart, id));

    }

    public CartDTO getCart() {
        Cart activeCart = activeCart();
        if (Objects.isNull(activeCart))
            throw new ResponseException("404", "No active cart found", HttpStatus.NOT_FOUND);

        return mapper.map(activeCart, CartDTO.class);
    }

    public void finishBuy(Long id) {
        Cart activeCart = activeCart();


        if (Objects.isNull(activeCart))
            throw new ResponseException("404", "No active cart found", HttpStatus.NOT_FOUND);

        if (!Objects.equals(activeCart.getId(), id))
            throw new ResponseException("404", "Id Cart not found", HttpStatus.NOT_FOUND);

        cartRepository.finishBuy(id);

//        Orders order = Orders.builder()
//                .cart(activeCart)
//                .orderFinish(false)
//                .status(StatusOrder.PREPARING)
//                .user(user)
//                .build();
//
//        orderSRepository.save(order);
        createOrder(activeCart);
    }

    //!  ---------------------------------------------------------------------------

    //*  ---------------  finishBuy METHODS ---------------

    private void createOrder(Cart activeCart) {

        updateStock(activeCart);
        Orders order = Orders.builder()
                .cart(activeCart)
                .orderFinish(false)
                .status(StatusOrder.PREPARING)
                .user(getUserContext())
                .build();

        orderSRepository.save(order);

    }

    private void updateStock(Cart cart) {
        List<Long> productIds = cart.getCartItems().stream().map(CartItem::getProductId).collect(Collectors.toList());
        List<Product> products = productRepository.findAllById(productIds);
        Map<Long, Product> productMap = products.stream().collect(Collectors.toMap(Product::getId, product -> product));

        for (CartItem item : cart.getCartItems()) {
            Product product = productMap.get(item.getProductId());

            if (product == null)
                throw new ResponseException("404", "Product not found", HttpStatus.NOT_FOUND);

            int newStock = product.getAmount() - item.getAmount();
            if (newStock < 0)
                throw new ResponseException("400", "Insufficient stock for product: " + product.getName(), HttpStatus.BAD_REQUEST);
            product.setAmount(newStock);
        }
        productRepository.saveAll(products);
    }
    
    //*  ---------------  deleteCartItem METHODS ---------------

    private Cart removeItemfromCart(Cart activeCart, Long id) {
        CartItem cartItem = existItemInCart(activeCart, id);
        Set<CartItem> updatedCartItems = activeCart.getCartItems().stream()
                .filter(item -> !Objects.equals(item.getId(), id))
                .collect(Collectors.toSet());
        activeCart.setCartItems(updatedCartItems);
        activeCart.setTotal(activeCart.getTotal() - cartItem.getPrice());
        activeCart.setItems(updatedCartItems.size());

        return activeCart;
    }

    //*  ---------------  addCartItem METHODS ---------------

    private CartItem existItemInCart(Cart cart, Long id) {
        return cart.getCartItems().stream()
                .filter((x) -> Objects.equals(x.getId(), id)).findFirst()
                .orElseThrow(() -> new ResponseException("404", "Product ID not found in the active Cart", HttpStatus.NOT_FOUND));
    }

    private Product getProduct(CartItemDTO cartItemDTO) {
        Product product = productRepository.findById(cartItemDTO.getProductId())
                .orElseThrow(() -> new ResponseException("404", "Product Not found", HttpStatus.NOT_FOUND));
        checkProduct(product, cartItemDTO);
        return product;
    }

    private void checkProduct(Product product, CartItemDTO cartItemDTO) {
        if (product.getAmount() < 0)
            throw new ResponseException("404", "Product out of stock", HttpStatus.NOT_FOUND);
        if (!Objects.equals(product.getPrice(), cartItemDTO.getPrice()))
            throw new ResponseException("404", "Product Invalid Price", HttpStatus.NOT_FOUND);

        if (!Objects.equals(product.getName(), cartItemDTO.getProductId()))
            throw new ResponseException("404", "Product Invalid Name", HttpStatus.NOT_FOUND);

        if (!Objects.equals(product.getCategory(), cartItemDTO.getCategory()))
            throw new ResponseException("404", "Product Invalid Name", HttpStatus.NOT_FOUND);

        if (!Objects.equals(product.getDescription(), cartItemDTO.getDescription()))
            throw new ResponseException("404", "Product Invalid Description", HttpStatus.NOT_FOUND);

        if (!Objects.equals(product.getWaist(), cartItemDTO.getWaist()))
            throw new ResponseException("404", "Product Invalid Waist", HttpStatus.NOT_FOUND);

        if (!Objects.equals(product.getImg(), cartItemDTO.getImg()))
            throw new ResponseException("404", "Product Invalid Img", HttpStatus.NOT_FOUND);

    }


    private void addItemOnCartActive(Cart activeCart, CartItemDTO cartItemDTO) {


        CartItem existingItem = activeCart.getCartItems().stream()
                .filter(item -> item.getProductId().equals(cartItemDTO.getProductId()))
                .findFirst()
                .orElse(null);

        if (Objects.isNull(existingItem)) {
            CartItem newItem = mapper.map(cartItemDTO, CartItem.class);
            activeCart.getCartItems().add(newItem);
        } else {
            existingItem.setAmount(existingItem.getAmount() + cartItemDTO.getAmount());
        }

//        activeCart.getCartItems().add(mapper.map(cartItemDTO, CartItem.class));
//        activeCart.setTotal(activeCart.getCartItems().stream().mapToDouble(CartItem::getPrice).sum());
//        activeCart.setItems(activeCart().getItems() + 1);
//        cartRepository.save(activeCart);

        activeCart.setTotal(activeCart.getCartItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getAmount())
                .sum());
        activeCart.setItems(activeCart.getCartItems().size());

        cartRepository.save(activeCart);
    }

    private void createNewCart(CartItemDTO cartItemDTO) {
        getProduct(cartItemDTO);

        Cart cart = Cart.builder()
                .cartItems(Set.of(mapper.map(cartItemDTO, CartItem.class)))
                .user(getUserContext())
                .total(cartItemDTO.getPrice())
                .items(1)
                .isFinishBuy(false)
                .build();
        cartRepository.save(cart);
    }

    //!  ---------------------------------------------------------------------------

    private Cart activeCart() {
        return cartRepository.findAll()
                .stream()
                .filter(cart -> cart.getUser().getId().equals(getUserContext().getId()))
                .filter(cart -> !cart.getIsFinishBuy())
                .findFirst()
                .orElse(null);
    }

    //? Extract email del contextHolder and find user
    private User getUserContext() {
        final String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .filter(this::addressExists)
                .orElseThrow(() -> new ResponseException("505", "INTERNAL SERVER ERROR", HttpStatus.INTERNAL_SERVER_ERROR));
    }


    private boolean addressExists(User user) {
        if (Objects.isNull(user.getInformation()))
            throw new ResponseException("404", "Information missing for shipping", HttpStatus.NOT_FOUND);
        if (Objects.isNull(user.getInformation().getAddress()))
            throw new ResponseException("404", "Information missing for shipping", HttpStatus.NOT_FOUND);
        if (Objects.isNull(user.getInformation().getZip()))
            throw new ResponseException("404", "Information missing for shipping", HttpStatus.NOT_FOUND);
        if (Objects.isNull(user.getInformation().getPhone()))
            throw new ResponseException("404", "Information missing for shipping", HttpStatus.NOT_FOUND);
        if (Objects.isNull(user.getInformation().getName()))
            throw new ResponseException("404", "Information missing for shipping", HttpStatus.NOT_FOUND);

        return true;
    }
}
