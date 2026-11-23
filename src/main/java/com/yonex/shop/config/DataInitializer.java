package com.yonex.shop.config;

import com.yonex.shop.entity.Role;
import com.yonex.shop.entity.User;
import com.yonex.shop.repository.RoleRepository;
import com.yonex.shop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Checking and initializing system roles & default accounts...");

        Role adminRole = roleRepository.findByRoleName("ROLE_ADMIN")
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .roleName("ROLE_ADMIN")
                        .description("Quản trị viên toàn hệ thống")
                        .build()));

        Role staffRole = roleRepository.findByRoleName("ROLE_STAFF")
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .roleName("ROLE_STAFF")
                        .description("Nhân viên quản lý đơn hàng và kho")
                        .build()));

        Role customerRole = roleRepository.findByRoleName("ROLE_CUSTOMER")
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .roleName("ROLE_CUSTOMER")
                        .description("Khách hàng mua sắm")
                        .build()));

        // Ensure Admin user
        userRepository.findByEmail("admin@yonexshop.vn").ifPresentOrElse(
                u -> {
                    u.setPasswordHash(passwordEncoder.encode("Password@123"));
                    u.setActive(true);
                    u.setRole(adminRole);
                    userRepository.save(u);
                    log.info("Updated password for admin: admin@yonexshop.vn / Password@123");
                },
                () -> {
                    userRepository.save(User.builder()
                            .role(adminRole)
                            .fullName("Quản Trị Viên Yonex")
                            .email("admin@yonexshop.vn")
                            .phone("0901234567")
                            .passwordHash(passwordEncoder.encode("Password@123"))
                            .isActive(true)
                            .build());
                    log.info("Created default admin account: admin@yonexshop.vn / Password@123");
                }
        );

        // Ensure Staff user
        userRepository.findByEmail("staff@yonexshop.vn").ifPresentOrElse(
                u -> {
                    u.setPasswordHash(passwordEncoder.encode("Password@123"));
                    u.setActive(true);
                    u.setRole(staffRole);
                    userRepository.save(u);
                    log.info("Updated password for staff: staff@yonexshop.vn / Password@123");
                },
                () -> {
                    userRepository.save(User.builder()
                            .role(staffRole)
                            .fullName("Nhân Viên Kho & Đơn")
                            .email("staff@yonexshop.vn")
                            .phone("0912345678")
                            .passwordHash(passwordEncoder.encode("Password@123"))
                            .isActive(true)
                            .build());
                    log.info("Created default staff account: staff@yonexshop.vn / Password@123");
                }
        );

        // Ensure Customer user
        userRepository.findByEmail("customer@yonexshop.vn").ifPresentOrElse(
                u -> {
                    u.setPasswordHash(passwordEncoder.encode("Password@123"));
                    u.setActive(true);
                    u.setRole(customerRole);
                    userRepository.save(u);
                    log.info("Updated password for customer: customer@yonexshop.vn / Password@123");
                },
                () -> {
                    userRepository.save(User.builder()
                            .role(customerRole)
                            .fullName("Nguyễn Văn Khách")
                            .email("customer@yonexshop.vn")
                            .phone("0987654321")
                            .passwordHash(passwordEncoder.encode("Password@123"))
                            .isActive(true)
                            .build());
                    log.info("Created default customer account: customer@yonexshop.vn / Password@123");
                }
        );
    }
}
