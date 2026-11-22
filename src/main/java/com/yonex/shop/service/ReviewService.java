package com.yonex.shop.service;

import com.yonex.shop.dto.ReviewRequest;
import com.yonex.shop.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReviewService {
    Review addReview(Long userId, ReviewRequest request);
    List<Review> getReviewsForProduct(Long productId);
    Double getAverageRatingForProduct(Long productId);
    boolean canUserReviewProduct(Long userId, Long productId, Long orderId);
    void toggleReviewVisibility(Long reviewId);
}
