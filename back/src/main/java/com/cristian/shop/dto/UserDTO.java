package com.cristian.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserDTO {

    private Long id;

    @NotEmpty(message = "Email required")
    @NotNull(message = "Email required")
    @Pattern(regexp = ".*(^\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$)", message = "Email not valid")
    private String email;

    @NotEmpty(message = "Password required")
    @Length(min = 7, max = 20, message = "Password min 8 and max 20")
    private String password;

    private String rol;
}
