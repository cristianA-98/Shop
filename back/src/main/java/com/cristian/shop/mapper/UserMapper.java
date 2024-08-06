package com.cristian.shop.mapper;

import com.cristian.shop.Model.UserEntity;
import com.cristian.shop.dto.UserDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toDto(UserEntity user);

    UserEntity toUser(UserDTO userDTO);
}
