package org.example.ecommercefashion.config.promotion;

import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.entities.Promotion;
import org.example.ecommercefashion.repositories.ProcessSendRepository;
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

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void updatePromotionStatuses() {
        int page = 0;
        int size = 100;

        boolean hasNextPage = true;
        while (hasNextPage) {
            Pageable pageable = PageRequest.of(page, size);
            Page<Promotion> promotionPage = promotionRepository.findAll(pageable);
            List<Promotion> promotionsToUpdate = promotionPage.getContent();

            for (Promotion promotion : promotionsToUpdate) {
                PromotionServiceImpl.setPromotionStatus(promotion);
            }
            promotionRepository.saveAll(promotionsToUpdate);
            hasNextPage = promotionPage.hasNext();
            page++;
        }
    }
}
