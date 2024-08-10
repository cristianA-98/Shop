package com.cristian.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class InformationDTO {
    private Long id;
    private String name;
    private String address;
    private Integer zip;
    private String phone;
}
