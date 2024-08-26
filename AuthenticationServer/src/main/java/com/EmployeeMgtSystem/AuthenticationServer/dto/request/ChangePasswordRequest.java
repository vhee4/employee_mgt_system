package com.EmployeeMgtSystem.AuthenticationServer.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordRequest {
    @NotNull(message = "please pass in old password")
    @NotEmpty(message = "please pass in old password")
    private String oldPassword;
    @NotNull(message = "please pass in new password")
    @NotEmpty(message = "please pass in new password")
    private String newPassword;
    @NotNull(message = "please pass in email")
    @NotEmpty(message = "please pass in email")
    private String email;

}
