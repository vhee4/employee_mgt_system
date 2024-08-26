package com.EmployeeMgtSystem.AuthenticationServer.model;

import jakarta.persistence.*;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "permissions")
public class Permission extends BaseEntity<Integer> {

    @Column(unique = true, nullable = false)
    private String name;

    @Column(length = 500)
    private String description;
}
