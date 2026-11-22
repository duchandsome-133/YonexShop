package com.yonex.shop.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "Addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AddressId")
    private Long addressId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserId", nullable = false)
    private User user;

    @Column(name = "RecipientName", nullable = false, length = 150)
    private String recipientName;

    @Column(name = "Phone", nullable = false, length = 20)
    private String phone;

    @Column(name = "AddressLine", nullable = false, length = 255)
    private String addressLine;

    @Column(name = "Ward", length = 100)
    private String ward;

    @Column(name = "District", length = 100)
    private String district;

    @Column(name = "Province", nullable = false, length = 100)
    private String province;

    @Column(name = "IsDefault", nullable = false)
    @Builder.Default
    private boolean isDefault = false;

    @CreationTimestamp
    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        if (addressLine != null) sb.append(addressLine);
        if (ward != null && !ward.isBlank()) sb.append(", ").append(ward);
        if (district != null && !district.isBlank()) sb.append(", ").append(district);
        if (province != null && !province.isBlank()) sb.append(", ").append(province);
        return sb.toString();
    }
}
