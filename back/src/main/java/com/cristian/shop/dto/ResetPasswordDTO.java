package com.cristian.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ResetPasswordDTO {


    @NotEmpty(message = "Password required")
    @Length(min = 7, max = 20, message = "Password min 7 and max 20")
    private String password;

    @NotEmpty(message = "Confirm password required")
    @Length(min = 7, max = 20, message = "Password min 7 and max 20")
    private String confirmPassword;

    // Custom validation method to ensure passwords match
    public boolean isPasswordsMatch() {
        return password != null && password.equals(confirmPassword);
    }
}
