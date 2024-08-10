package com.cristian.shop.repository;

import com.cristian.shop.Model.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

public interface OrderSRepository extends JpaRepository<Orders, Long> {

    @Transactional
    @Modifying
    @Query(value = "update `orders` set `order_finish`=1, `status`=:status where id=:id", nativeQuery = true)
    void orderCancel(@Param("id") Long id, @Param("status") String status);

    @Transactional
    @Modifying
    @Query(value = "update orders set  status=:status where id=:id", nativeQuery = true)
    void changeStatusOrder(@Param("status") String status, @Param("id") Long id);

    @Transactional
    @Modifying
    @Query(value = "update orders set order_finish=1 where id=:id", nativeQuery = true)
    void orderFinish(@Param("id") Long id);

}
