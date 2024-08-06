package com.cristian.shop.mapper;

import com.cristian.shop.Model.User;
import com.cristian.shop.dto.UserDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toDto(User user);

    User toUser(UserDTO userDTO);
}
