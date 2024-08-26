package com.EmployeeMgtSystem.EmployeeService.utils;

import com.EmployeeMgtSystem.EmployeeService.model.Employee;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
@Component
public class EmailUtils {
    public String welcomeMessage(Employee employee, String temporaryPassword) {

        return String.format(
                "Dear %s %s,%n%n" +
                        "Welcome to our company! Your account has been created successfully.%n%n" +
                        "Here are your login details:%n" +
                        "Email: %s%n" +
                        "Temporary Password: %s%n%n" +
                        "Please log in to your account and change your temporary password as soon as possible.%n%n" +
                        "Best regards,%n" +
                        "Company"
                , employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail(), temporaryPassword);
    }

}
