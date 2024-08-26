//package com.EmployeeMgtSystem.AuthenticationServer.model;
//
//import com.EmployeeMgtSystem.AuthenticationServer.enums.Status;
//import com.EmployeeMgtSystem.AuthenticationServer.repository.PermissionRepository;
//import com.EmployeeMgtSystem.AuthenticationServer.repository.RoleRepository;
//import com.EmployeeMgtSystem.AuthenticationServer.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDateTime;
//import java.util.Optional;
//import java.util.Set;
//
//@Component
//public class DataLoader implements CommandLineRunner {
//
//    @Autowired
//    private RoleRepository roleRepository;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private PermissionRepository permissionRepository;
//
//    @Override
//    public void run(String... args) throws Exception {
//        // Create default permissions
//        Permission readUsers = createPermissionIfNotFound("READ_USERS", "Can read all users");
//        Permission readUser = createPermissionIfNotFound("READ_USER", "Can read a user");
//        Permission editUser = createPermissionIfNotFound("EDIT_USER", "Can update user details");
//        Permission deleteUser = createPermissionIfNotFound("DELETE_USER", "Can delete a user");
//        Permission createUser = createPermissionIfNotFound("CREATE_USER", "Can create a user");
//        Permission readEmployees = createPermissionIfNotFound("READ_EMPLOYEES", "Can read all employees");
//        Permission readEmployee = createPermissionIfNotFound("READ_EMPLOYEE", "Can read an employee");
//        Permission createEmployee = createPermissionIfNotFound("CREATE_EMPLOYEE", "Can create an employee");
//        Permission readEmployeeByDept = createPermissionIfNotFound("READ_EMPLOYEE_BY_DEPT", "Can read all employees by department");
//        Permission editEmployee = createPermissionIfNotFound("EDIT_EMPLOYEE", "Can update employee details");
//        Permission deleteEmployee = createPermissionIfNotFound("DELETE_EMPLOYEE", "Can delete an employee");
//        Permission inviteUsers = createPermissionIfNotFound("INVITE_USERS", "Can invite users");
//        Permission createDept = createPermissionIfNotFound("CREATE_DEPT", "Can create department");
//        Permission editDept = createPermissionIfNotFound("EDIT_DEPT", "Can update department details");
//        Permission readDept = createPermissionIfNotFound("READ_DEPT", "Can read a department");
//        Permission readDepts = createPermissionIfNotFound("READ_DEPTS", "Can read departments");
//
//
//        Set<Permission> commonPermissions = Set.of(
//                createDept, editDept, readDept, readDepts, inviteUsers, deleteEmployee,
//                deleteUser, editEmployee, createEmployee, readEmployee, createUser,
//                readEmployees, editUser, readUsers, readUser
//        );
//
//        Role adminRole = createRoleIfNotFound("ADMIN", "Role for admin", commonPermissions);
//        Role managerRole = createRoleIfNotFound("MANAGER", "Role for manager", Set.of(readEmployeeByDept,readEmployee,readDept));
//        Role employeeRole = createRoleIfNotFound("EMPLOYEE", "Role for employee", Set.of(readEmployee));
//
//        createUserIfNotFound("admin@gmail.com", "Super", "Admin", "admin123!", adminRole);
//    }
//
//    private Permission createPermissionIfNotFound(String name, String desc) {
//        Optional<Permission> permissionOpt = permissionRepository.findByName(name);
//        if (permissionOpt.isEmpty()) {
//            Permission permission = new Permission();
//            permission.setName(name);
//            permission.setDescription(desc);
//            permission.setCreatedTime(LocalDateTime.now());
//            permission.setCreatedBy("System");
//            permission.setStatus(Status.ACTIVE);
//            return permissionRepository.save(permission);
//        }
//        return permissionOpt.get();
//    }
//
//    private Role createRoleIfNotFound(String name, String desc, Set<Permission> permissions) {
//        Optional<Role> roleOpt = roleRepository.findByNameIgnoreCase(name);
//        if (roleOpt.isEmpty()) {
//            Role role = new Role();
//            role.setName(name);
//            role.setDescription(desc);
//            role.setCreatedTime(LocalDateTime.now());
//            role.setCreatedBy("System");
//            role.setStatus(Status.ACTIVE);
//            role.setPermissions(permissions);
//            return roleRepository.save(role);
//        }
//        return roleOpt.get();
//    }
//
//    private void createUserIfNotFound(String email, String firstName, String lastName, String password, Role role) {
//        Optional<User> userOpt = userRepository.findByEmail(email);
//        if (userOpt.isEmpty()) {
//            User user = new User();
//            user.setEmail(email);
//            user.setPassword(passwordEncoder.encode(password));
//            user.setCreatedBy("System");
//            user.setCreatedTime(LocalDateTime.now());
//            user.setUsername(firstName + " " + lastName);
//            user.setFirstName(firstName);
//            user.setLastName(lastName);
//            user.setRoles(Set.of(role));
//            userRepository.save(user);
//        }
//    }
//}
