package com.EmployeeMgtSystem.EmployeeService.service;

import com.EmployeeMgtSystem.EmployeeService.service.serviceImpl.EmployeeServiceImpl;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class EmployeeServiceImplIT {
    private final EmployeeServiceImpl test;

    @Autowired
    public EmployeeServiceImplIT(EmployeeServiceImpl test) {
        this.test = test;
    }

}
