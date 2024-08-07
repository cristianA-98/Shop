package com.cristian.shop.repository;

import com.cristian.shop.Model.Information;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InformationRespository extends JpaRepository<Information, Long> {
}
