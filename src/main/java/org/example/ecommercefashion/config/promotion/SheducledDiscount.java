package org.example.ecommercefashion.config.promotion;

import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.entities.Discount;
import org.example.ecommercefashion.repositories.DiscountRepository;
import org.example.ecommercefashion.services.impl.DiscountServiceImpl;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Configuration
@RequiredArgsConstructor
public class SheducledDiscount {

    private final DiscountRepository discountRepository;

//    @Scheduled(cron = "0 0 * * * *")
//    @Transactional
//    public void updateDiscountStatus(){
//        int page = 0;
//        int size = 100;
//
//        boolean hasNextPage = true;
//        while (hasNextPage){
//            Pageable pageable = PageRequest.of(page,size);
//            Page<Discount> discountsPage = discountRepository.findAll(pageable);
//            List<Discount> discountsToUpdate = discountsPage.getContent();
//
//            for (Discount discount : discountsToUpdate){
//                DiscountServiceImpl.setDiscountStatus(discount);
//            }
//            discountRepository.saveAll(discountsToUpdate);
//            hasNextPage = discountsPage.hasNext();
//            page++;
//        }
//    }
}
