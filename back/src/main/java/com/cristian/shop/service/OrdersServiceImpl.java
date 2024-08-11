package com.cristian.shop.service;


import com.cristian.shop.Model.CartItem;
import com.cristian.shop.Model.Orders;
import com.cristian.shop.Model.Product;
import com.cristian.shop.Model.User;
import com.cristian.shop.config.exceptionControll.ResponseException;
import com.cristian.shop.dto.CartDTO;
import com.cristian.shop.dto.InformationDTO;
import com.cristian.shop.dto.OrderDTO;
import com.cristian.shop.enum_.StatusOrder;
import com.cristian.shop.repository.InformationRespository;
import com.cristian.shop.repository.OrderSRepository;
import com.cristian.shop.repository.ProductRepository;
import com.cristian.shop.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OrdersServiceImpl {


    private final OrderSRepository orderSRepository;
    private final UserRepository userRepository;
    private final InformationRespository informationRespository;
    private final ProductRepository productRepository;
    private final ModelMapper mapper;

    //!   -----------  USER Response ---------

    public List<OrderDTO> allOrdersUser() {
        return orderSRepository.findAll().stream()
                .filter(orders -> orders.getUser().getId().equals(getUserContext().getId()))
                .map(this::toOrderDTO
                ).collect(Collectors.toList());

//                .map(order -> {
//                    return OrderDTO.builder()
//                            .id(order.getId())
//                            .cartDTO(mapper.map(order.getCart(), CartDTO.class))
//                            .informationDTO(mapper.map(order.getUser().getInformation(), InformationDTO.class))
//                            .email(order.getUser().getEmail())
//                            .status(order.getStatus())
//                            .orderFinish(order.getOrderFinish())
//                            .build();
//                }).collect(Collectors.toList());

    }

    public OrderDTO orderUser(Long id) {
        Orders order = customerOrder(id);
        return mapper.map(order, OrderDTO.class);
    }

    public void orderCancelUser(Long id) {

        Orders order = customerOrder(id);


        if (order.getStatus().equals(StatusOrder.IN_TRAVEL))
            throw new ResponseException("400", "Travel order, cannot be canceled", HttpStatus.BAD_REQUEST);

        cancelPreparingOrder(order);
//        if (order.getStatus().equals(StatusOrder.DELIVERED)) {
//            LocalDateTime thirtyDaysAfterDelivery = order.getCreatedAt().plusDays(30);
//            if (LocalDateTime.now().isBefore(thirtyDaysAfterDelivery)) {
//                restoreStock(order);
//                orderSRepository.orderCancel(id, StatusOrder.CANCELED.name());
//                return;
//            } else {
//                throw new ResponseException("404", "Order cannot be canceled after 30 days of delivery", HttpStatus.NOT_FOUND);
//            }
//        }

        cancelDeliveredOrderIfAllowed(order);
        throw new ResponseException("404", "Order can only be canceled while preparing or within 30 days of delivery", HttpStatus.NOT_FOUND);
    }

    //* ------------- USER METHOD  -------------

    private Orders customerOrder(Long id) {
        return orderSRepository.findById(id)
                .filter(orders -> orders.getUser().getId().equals(getUserContext().getId()) && !orders.getOrderFinish())
                .orElseThrow(() -> new ResponseException("404", "Order ID NOT FOUND", HttpStatus.NOT_FOUND));
    }

    private void cancelDeliveredOrderIfAllowed(Orders order) {
        LocalDateTime thirtyDaysAfterDelivery = order.getCreatedAt().plusDays(30);
        if (LocalDateTime.now().isBefore(thirtyDaysAfterDelivery) && order.getStatus().equals(StatusOrder.DELIVERED)) {
            restoreStock(order);
            orderSRepository.orderCancel(order.getId(), StatusOrder.CANCELED.name());
        } else {
            throw new ResponseException("404", "Order cannot be canceled after 30 days of delivery", HttpStatus.NOT_FOUND);
        }
    }

    private void cancelPreparingOrder(Orders order) {
        if (order.getStatus().equals(StatusOrder.PREPARING)) {
            orderSRepository.orderCancel(order.getId(), StatusOrder.CANCELED.name());
        }
    }
//!       -----------  ADMIN RESPONSE---------

    public OrderDTO order(Long id) {
        Orders order = existOrder(id);
        return mapper.map(order, OrderDTO.class);
    }

    public List<OrderDTO> allOrders() {
        return orderSRepository.findAll().stream()
                .map(this::toOrderDTO
                ).collect(Collectors.toList());
    }

    public void orderCancel(Long id) {
        Orders order = findInactiveOrder(id);
        validateOrderCancellation(order);
        restoreStock(order);
        orderSRepository.orderCancel(id, StatusOrder.CANCELED.toString());
    }

    public void orderFinish(Long id) {
        Orders order = validOrderToFinish(id);
        orderSRepository.orderFinish(id);
    }

//    @Scheduled(cron = "0 0 0 * * ?")
//    private void finishOldOrders() {
//        List<Orders> orders = orderSRepository.findAll()
//                .stream().filter(this::isOlderThan30DaysAndDelivered
//                ).collect(Collectors.toList());
//
//        for (Orders order : orders) {
//            orderSRepository.orderFinish(order.getId());
//        }
//    }

    public void orderStatusChange(Long id) {
        Orders order = findInactiveOrder(id);

        if (order.getStatus().equals(StatusOrder.PREPARING)) {
            orderSRepository.changeStatusOrder(StatusOrder.IN_TRAVEL.toString(), order.getId());
            return;
        }

        if (order.getStatus().equals(StatusOrder.IN_TRAVEL)) {
            orderSRepository.changeStatusOrder(StatusOrder.DELIVERED.toString(), order.getId());
        }
    }


    private User getUserContext() {
        final String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).orElseThrow(
                () -> new ResponseException("505", "INTERNAL SERVER ERROR", HttpStatus.INTERNAL_SERVER_ERROR)
        );
    }


    //* ------------- ADMIN METHOD  -------------

    @Transactional
    private void restoreStock(Orders order) {
        Set<Long> productIds = order.getCart().getCartItems().stream().map(CartItem::getProductId).collect(Collectors.toSet());
        List<Product> products = productRepository.findAllById(productIds);
        Map<Long, Product> productMap = products.stream().collect(Collectors.toMap(Product::getId, product -> product));

        for (CartItem item : order.getCart().getCartItems()) {
            Product product = productMap.get(item.getProductId());

            if (Objects.isNull(product))
                throw new ResponseException("404", "Product not found", HttpStatus.NOT_FOUND);

            int newStock = product.getAmount() + item.getAmount();

            product.setAmount(newStock);
        }
        productRepository.saveAll(products);
    }

    private Orders existOrder(Long id) {
        return orderSRepository.findById(id)
                .orElseThrow(() -> new ResponseException("404", "Order ID NOT FOUND", HttpStatus.NOT_FOUND));

    }

    private OrderDTO toOrderDTO(Orders order) {

        return OrderDTO.builder()
                .id(order.getId())
                .cartDTO(mapper.map(order.getCart(), CartDTO.class))
                .informationDTO(mapper.map(order.getUser().getInformation(), InformationDTO.class))
                .email(order.getUser().getEmail())
                .status(order.getStatus())
                .orderFinish(order.getOrderFinish())
                .build();
    }

    private Orders findInactiveOrder(Long id) {
        return orderSRepository.findById(id)
                .filter(orders -> !orders.getOrderFinish())
                .orElseThrow(() -> new ResponseException("404", "Order ID NOT FOUND", HttpStatus.NOT_FOUND));
    }

    private void validateOrderCancellation(Orders order) {
        if (order.getStatus().equals(StatusOrder.PREPARING)) {
            return;
        }

        if (order.getStatus().equals(StatusOrder.IN_TRAVEL)) {
            throw new ResponseException("400", "Travel order, cannot be canceled", HttpStatus.BAD_REQUEST);
        }

        if (order.getStatus().equals(StatusOrder.DELIVERED)) {
            LocalDateTime thirtyDaysAfterDelivery = order.getCreatedAt().plusDays(30);
            if (LocalDateTime.now().isAfter(thirtyDaysAfterDelivery)) {
                throw new ResponseException("404", "Order cannot be canceled after 30 days of delivery", HttpStatus.NOT_FOUND);
            }
        }
    }

    private Orders validOrderToFinish(Long id) {
        return orderSRepository.findById(id)
                .filter(this::isOlderThan30DaysAndDelivered)
                .orElseThrow(() -> new ResponseException("404", "Order ID NOT FOUND", HttpStatus.NOT_FOUND));
    }

    private boolean isOlderThan30DaysAndDelivered(Orders order) {
        LocalDateTime now = LocalDateTime.now();
        if (order.getOrderFinish())
            throw new ResponseException("404", "Order already finished", HttpStatus.NOT_FOUND);
        if (order.getStatus().equals(StatusOrder.DELIVERED) && now.isAfter(order.getCreatedAt().plusDays(30)))
            return true;
        throw new ResponseException("404", "Order delivered, 30 days must pass to be able to carry out this action", HttpStatus.NOT_FOUND);
    }

}
