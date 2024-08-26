package com.EmployeeMgtSystem.AuthenticationServer.model;

import com.EmployeeMgtSystem.AuthenticationServer.enums.Status;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.LocalDateTime;

@Data
@ToString
@MappedSuperclass
@EnableJpaAuditing
@Getter
@Setter
public abstract class BaseEntity<T> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private T id;
    private String createdBy;
    private LocalDateTime createdTime = LocalDateTime.now();
    private String updatedBy;
    private LocalDateTime updatedTime = LocalDateTime.now();
    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;
}

