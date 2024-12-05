package org.example.ecommercefashion.config.promotion;

import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.entities.ProductDetail;
import org.example.ecommercefashion.entities.Promotion;
import org.example.ecommercefashion.enums.promotion.StatusPromotionEnum;
import org.example.ecommercefashion.enums.promotion.TypePromotionEnum;
import org.example.ecommercefashion.repositories.ProcessSendRepository;
import org.example.ecommercefashion.repositories.ProductDetailRepository;
import org.example.ecommercefashion.repositories.PromotionRepository;
import org.example.ecommercefashion.services.impl.PromotionServiceImpl;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Configuration
@RequiredArgsConstructor
public class SheducledPromotion {

    private final PromotionRepository promotionRepository;

    private final ProductDetailRepository productDetailRepository;

    @Scheduled(cron = "*/10 * * * * *")
    @Transactional
    public synchronized void updatePromotionStatuses() {
        int promotionPageNumber = 0;
        int promotionPageSize = 100;
        int productDetailPageSize = 100;

        boolean hasMorePromotions = true;
        while (hasMorePromotions) {
            Pageable promotionPageable = PageRequest.of(promotionPageNumber, promotionPageSize);
            Page<Promotion> promotionPage = promotionRepository.findAll(promotionPageable);
            List<Promotion> promotionsToUpdate = promotionPage.getContent();

            for (Promotion promotion : promotionsToUpdate) {
                PromotionServiceImpl.setPromotionStatus(promotion);

                boolean hasMoreProductDetails = true;
                int productDetailPageNumber = 0;

                while (hasMoreProductDetails) {
                    Pageable productDetailPageable = PageRequest.of(productDetailPageNumber, productDetailPageSize);
                    Page<ProductDetail> productDetailPage = productDetailRepository.findByPromotionId(
                            promotion.getId(), productDetailPageable);
                    List<ProductDetail> productDetails = productDetailPage.getContent();

                    for (ProductDetail productDetail : productDetails) {
                        if (promotion.getStatusPromotionEnum() == StatusPromotionEnum.ACTIVE) {
                            if (productDetail.getOriginPrice() == null) {
                                productDetail.setOriginPrice(productDetail.getPrice());
                            }

                            double discountedPrice;
                            if (promotion.getTypePromotionEnum() == TypePromotionEnum.PERCENTAGE_DISCOUNT) {
                                discountedPrice = productDetail.getOriginPrice() * (1 - promotion.getValue() / 100.0);
                            } else if (promotion.getTypePromotionEnum() == TypePromotionEnum.AMOUNT_DISCOUNT) {
                                discountedPrice = productDetail.getOriginPrice() - promotion.getValue();
                            } else {
                                discountedPrice = productDetail.getOriginPrice();
                            }

                            productDetail.setPrice(Math.max(discountedPrice, productDetail.getOriginPrice()*0.5));
                        } else if (promotion.getStatusPromotionEnum() == StatusPromotionEnum.ENDED) {
                            if (productDetail.getOriginPrice() != null) {
                                productDetail.setPrice(productDetail.getOriginPrice());
                                 productDetail.setOriginPrice(null);
                            }
                        }
                    }
                    productDetailRepository.saveAll(productDetails);
                    hasMoreProductDetails = productDetailPage.hasNext();
                    productDetailPageNumber++;
                }
            }
            promotionRepository.saveAll(promotionsToUpdate);
            hasMorePromotions = promotionPage.hasNext();
            promotionPageNumber++;
        }
    }
}
