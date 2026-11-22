package com.yonex.shop.service;

import com.yonex.shop.dto.CheckoutRequest;
import com.yonex.shop.entity.*;
import com.yonex.shop.exception.BadRequestException;
import com.yonex.shop.repository.*;
import com.yonex.shop.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private ProductVariantRepository productVariantRepository;
    @Mock
    private InventoryRepository inventoryRepository;
    @Mock
    private InventoryTransactionRepository inventoryTransactionRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User sampleUser;
    private Product sampleProduct;
    private ProductVariant sampleVariant;
    private Cart sampleCart;
    private CartItem sampleCartItem;

    @BeforeEach
    void setUp() {
        Role customerRole = Role.builder().roleId(3L).roleName("ROLE_CUSTOMER").build();
        sampleUser = User.builder()
                .userId(1L)
                .email("customer@yonexshop.vn")
                .fullName("Nguyễn Văn Khách")
                .phone("0987654321")
                .role(customerRole)
                .isActive(true)
                .build();

        sampleProduct = Product.builder()
                .productId(100L)
                .name("Vợt Cầu Lông Yonex Astrox 100ZZ")
                .sku("YNX-AX100ZZ")
                .price(new BigDecimal("4250000.00"))
                .isActive(true)
                .build();

        sampleVariant = ProductVariant.builder()
                .variantId(200L)
                .product(sampleProduct)
                .variantName("4UG5")
                .sku("YNX-AX100ZZ-4U5")
                .priceAdjustment(BigDecimal.ZERO)
                .isActive(true)
                .build();

        sampleCart = Cart.builder()
                .cartId(10L)
                .user(sampleUser)
                .items(new ArrayList<>())
                .build();

        sampleCartItem = CartItem.builder()
                .cartItemId(50L)
                .cart(sampleCart)
                .variant(sampleVariant)
                .quantity(1)
                .build();

        sampleCart.getItems().add(sampleCartItem);
    }

    @Test
    @DisplayName("Đặt hàng COD thành công: Tạo đơn, snapshot thông tin, trừ kho, làm rỗng giỏ (BR-06, BR-07, BR-08, BR-16)")
    void testCreateOrderFromCart_Success() {
        CheckoutRequest request = CheckoutRequest.builder()
                .recipientName("Nguyễn Văn Khách")
                .recipientPhone("0987654321")
                .shippingAddress("Số 123 Cầu Giấy, Hà Nội")
                .note("Giao giờ hành chính")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(cartRepository.findByUser_UserId(1L)).thenReturn(Optional.of(sampleCart));
        when(productVariantRepository.findById(200L)).thenReturn(Optional.of(sampleVariant));
        // Giả lập trừ kho thành công (1 dòng affected)
        when(inventoryRepository.deductStockConditional(eq(200L), eq(1))).thenReturn(1);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order o = invocation.getArgument(0);
            o.setOrderId(999L);
            return o;
        });

        Order createdOrder = orderService.createOrderFromCart(1L, request);

        assertNotNull(createdOrder);
        assertEquals("COD", createdOrder.getPaymentMethod(), "Phương thức thanh toán phải là COD (BR-08)");
        assertEquals(OrderStatus.PENDING, createdOrder.getOrderStatus(), "Đơn mới phải có trạng thái PENDING");
        assertEquals(new BigDecimal("4250000.00"), createdOrder.getSubtotal());
        assertEquals(BigDecimal.ZERO, createdOrder.getShippingFee(), "Đơn trên 1 triệu được miễn phí ship");
        assertEquals(new BigDecimal("4250000.00"), createdOrder.getTotalAmount());

        // Kiểm tra trừ kho nguyên tử
        verify(inventoryRepository, times(1)).deductStockConditional(200L, 1);
        // Kiểm tra làm rỗng giỏ
        verify(cartItemRepository, times(1)).deleteByCart(sampleCart);
        // Kiểm tra lưu OrderItems
        verify(orderItemRepository, times(1)).save(any(OrderItem.class));
    }

    @Test
    @DisplayName("Bán vượt tồn kho: Ném BadRequestException và không tạo đơn (BR-05, BR-10)")
    void testCreateOrderFromCart_InsufficientStock_ThrowsException() {
        CheckoutRequest request = CheckoutRequest.builder()
                .recipientName("Nguyễn Văn Khách")
                .recipientPhone("0987654321")
                .shippingAddress("Hà Nội")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(cartRepository.findByUser_UserId(1L)).thenReturn(Optional.of(sampleCart));
        when(productVariantRepository.findById(200L)).thenReturn(Optional.of(sampleVariant));
        // deductStockConditional trả về 0 biểu thị không đủ hàng
        when(inventoryRepository.deductStockConditional(eq(200L), eq(1))).thenReturn(0);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> {
            orderService.createOrderFromCart(1L, request);
        });

        assertTrue(ex.getMessage().contains("không đủ tồn kho khả dụng"));
        verify(orderRepository, never()).save(any(Order.class));
        verify(cartItemRepository, never()).deleteByCart(any());
    }

    @Test
    @DisplayName("Hủy đơn hàng PENDING: Cập nhật CANCELLED và hoàn lại kho đúng 1 lần (FR-11, FR-13)")
    void testCancelOrder_Pending_RestoresStock() {
        Order pendingOrder = Order.builder()
                .orderId(123L)
                .orderCode("YNX123")
                .user(sampleUser)
                .orderStatus(OrderStatus.PENDING)
                .items(new ArrayList<>())
                .build();

        OrderItem item = OrderItem.builder()
                .variant(sampleVariant)
                .quantity(2)
                .build();
        pendingOrder.getItems().add(item);

        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(orderRepository.findByOrderIdAndUser(123L, sampleUser)).thenReturn(Optional.of(pendingOrder));

        orderService.cancelOrderByCustomer(123L, 1L, "Đổi ý");

        assertEquals(OrderStatus.CANCELLED, pendingOrder.getOrderStatus());
        // Kiểm tra hoàn kho đúng 2 sản phẩm
        verify(inventoryRepository, times(1)).restoreStock(200L, 2);
        verify(orderRepository, times(1)).save(pendingOrder);
    }

    @Test
    @DisplayName("Hủy đơn hàng DELIVERED: Bị từ chối vì không được hủy đơn đã giao (BR-12, FR-11)")
    void testCancelOrder_Delivered_ThrowsException() {
        Order deliveredOrder = Order.builder()
                .orderId(123L)
                .orderCode("YNX123")
                .user(sampleUser)
                .orderStatus(OrderStatus.DELIVERED)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(orderRepository.findByOrderIdAndUser(123L, sampleUser)).thenReturn(Optional.of(deliveredOrder));

        assertThrows(BadRequestException.class, () -> {
            orderService.cancelOrderByCustomer(123L, 1L, "Lý do");
        });

        verify(inventoryRepository, never()).restoreStock(anyLong(), anyInt());
    }
}
