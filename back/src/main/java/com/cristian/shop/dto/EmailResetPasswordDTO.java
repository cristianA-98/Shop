package com.cristian.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class EmailResetPasswordDTO {
    @NotEmpty(message = "Email required")
    @NotNull(message = "Email required")
    @Pattern(regexp = ".*(^\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$)", message = "Email not valid")
    private String email;
}
