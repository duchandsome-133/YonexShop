package com.yonex.shop.service;

import com.yonex.shop.dto.AddressRequest;
import com.yonex.shop.dto.ChangePasswordRequest;
import com.yonex.shop.dto.ProfileUpdateRequest;
import com.yonex.shop.dto.RegisterRequest;
import com.yonex.shop.entity.Address;
import com.yonex.shop.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    User registerCustomer(RegisterRequest request);
    User getByEmail(String email);
    User getById(Long id);
    void updateProfile(Long userId, ProfileUpdateRequest request);
    void changePassword(Long userId, ChangePasswordRequest request);

    List<Address> getUserAddresses(Long userId);
    Address addAddress(Long userId, AddressRequest request);
    Address updateAddress(Long userId, Long addressId, AddressRequest request);
    void deleteAddress(Long userId, Long addressId);
    void setDefaultAddress(Long userId, Long addressId);

    Page<User> getCustomers(String keyword, Pageable pageable);
    Page<User> getStaffs(Pageable pageable);
    User createStaff(RegisterRequest request);
    void toggleUserStatus(Long userId);
    void updateUserRole(Long userId, String roleName);
}
