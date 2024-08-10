package com.cristian.shop.dto;

import com.cristian.shop.enum_.StatusOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class OrderDTO {

    private Long id;
    private CartDTO cartDTO;
    private Boolean orderFinish;
    private InformationDTO informationDTO;
    private String email;

    @Enumerated(EnumType.STRING)
    private StatusOrder status;
}
