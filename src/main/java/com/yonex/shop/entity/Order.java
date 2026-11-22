package com.yonex.shop.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OrderId")
    private Long orderId;

    @Column(name = "OrderCode", nullable = false, unique = true, length = 50)
    private String orderCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserId", nullable = false)
    private User user;

    @Column(name = "RecipientName", nullable = false, length = 150)
    private String recipientName;

    @Column(name = "RecipientPhone", nullable = false, length = 20)
    private String recipientPhone;

    @Column(name = "ShippingAddress", nullable = false, length = 500)
    private String shippingAddress;

    @Column(name = "Subtotal", nullable = false, precision = 18, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "ShippingFee", nullable = false, precision = 18, scale = 2)
    @Builder.Default
    private BigDecimal shippingFee = BigDecimal.ZERO;

    @Column(name = "DiscountAmount", nullable = false, precision = 18, scale = 2)
    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "TotalAmount", nullable = false, precision = 18, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "PaymentMethod", nullable = false, length = 20)
    @Builder.Default
    private String paymentMethod = "COD";

    @Column(name = "PaymentStatus", nullable = false, length = 30)
    @Builder.Default
    private String paymentStatus = "UNPAID";

    @Enumerated(EnumType.STRING)
    @Column(name = "OrderStatus", nullable = false, length = 30)
    @Builder.Default
    private OrderStatus orderStatus = OrderStatus.PENDING;

    @Column(name = "Note", length = 500)
    private String note;

    @CreationTimestamp
    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    public boolean canCancel() {
        return this.orderStatus == OrderStatus.PENDING;
    }
}
