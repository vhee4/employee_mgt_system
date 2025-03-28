package com.EmployeeMgtSystem.AuthenticationServer.dto.request;

import com.EmployeeMgtSystem.AuthenticationServer.model.Permission;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateRoleRequest {
    @NotBlank(message = "At least one permission is required")
    private List<Integer> permissionIds;
    @NotBlank(message = "please specify a role name")
    private String name;

    private String description;
}
