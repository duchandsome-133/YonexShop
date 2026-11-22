package com.yonex.shop.service;

import com.yonex.shop.dto.RegisterRequest;
import com.yonex.shop.entity.Cart;
import com.yonex.shop.entity.Role;
import com.yonex.shop.entity.User;
import com.yonex.shop.exception.BadRequestException;
import com.yonex.shop.repository.AddressRepository;
import com.yonex.shop.repository.CartRepository;
import com.yonex.shop.repository.RoleRepository;
import com.yonex.shop.repository.UserRepository;
import com.yonex.shop.service.impl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private AddressRepository addressRepository;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("Đăng ký khách hàng thành công: Gán ROLE_CUSTOMER, mã hóa BCrypt, tạo giỏ hàng rỗng (FR-01, BR-01, BR-02)")
    void testRegisterCustomer_Success() {
        RegisterRequest request = RegisterRequest.builder()
                .fullName("Trần Thị Khách")
                .email("khach@gmail.com")
                .phone("0988776655")
                .password("Password@123")
                .confirmPassword("Password@123")
                .build();

        Role customerRole = Role.builder().roleId(3L).roleName("ROLE_CUSTOMER").build();

        when(userRepository.existsByEmail("khach@gmail.com")).thenReturn(false);
        when(roleRepository.findByRoleName("ROLE_CUSTOMER")).thenReturn(Optional.of(customerRole));
        when(passwordEncoder.encode("Password@123")).thenReturn("$2a$10$encodedHashStringSample123456");
        when(userRepository.save(any(User.class))).thenAnswer(i -> {
            User u = i.getArgument(0);
            u.setUserId(99L);
            return u;
        });

        User registered = userService.registerCustomer(request);

        assertNotNull(registered);
        assertEquals("ROLE_CUSTOMER", registered.getRole().getRoleName(), "Người dùng công khai mặc định là ROLE_CUSTOMER (BR-02)");
        assertEquals("$2a$10$encodedHashStringSample123456", registered.getPasswordHash(), "Mật khẩu phải được băm an toàn (NFR-01)");
        assertTrue(registered.isActive());

        // Kiểm tra tạo giỏ hàng cho người dùng mới
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    @DisplayName("Đăng ký trùng email: Ném ngoại lệ BadRequestException (BR-01)")
    void testRegisterCustomer_DuplicateEmail_ThrowsException() {
        RegisterRequest request = RegisterRequest.builder()
                .fullName("Khách")
                .email("duplicate@gmail.com")
                .phone("0988776655")
                .password("Password@123")
                .confirmPassword("Password@123")
                .build();

        when(userRepository.existsByEmail("duplicate@gmail.com")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> {
            userService.registerCustomer(request);
        });

        verify(userRepository, never()).save(any(User.class));
    }
}
