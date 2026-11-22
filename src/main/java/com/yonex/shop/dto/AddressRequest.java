package com.yonex.shop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequest {

    private Long addressId;

    @NotBlank(message = "Tên người nhận không được để trống")
    @Size(max = 150)
    private String recipientName;

    @NotBlank(message = "Số điện thoại người nhận không được để trống")
    @Pattern(regexp = "^(0|\\+84)[3|5|7|8|9][0-9]{8}$", message = "Số điện thoại không đúng định dạng Việt Nam")
    private String phone;

    @NotBlank(message = "Địa chỉ chi tiết không được để trống")
    @Size(max = 255)
    private String addressLine;

    private String ward;
    private String district;

    @NotBlank(message = "Tỉnh/Thành phố không được để trống")
    @Size(max = 100)
    private String province;

    private boolean isDefault;
}
