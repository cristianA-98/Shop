package com.cristian.shop.repository;

import com.cristian.shop.Model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    @Transactional
    @Modifying
    @Query(value = "update `cart` set `is_finish_buy`= 1 where `id`=:id", nativeQuery = true)
    void finishBuy(@Param("id") Long id);
}
