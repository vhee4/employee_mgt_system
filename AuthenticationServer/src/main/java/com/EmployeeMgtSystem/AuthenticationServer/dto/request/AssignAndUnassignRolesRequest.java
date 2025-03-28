package com.EmployeeMgtSystem.AuthenticationServer.dto.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssignAndUnassignRolesRequest {
    //TODO: validate these fields
    private int roleId;
    private List<Integer> permissionIds;
}
