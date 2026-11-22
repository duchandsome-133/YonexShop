package com.yonex.shop.service.impl;

import com.yonex.shop.dto.AddressRequest;
import com.yonex.shop.dto.ChangePasswordRequest;
import com.yonex.shop.dto.ProfileUpdateRequest;
import com.yonex.shop.dto.RegisterRequest;
import com.yonex.shop.entity.Address;
import com.yonex.shop.entity.Cart;
import com.yonex.shop.entity.Role;
import com.yonex.shop.entity.User;
import com.yonex.shop.exception.BadRequestException;
import com.yonex.shop.exception.ResourceNotFoundException;
import com.yonex.shop.repository.AddressRepository;
import com.yonex.shop.repository.CartRepository;
import com.yonex.shop.repository.RoleRepository;
import com.yonex.shop.repository.UserRepository;
import com.yonex.shop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AddressRepository addressRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User registerCustomer(RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Mật khẩu xác nhận không khớp");
        }

        if (userRepository.existsByEmail(request.getEmail().trim().toLowerCase())) {
            throw new BadRequestException("Email đã được đăng ký trong hệ thống");
        }

        Role customerRole = roleRepository.findByRoleName("ROLE_CUSTOMER")
                .orElseThrow(() -> new ResourceNotFoundException("Role ROLE_CUSTOMER không tồn tại"));

        User user = User.builder()
                .role(customerRole)
                .fullName(request.getFullName().trim())
                .email(request.getEmail().trim().toLowerCase())
                .phone(request.getPhone().trim())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .isActive(true)
                .build();

        User savedUser = userRepository.save(user);

        // Khởi tạo giỏ hàng rỗng cho người dùng mới
        Cart cart = Cart.builder()
                .user(savedUser)
                .build();
        cartRepository.save(cart);

        return savedUser;
    }

    @Override
    @Transactional(readOnly = true)
    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với email: " + email));
    }

    @Override
    @Transactional(readOnly = true)
    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + id));
    }

    @Override
    @Transactional
    public void updateProfile(Long userId, ProfileUpdateRequest request) {
        User user = getById(userId);
        user.setFullName(request.getFullName().trim());
        user.setPhone(request.getPhone().trim());
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = getById(userId);

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Mật khẩu hiện tại không chính xác");
        }

        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new BadRequestException("Xác nhận mật khẩu mới không khớp");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Address> getUserAddresses(Long userId) {
        User user = getById(userId);
        return addressRepository.findByUserOrderByIsDefaultDescCreatedAtDesc(user);
    }

    @Override
    @Transactional
    public Address addAddress(Long userId, AddressRequest request) {
        User user = getById(userId);

        // Nếu là địa chỉ đầu tiên hoặc được đánh dấu mặc định
        List<Address> existing = addressRepository.findByUserOrderByIsDefaultDescCreatedAtDesc(user);
        boolean shouldBeDefault = existing.isEmpty() || request.isDefault();

        if (shouldBeDefault) {
            existing.forEach(a -> {
                a.setDefault(false);
                addressRepository.save(a);
            });
        }

        Address address = Address.builder()
                .user(user)
                .recipientName(request.getRecipientName().trim())
                .phone(request.getPhone().trim())
                .addressLine(request.getAddressLine().trim())
                .ward(request.getWard() != null ? request.getWard().trim() : "")
                .district(request.getDistrict() != null ? request.getDistrict().trim() : "")
                .province(request.getProvince().trim())
                .isDefault(shouldBeDefault)
                .build();

        return addressRepository.save(address);
    }

    @Override
    @Transactional
    public Address updateAddress(Long userId, Long addressId, AddressRequest request) {
        User user = getById(userId);
        Address address = addressRepository.findByAddressIdAndUser(addressId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy địa chỉ"));

        if (request.isDefault() && !address.isDefault()) {
            addressRepository.findByUserAndIsDefaultTrue(user).ifPresent(a -> {
                a.setDefault(false);
                addressRepository.save(a);
            });
            address.setDefault(true);
        }

        address.setRecipientName(request.getRecipientName().trim());
        address.setPhone(request.getPhone().trim());
        address.setAddressLine(request.getAddressLine().trim());
        address.setWard(request.getWard() != null ? request.getWard().trim() : "");
        address.setDistrict(request.getDistrict() != null ? request.getDistrict().trim() : "");
        address.setProvince(request.getProvince().trim());

        return addressRepository.save(address);
    }

    @Override
    @Transactional
    public void deleteAddress(Long userId, Long addressId) {
        User user = getById(userId);
        Address address = addressRepository.findByAddressIdAndUser(addressId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy địa chỉ"));

        boolean wasDefault = address.isDefault();
        addressRepository.delete(address);

        if (wasDefault) {
            List<Address> remaining = addressRepository.findByUserOrderByIsDefaultDescCreatedAtDesc(user);
            if (!remaining.isEmpty()) {
                Address first = remaining.get(0);
                first.setDefault(true);
                addressRepository.save(first);
            }
        }
    }

    @Override
    @Transactional
    public void setDefaultAddress(Long userId, Long addressId) {
        User user = getById(userId);
        Address address = addressRepository.findByAddressIdAndUser(addressId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy địa chỉ"));

        addressRepository.findByUserAndIsDefaultTrue(user).ifPresent(a -> {
            a.setDefault(false);
            addressRepository.save(a);
        });

        address.setDefault(true);
        addressRepository.save(address);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> getCustomers(String keyword, Pageable pageable) {
        if (keyword != null && !keyword.isBlank()) {
            return userRepository.searchUsers(keyword.trim(), pageable);
        }
        Role customerRole = roleRepository.findByRoleName("ROLE_CUSTOMER")
                .orElseThrow(() -> new ResourceNotFoundException("ROLE_CUSTOMER không tồn tại"));
        return userRepository.findByRole(customerRole, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> getStaffs(Pageable pageable) {
        Role staffRole = roleRepository.findByRoleName("ROLE_STAFF")
                .orElseThrow(() -> new ResourceNotFoundException("ROLE_STAFF không tồn tại"));
        return userRepository.findByRole(staffRole, pageable);
    }

    @Override
    @Transactional
    public User createStaff(RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Mật khẩu xác nhận không khớp");
        }

        if (userRepository.existsByEmail(request.getEmail().trim().toLowerCase())) {
            throw new BadRequestException("Email đã được sử dụng");
        }

        Role staffRole = roleRepository.findByRoleName("ROLE_STAFF")
                .orElseThrow(() -> new ResourceNotFoundException("ROLE_STAFF không tồn tại"));

        User staff = User.builder()
                .role(staffRole)
                .fullName(request.getFullName().trim())
                .email(request.getEmail().trim().toLowerCase())
                .phone(request.getPhone().trim())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .isActive(true)
                .build();

        return userRepository.save(staff);
    }

    @Override
    @Transactional
    public void toggleUserStatus(Long userId) {
        User user = getById(userId);
        user.setActive(!user.isActive());
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateUserRole(Long userId, String roleName) {
        User user = getById(userId);
        Role role = roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role không hợp lệ: " + roleName));
        user.setRole(role);
        userRepository.save(user);
    }
}
