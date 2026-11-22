package com.yonex.shop.service.impl;

import com.yonex.shop.dto.CheckoutRequest;
import com.yonex.shop.entity.*;
import com.yonex.shop.exception.BadRequestException;
import com.yonex.shop.exception.ResourceNotFoundException;
import com.yonex.shop.repository.*;
import com.yonex.shop.service.CartService;
import com.yonex.shop.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductVariantRepository productVariantRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    private static final BigDecimal FIXED_SHIPPING_FEE = new BigDecimal("30000.00");
    private static final BigDecimal FREE_SHIPPING_THRESHOLD = new BigDecimal("1000000.00");

    private synchronized String generateOrderCode() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmss"));
        int rand = new Random().nextInt(900) + 100;
        return "YNX" + timestamp + rand;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Order createOrderFromCart(Long userId, CheckoutRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng không tồn tại"));

        Cart cart = cartRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new BadRequestException("Giỏ hàng của bạn đang trống"));

        List<CartItem> cartItems = cart.getItems();
        if (cartItems == null || cartItems.isEmpty()) {
            throw new BadRequestException("Giỏ hàng của bạn không có sản phẩm nào để đặt hàng");
        }

        // Xác định thông tin giao hàng
        String recipientName = request.getRecipientName();
        String recipientPhone = request.getRecipientPhone();
        String shippingAddress = request.getShippingAddress();

        if (request.getSelectedAddressId() != null) {
            Address address = addressRepository.findByAddressIdAndUser(request.getSelectedAddressId(), user)
                    .orElseThrow(() -> new BadRequestException("Địa chỉ nhận hàng không hợp lệ"));
            recipientName = address.getRecipientName();
            recipientPhone = address.getPhone();
            shippingAddress = address.getFullAddress();
        }

        if (recipientName == null || recipientName.isBlank() ||
            recipientPhone == null || recipientPhone.isBlank() ||
            shippingAddress == null || shippingAddress.isBlank()) {
            throw new BadRequestException("Vui lòng cung cấp đầy đủ thông tin người nhận và địa chỉ giao hàng");
        }

        BigDecimal subtotal = BigDecimal.ZERO;
        List<OrderItem> orderItemsToSave = new ArrayList<>();

        // Kiểm tra tồn kho & tính toán giá thẩm quyền từ database
        for (CartItem ci : cartItems) {
            ProductVariant variant = productVariantRepository.findById(ci.getVariant().getVariantId())
                    .orElseThrow(() -> new ResourceNotFoundException("Biến thể không tồn tại"));

            if (!variant.isActive() || !variant.getProduct().isActive()) {
                throw new BadRequestException("Sản phẩm '" + variant.getVariantName() + "' đã ngừng kinh doanh");
            }

            int requestedQty = ci.getQuantity();

            // Trừ kho nguyên tử với điều kiện (Atomic conditional update)
            int rowsAffected = inventoryRepository.deductStockConditional(variant.getVariantId(), requestedQty);
            if (rowsAffected == 0) {
                int currentStock = variant.getAvailableQuantity();
                throw new BadRequestException("Sản phẩm '" + variant.getVariantName() + 
                        "' không đủ tồn kho khả dụng (còn: " + currentStock + ", yêu cầu: " + requestedQty + ")");
            }

            // Ghi nhận lịch sử giao dịch kho
            InventoryTransaction tx = InventoryTransaction.builder()
                    .variant(variant)
                    .changeQuantity(-requestedQty)
                    .reason("Khách đặt hàng COD")
                    .createdBy(user.getEmail())
                    .build();
            inventoryTransactionRepository.save(tx);

            // Snapshot giá và tên sản phẩm tại thời điểm mua (BR-07)
            BigDecimal unitPrice = variant.getEffectivePrice();
            BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(requestedQty));
            subtotal = subtotal.add(lineTotal);

            OrderItem oi = OrderItem.builder()
                    .product(variant.getProduct())
                    .variant(variant)
                    .productNameSnapshot(variant.getProduct().getName() + " - " + variant.getVariantName())
                    .skuSnapshot(variant.getSku())
                    .unitPrice(unitPrice)
                    .quantity(requestedQty)
                    .lineTotal(lineTotal)
                    .build();

            orderItemsToSave.add(oi);
        }

        // Tính phí vận chuyển (Miễn phí nếu đơn >= 1.000.000đ)
        BigDecimal shippingFee = subtotal.compareTo(FREE_SHIPPING_THRESHOLD) >= 0 ? BigDecimal.ZERO : FIXED_SHIPPING_FEE;
        BigDecimal totalAmount = subtotal.add(shippingFee);

        Order order = Order.builder()
                .orderCode(generateOrderCode())
                .user(user)
                .recipientName(recipientName.trim())
                .recipientPhone(recipientPhone.trim())
                .shippingAddress(shippingAddress.trim())
                .subtotal(subtotal)
                .shippingFee(shippingFee)
                .discountAmount(BigDecimal.ZERO)
                .totalAmount(totalAmount)
                .paymentMethod("COD")
                .paymentStatus("UNPAID")
                .orderStatus(OrderStatus.PENDING)
                .note(request.getNote())
                .build();

        Order savedOrder = orderRepository.save(order);

        // Gán order cho từng OrderItem và lưu
        for (OrderItem oi : orderItemsToSave) {
            oi.setOrder(savedOrder);
            orderItemRepository.save(oi);
        }

        // Cập nhật referenceId cho transaction kho
        for (OrderItem oi : orderItemsToSave) {
            InventoryTransaction tx = InventoryTransaction.builder()
                    .variant(oi.getVariant())
                    .changeQuantity(0) // Marker reference
                    .reason("Đơn hàng: " + savedOrder.getOrderCode())
                    .referenceId(savedOrder.getOrderCode())
                    .createdBy(user.getEmail())
                    .build();
            inventoryTransactionRepository.save(tx);
        }

        // Làm rỗng giỏ hàng sau khi đặt thành công (FR-10)
        cartItemRepository.deleteByCart(cart);

        log.info("Order created successfully: {} for user: {}", savedOrder.getOrderCode(), user.getEmail());
        return savedOrder;
    }

    @Override
    @Transactional(readOnly = true)
    public Order getOrderByIdAndUser(Long orderId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng không tồn tại"));

        return orderRepository.findByOrderIdAndUser(orderId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng hoặc bạn không có quyền xem đơn này"));
    }

    @Override
    @Transactional(readOnly = true)
    public Order getOrderByCodeAndUser(String orderCode, Long userId) {
        Order order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng: " + orderCode));

        if (!order.getUser().getUserId().equals(userId)) {
            throw new BadRequestException("Bạn không có quyền xem đơn hàng này");
        }
        return order;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Order> getOrdersByUser(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng không tồn tại"));
        return orderRepository.findByUserOrderByCreatedAtDesc(user, pageable);
    }

    @Override
    @Transactional
    public void cancelOrderByCustomer(Long orderId, Long userId, String reason) {
        Order order = getOrderByIdAndUser(orderId, userId);

        if (order.getOrderStatus() != OrderStatus.PENDING) {
            throw new BadRequestException("Chỉ có thể hủy đơn hàng khi đang ở trạng thái 'Chờ xác nhận'");
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        if (reason != null && !reason.isBlank()) {
            order.setNote((order.getNote() != null ? order.getNote() + " | " : "") + "Lý do hủy: " + reason.trim());
        }
        orderRepository.save(order);

        // Hoàn lại tồn kho đúng một lần (FR-13, BR-16)
        for (OrderItem oi : order.getItems()) {
            if (oi.getVariant() != null) {
                inventoryRepository.restoreStock(oi.getVariant().getVariantId(), oi.getQuantity());

                InventoryTransaction tx = InventoryTransaction.builder()
                        .variant(oi.getVariant())
                        .changeQuantity(oi.getQuantity())
                        .reason("Hoàn kho do hủy đơn " + order.getOrderCode())
                        .referenceId(order.getOrderCode())
                        .createdBy("Customer (Hủy đơn)")
                        .build();
                inventoryTransactionRepository.save(tx);
            }
        }
        log.info("Order {} cancelled by customer and stock restored", order.getOrderCode());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Order> searchOrdersAdmin(String keyword, OrderStatus status, Pageable pageable) {
        String cleanKeyword = (keyword != null && !keyword.isBlank()) ? keyword.trim() : null;
        return orderRepository.searchOrdersAdmin(cleanKeyword, status, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng ID: " + orderId));
    }

    @Override
    @Transactional
    public void updateOrderStatus(Long orderId, OrderStatus newStatus, String adminUsername) {
        Order order = getOrderById(orderId);
        OrderStatus currentStatus = order.getOrderStatus();

        if (currentStatus == OrderStatus.DELIVERED) {
            throw new BadRequestException("Đơn hàng đã giao thành công không thể thay đổi trạng thái");
        }

        if (currentStatus == OrderStatus.CANCELLED) {
            throw new BadRequestException("Đơn hàng đã hủy không thể kích hoạt lại");
        }

        // Quy tắc luồng chuyển trạng thái
        boolean isValidTransition = false;
        if (currentStatus == OrderStatus.PENDING && (newStatus == OrderStatus.CONFIRMED || newStatus == OrderStatus.CANCELLED)) {
            isValidTransition = true;
        } else if (currentStatus == OrderStatus.CONFIRMED && (newStatus == OrderStatus.SHIPPING || newStatus == OrderStatus.CANCELLED)) {
            isValidTransition = true;
        } else if (currentStatus == OrderStatus.SHIPPING && newStatus == OrderStatus.DELIVERED) {
            isValidTransition = true;
        }

        if (!isValidTransition) {
            throw new BadRequestException("Không thể chuyển trạng thái từ " + 
                    currentStatus.getDisplayName() + " sang " + newStatus.getDisplayName());
        }

        order.setOrderStatus(newStatus);
        if (newStatus == OrderStatus.DELIVERED) {
            order.setPaymentStatus("PAID"); // COD thanh toán khi nhận hàng thành công
        }

        // Nếu admin hủy đơn (ví dụ hết hàng ngoài ý muốn), hoàn lại tồn kho
        if (newStatus == OrderStatus.CANCELLED) {
            for (OrderItem oi : order.getItems()) {
                if (oi.getVariant() != null) {
                    inventoryRepository.restoreStock(oi.getVariant().getVariantId(), oi.getQuantity());

                    InventoryTransaction tx = InventoryTransaction.builder()
                            .variant(oi.getVariant())
                            .changeQuantity(oi.getQuantity())
                            .reason("Admin hủy đơn: " + order.getOrderCode())
                            .referenceId(order.getOrderCode())
                            .createdBy(adminUsername)
                            .build();
                    inventoryTransactionRepository.save(tx);
                }
            }
        }

        orderRepository.save(order);
        log.info("Order {} updated to {} by {}", order.getOrderCode(), newStatus, adminUsername);
    }
}
