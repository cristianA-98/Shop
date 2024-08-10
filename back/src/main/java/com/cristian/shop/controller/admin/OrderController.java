package com.cristian.shop.controller.admin;

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
@AllArgsConstructor
@RequestMapping("/api/v1/admin/order/")
public class OrderController {

    private final OrdersServiceImpl ordersService;

    @GetMapping("{id}")
    public ResponseEntity<Map<String, OrderDTO>> order(@PathVariable("id") Long id) {
        Map<String, OrderDTO> body = new HashMap<>();
        body.put("order", ordersService.order(id));
        return new ResponseEntity<>(body, HttpStatus.ACCEPTED);
    }

    @GetMapping("")
    public ResponseEntity<Map<String, List<OrderDTO>>> allOrder() {
        Map<String, List<OrderDTO>> body = new HashMap<>();
        body.put("Orders", ordersService.allOrders());
        return new ResponseEntity<>(body, HttpStatus.ACCEPTED);
    }

    @GetMapping("cancel/{id}")
    public ResponseEntity<Map<String, String>> cancelOrder(@PathVariable("id") Long id) {
        ordersService.orderCancel(id);
        return new ResponseEntity<>(Map.of("Order", "Order canceled"), HttpStatus.ACCEPTED);
    }

    @GetMapping("changeStatus/{id}")
    public ResponseEntity<Map<String, String>> changeStatusOrder(@PathVariable("id") Long id) {
        ordersService.orderStatusChange(id);
        return new ResponseEntity<>(Map.of("Order", "Order change status"), HttpStatus.ACCEPTED);
    }

    @GetMapping("finish/{id}")
    public ResponseEntity<Map<String, String>> orderFinish(@PathVariable("id") Long id) {
        ordersService.orderFinish(id);
        return new ResponseEntity<>(Map.of("Order", "Order finish"), HttpStatus.ACCEPTED);
    }


}
