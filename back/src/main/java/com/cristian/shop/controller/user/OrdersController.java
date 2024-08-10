package com.cristian.shop.controller.user;

import com.cristian.shop.dto.OrderDTO;
import com.cristian.shop.service.OrdersServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/user/order/")
@AllArgsConstructor
public class OrdersController {

    private final OrdersServiceImpl ordersService;

    @GetMapping("")
    public ResponseEntity<Map<String, List<OrderDTO>>> allOrders() {
        Map<String, List<OrderDTO>> body = new HashMap<>();
        body.put("Orders", ordersService.allOrdersUser());
        return new ResponseEntity<>(body, HttpStatus.ACCEPTED);
    }

    @GetMapping("{id}")
    public ResponseEntity<Map<String, String>> cancelOrder(@PathVariable("id") Long id) {
        ordersService.orderCancelUser(id);
        return new ResponseEntity<>(Map.of("Order", "Order canceled"), HttpStatus.ACCEPTED);
    }

}
