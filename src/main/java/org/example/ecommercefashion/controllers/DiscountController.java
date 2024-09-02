package org.example.ecommercefashion.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecommercefashion.dtos.request.DiscountRequest;
import org.example.ecommercefashion.dtos.response.DiscountResponse;
import org.example.ecommercefashion.dtos.response.MessageResponse;
import org.example.ecommercefashion.dtos.response.ResponsePage;
import org.example.ecommercefashion.entities.Discount;
import org.example.ecommercefashion.services.DiscountService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


@RestController
@RequestMapping("/api/v1/discount")
@RequiredArgsConstructor
//@Slf4j
public class DiscountController {
    private final DiscountService discountService;

    @GetMapping
    public ResponsePage<Discount, DiscountResponse> FilerDiscount(Long keyword ,Pageable pageable){
        return discountService.filterDiscount(keyword,pageable);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<DiscountResponse> add(@Valid @RequestBody DiscountRequest request,
                                                @RequestHeader("Authorization") String token ){
//        log.info("aaa  {}",  request);
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return ResponseEntity.ok(discountService.add(request,token));
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<DiscountResponse> update( @PathVariable long id,@Valid @RequestBody DiscountRequest request,
                                                   @RequestHeader("Authorization") String token ){
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return ResponseEntity.ok(discountService.update(request,id,token));
    }

    @GetMapping("/{id}")
    @PreAuthorize(("hasRole('ROLE_ADMIN')"))
    public ResponseEntity<DiscountResponse> getFindById(@PathVariable Long id){
        DiscountResponse response = discountService.getByDiscountId(id);
        if(response != null){
            return ResponseEntity.ok(response);
        }else {
            return null;
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(("hasRole('ROLE_ADMIN')"))
    public ResponseEntity<MessageResponse> getDeleted(@PathVariable Long id){
        MessageResponse messageResponse = discountService.deleted(id);
        return ResponseEntity.ok(messageResponse);
    }

}
