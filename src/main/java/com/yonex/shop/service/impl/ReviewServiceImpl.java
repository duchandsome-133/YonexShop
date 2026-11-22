package com.yonex.shop.service.impl;

import com.yonex.shop.dto.ReviewRequest;
import com.yonex.shop.entity.*;
import com.yonex.shop.exception.BadRequestException;
import com.yonex.shop.exception.ResourceNotFoundException;
import com.yonex.shop.repository.OrderRepository;
import com.yonex.shop.repository.ProductRepository;
import com.yonex.shop.repository.ReviewRepository;
import com.yonex.shop.repository.UserRepository;
import com.yonex.shop.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Review addReview(Long userId, ReviewRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng không tồn tại"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm không tồn tại"));

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Đơn hàng không tồn tại"));

        // Kiểm tra quyền sở hữu đơn hàng
        if (!order.getUser().getUserId().equals(userId)) {
            throw new BadRequestException("Bạn không thể đánh giá đơn hàng của người khác");
        }

        // BR-14: Chỉ đơn đã giao (DELIVERED) mới đủ điều kiện đánh giá
        if (order.getOrderStatus() != OrderStatus.DELIVERED) {
            throw new BadRequestException("Chỉ có thể đánh giá sản phẩm từ đơn hàng đã giao thành công");
        }

        // Kiểm tra xem sản phẩm có nằm trong đơn hàng này không
        boolean productInOrder = order.getItems().stream()
                .anyMatch(oi -> oi.getProduct() != null && oi.getProduct().getProductId().equals(product.getProductId()));
        if (!productInOrder) {
            throw new BadRequestException("Sản phẩm này không nằm trong đơn hàng được chọn");
        }

        // Kiểm tra trùng đánh giá (BR-14)
        if (reviewRepository.existsByUserAndProductAndOrder(user, product, order)) {
            throw new BadRequestException("Bạn đã đánh giá sản phẩm này cho đơn hàng này rồi");
        }

        Review review = Review.builder()
                .user(user)
                .product(product)
                .order(order)
                .rating(request.getRating())
                .comment(request.getComment() != null ? request.getComment().trim() : "")
                .isVisible(true)
                .build();

        return reviewRepository.save(review);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Review> getReviewsForProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm không tồn tại"));
        return reviewRepository.findByProductAndIsVisibleTrueOrderByCreatedAtDesc(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getAverageRatingForProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm không tồn tại"));
        Double avg = reviewRepository.getAverageRatingForProduct(product);
        return avg != null ? Math.round(avg * 10.0) / 10.0 : 5.0;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canUserReviewProduct(Long userId, Long productId, Long orderId) {
        User user = userRepository.findById(userId).orElse(null);
        Product product = productRepository.findById(productId).orElse(null);
        Order order = orderRepository.findById(orderId).orElse(null);

        if (user == null || product == null || order == null) return false;
        if (!order.getUser().getUserId().equals(userId)) return false;
        if (order.getOrderStatus() != OrderStatus.DELIVERED) return false;

        boolean inOrder = order.getItems().stream()
                .anyMatch(oi -> oi.getProduct() != null && oi.getProduct().getProductId().equals(productId));
        if (!inOrder) return false;

        return !reviewRepository.existsByUserAndProductAndOrder(user, product, order);
    }

    @Override
    @Transactional
    public void toggleReviewVisibility(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Đánh giá không tồn tại"));
        review.setVisible(!review.isVisible());
        reviewRepository.save(review);
    }
}
