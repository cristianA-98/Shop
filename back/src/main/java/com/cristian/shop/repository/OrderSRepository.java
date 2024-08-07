package com.cristian.shop.repository;

import com.cristian.shop.Model.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderSRepository extends JpaRepository<Orders, Long> {
}
