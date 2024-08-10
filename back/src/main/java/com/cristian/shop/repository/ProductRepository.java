package com.cristian.shop.repository;

import com.cristian.shop.Model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Modifying
    @Query(value = "update `product` set `name`=:name,`description`=:description,`category`=:category,`amount`=:amount,`price`=:price,`waist`=:waist,`img`=:img, `admin_id`=:admin_id where `id`=:id  ", nativeQuery = true)
    void patch(@Param("name") String name, @Param("description") String description, @Param("category") String category, @Param("amount") Integer amount, @Param("price") Double price, @Param("waist") String waist, @Param("img") String img, @Param("admin_id") Long admin, @Param("id") Long id);

//    @Modifying
//    @Query(value = "update  `product` set  `name`=:name where  `id`=:id ", nativeQuery = true)
//    void patch(@Param("name") String name, @Param("id") Long id);

}
