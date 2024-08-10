package com.cristian.shop.service;


import com.cristian.shop.Model.Orders;
import com.cristian.shop.Model.User;
import com.cristian.shop.config.exceptionControll.ResponseException;
import com.cristian.shop.dto.CartDTO;
import com.cristian.shop.dto.InformationDTO;
import com.cristian.shop.dto.OrderDTO;
import com.cristian.shop.enum_.StatusOrder;
import com.cristian.shop.repository.InformationRespository;
import com.cristian.shop.repository.OrderSRepository;
import com.cristian.shop.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OrdersServiceImpl {


    private final OrderSRepository orderSRepository;
    private final UserRepository userRepository;
    private final InformationRespository informationRespository;
    private final ModelMapper mapper;

    //   -----------  USER ---------

    public List<OrderDTO> allOrdersUser() {
        return orderSRepository.findAll().stream()
                .filter(orders -> orders.getUser().getId().equals(getUserContext().getId()))
                .map(order -> {
                    return OrderDTO.builder()
                            .id(order.getId())
                            .cartDTO(mapper.map(order.getCart(), CartDTO.class))
                            .informationDTO(mapper.map(order.getUser().getInformation(), InformationDTO.class))
                            .email(order.getUser().getEmail())
                            .status(order.getStatus())
                            .orderFinish(order.getOrderFinish())
                            .build();
                }).collect(Collectors.toList());
    }

    public OrderDTO orderUser(Long id) {
        Orders order = orderSRepository.findById(id)
                .filter(orders -> orders.getUser().getId().equals(getUserContext().getId()))
                .orElseThrow(() -> new ResponseException("404", "Order ID NOT FOUND", HttpStatus.NOT_FOUND));

        return mapper.map(order, OrderDTO.class);
    }

    public void orderCancelUser(Long id) {

        Orders order = orderSRepository.findById(id)
                .filter(orders -> orders.getUser().getId().equals(getUserContext().getId()) && !orders.getOrderFinish())
                .orElseThrow(() -> new ResponseException("404", "Order ID NOT FOUND", HttpStatus.NOT_FOUND));

        if (order.getStatus().equals(StatusOrder.PREPARING)) {
            orderSRepository.orderCancel(id, StatusOrder.CANCELED.toString());
            return;
        }

        if (order.getStatus().equals(StatusOrder.IN_TRAVEL))
            throw new ResponseException("400", "Travel order, cannot be canceled", HttpStatus.BAD_REQUEST);

        if (order.getStatus().equals(StatusOrder.DELIVERED)) {
            LocalDateTime thirtyDaysAfterDelivery = order.getCreatedAt().plusDays(30);
            if (LocalDateTime.now().isBefore(thirtyDaysAfterDelivery)) {
                orderSRepository.orderCancel(id, StatusOrder.CANCELED.toString());
                return;
            } else {
                throw new ResponseException("404", "Order cannot be canceled after 30 days of delivery", HttpStatus.NOT_FOUND);
            }
        }


        throw new ResponseException("404", "Order can only be canceled while preparing or within 30 days of delivery", HttpStatus.NOT_FOUND);
    }

//       -----------  ADMIN ---------

    public OrderDTO order(Long id) {
        Orders order = orderSRepository.findById(id)
                .orElseThrow(() -> new ResponseException("404", "Order ID NOT FOUND", HttpStatus.NOT_FOUND));

        return mapper.map(order, OrderDTO.class);
    }

    public List<OrderDTO> allOrders() {
        return orderSRepository.findAll().stream()
                .map(order -> {
                    return OrderDTO.builder()
                            .id(order.getId())
                            .cartDTO(mapper.map(order.getCart(), CartDTO.class))
                            .informationDTO(mapper.map(order.getUser().getInformation(), InformationDTO.class))
                            .email(order.getUser().getEmail())
                            .status(order.getStatus())
                            .orderFinish(order.getOrderFinish())
                            .build();
                }).collect(Collectors.toList());
    }

    public void orderCancel(Long id) {
        Orders order = orderSRepository.findById(id)
                .filter(orders -> !orders.getOrderFinish())
                .orElseThrow(() -> new ResponseException("404", "Order ID NOT FOUND", HttpStatus.NOT_FOUND));

        if (order.getStatus().equals(StatusOrder.IN_TRAVEL))
            throw new ResponseException("200", "Travel order, cannot be canceled", HttpStatus.OK);

        if (order.getStatus().equals(StatusOrder.DELIVERED))
            throw new ResponseException("200", "Delivered order, cannot be canceled", HttpStatus.OK);

        orderSRepository.orderCancel(id, StatusOrder.CANCELED.toString());
    }

    public void orderFinish(Long id) {
        Orders order = orderSRepository.findById(id)
                .filter(this::isOlderThan30DaysAndDelivered)
                .orElseThrow(() -> new ResponseException("404", "Order ID NOT FOUND", HttpStatus.NOT_FOUND));
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
        Orders order = orderSRepository.findById(id)
                .filter(orders -> !orders.getOrderFinish())
                .orElseThrow(() -> new ResponseException("404", "Order ID NOT FOUND", HttpStatus.NOT_FOUND));

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

    private boolean isOlderThan30DaysAndDelivered(Orders order) {
        LocalDateTime now = LocalDateTime.now();
        if (order.getOrderFinish())
            throw new ResponseException("404", "Order already finished", HttpStatus.NOT_FOUND);
        if (order.getStatus().equals(StatusOrder.DELIVERED) && now.isAfter(order.getCreatedAt().plusDays(30)))
            return true;
        throw new ResponseException("404", "Order delivered, 30 days must pass to be able to carry out this action", HttpStatus.NOT_FOUND);
    }
}
