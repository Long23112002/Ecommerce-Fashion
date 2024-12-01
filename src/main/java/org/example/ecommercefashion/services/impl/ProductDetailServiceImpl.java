package org.example.ecommercefashion.services.impl;

import com.longnh.exceptions.ExceptionHandle;
import com.longnh.utils.FnCommon;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.dtos.filter.ProductDetailParam;
import org.example.ecommercefashion.dtos.request.ProductDetailRequest;
import org.example.ecommercefashion.dtos.response.JwtResponse;
import org.example.ecommercefashion.dtos.response.MessageResponse;
import org.example.ecommercefashion.dtos.response.ProductDetailResponse;
import org.example.ecommercefashion.dtos.response.ProductResponse;
import org.example.ecommercefashion.dtos.response.ResponsePage;
import org.example.ecommercefashion.dtos.response.UserResponse;
import org.example.ecommercefashion.entities.Color;
import org.example.ecommercefashion.entities.Product;
import org.example.ecommercefashion.entities.ProductDetail;
import org.example.ecommercefashion.entities.Promotion;
import org.example.ecommercefashion.entities.Size;
import org.example.ecommercefashion.entities.User;
import org.example.ecommercefashion.entities.value.UserValue;
import org.example.ecommercefashion.exceptions.AttributeErrorMessage;
import org.example.ecommercefashion.exceptions.ErrorMessage;
import org.example.ecommercefashion.repositories.ColorRepository;
import org.example.ecommercefashion.repositories.ProductDetailRepository;
import org.example.ecommercefashion.repositories.ProductRepository;
import org.example.ecommercefashion.repositories.PromotionRepository;
import org.example.ecommercefashion.repositories.SizeRepository;
import org.example.ecommercefashion.repositories.UserRepository;
import org.example.ecommercefashion.security.JwtService;
import org.example.ecommercefashion.services.ProductDetailService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductDetailServiceImpl implements ProductDetailService {
    private final ProductDetailRepository productDetailRepository;
    private final ProductRepository productRepository;
    private final SizeRepository sizeRepository;
    private final ColorRepository colorRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final EntityManager entityManager;
    private final PromotionRepository promotionRepository;


    private UserResponse getInfoUser(Long id) {
        User user =
                userRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.USER_NOT_FOUND));
        UserResponse userResponse = new UserResponse();
        FnCommon.copyNonNullProperties(userResponse, user);
        return userResponse;
    }

    private UserValue getInfoUserValue(Long id) {
        User user =
                userRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.USER_NOT_FOUND));
        UserValue userValue = new UserValue();
        FnCommon.copyNonNullProperties(userValue, user);
        return userValue;
    }

    @Override
    public ProductDetail createProductDetail(ProductDetailRequest request, String token) {
        if (token != null) {
            JwtResponse jwtResponse = jwtService.decodeToken(token);

            Product product =
                    productRepository
                            .findById(request.getIdProduct())
                            .orElseThrow(
                                    () ->
                                            new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.PRODUCT_NOT_FOUND));
            Size size =
                    sizeRepository
                            .findById(request.getIdSize())
                            .orElseThrow(
                                    () ->
                                            new ExceptionHandle(
                                                    HttpStatus.BAD_REQUEST, AttributeErrorMessage.SIZE_NOT_FOUND));
            Color color =
                    colorRepository
                            .findById(request.getIdColor())
                            .orElseThrow(
                                    () ->
                                            new ExceptionHandle(
                                                    HttpStatus.BAD_REQUEST, AttributeErrorMessage.COLOR_NOT_FOUND));

            List<ProductDetail> productDetailList = productDetailRepository.getDetailByIdProduct(request.getIdProduct());

            if (!productDetailList.isEmpty()) {
                Optional<ProductDetail> minPriceProduct = productDetailList.stream()
                        .min(Comparator.comparing(ProductDetail::getPrice));
                Optional<ProductDetail> maxPriceProduct = productDetailList.stream()
                        .max(Comparator.comparing(ProductDetail::getPrice));

                if (minPriceProduct.get().getPrice() >= request.getPrice()) {
                    product.setMinPrice(request.getPrice().longValue());
                } else if (maxPriceProduct.get().getPrice() <= request.getPrice()) {
                    product.setMaxPrice(request.getPrice().longValue());
                }
            } else {
                // nếu chưa có detail nào trong product thì set maxPrice, minPrice là price trong request
                product.setMaxPrice(request.getPrice().longValue());
                product.setMinPrice(request.getPrice().longValue());

            }
            productRepository.save(product);

            ProductDetail detail = new ProductDetail();
            FnCommon.copyNonNullProperties(detail, request);
            detail.setProduct(product);
            detail.setSize(size);
            detail.setColor(color);
            detail.setCreateBy(jwtResponse.getUserId());
            detail.setCreateByUser(getInfoUserValue(jwtResponse.getUserId()));
            detail = productDetailRepository.save(detail);

            return detail;
        } else {
            throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.ERROR_WHEN_CREATE);
        }
    }

    @Override
    public ProductDetail detail(Long id) {
        ProductDetail productDetail = findById(id);
        List<Promotion> promotionList = promotionRepository.findAllByProductDetailId(productDetail.getId());
        productDetail.setPromotion(promotionList.isEmpty() ? null : promotionList.get(0));
        if (productDetail.getCreateBy() != null) {
            productDetail.setCreateByUser(getInfoUserValue(productDetail.getCreateBy()));
        }
        if (productDetail.getUpdateBy() != null) {
            productDetail.setUpdateByUser(getInfoUserValue(productDetail.getUpdateBy()));
        }
        return productDetail;
    }

    @Override
    public ResponsePage<ProductDetail, ProductDetail> getDetailByIdProduct(
            Long idProduct, Pageable pageable) {
        Page<ProductDetail> productDetailPage =
                productDetailRepository
                        .findAllByProductId(idProduct, pageable)
                        .map(
                                detail -> {
                                    List<Promotion> promotionList = promotionRepository.findAllByProductDetailId(detail.getId());
                                    detail.setPromotion(promotionList.isEmpty() ? null : promotionList.get(0));
                                    if (detail.getCreateBy() != null) {
                                        detail.setCreateByUser(getInfoUserValue(detail.getCreateBy()));
                                    }
                                    if (detail.getUpdateBy() != null) {
                                        detail.setUpdateByUser(getInfoUserValue(detail.getUpdateBy()));
                                    }
                                    return detail;
                                });
        return new ResponsePage<>(productDetailPage);

        //        return productDetailRepository.getDetailByIdProduct(idProduct, pageable);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleMinusQuantity(Integer quantityBuy, ProductDetail productDetail) {
        if (productDetail.getQuantity() < quantityBuy) {
            throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.PRODUCT_NOT_ENOUGH);
        }
        productDetail.setQuantity(productDetail.getQuantity() - quantityBuy);
        productDetailRepository.save(productDetail);
    }

    private ProductDetail findById(Long id) {
        return productDetailRepository
                .findById(id)
                .orElseThrow(
                        () -> new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.PRODUCT_NOT_FOUND));
    }

    @Override
    public ProductDetail updateProductDetail(Long id, ProductDetailRequest request, String token) {
        if (token != null) {
            JwtResponse jwtResponse = jwtService.decodeToken(token);

            ProductDetail detail = findById(id);
            FnCommon.copyNonNullProperties(detail, request);

            Product product =
                    productRepository
                            .findById(request.getIdProduct())
                            .orElseThrow(
                                    () ->
                                            new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.PRODUCT_NOT_FOUND));
            Size size =
                    sizeRepository
                            .findById(request.getIdSize())
                            .orElseThrow(
                                    () ->
                                            new ExceptionHandle(
                                                    HttpStatus.BAD_REQUEST, AttributeErrorMessage.SIZE_NOT_FOUND));
            Color color =
                    colorRepository
                            .findById(request.getIdColor())
                            .orElseThrow(
                                    () ->
                                            new ExceptionHandle(
                                                    HttpStatus.BAD_REQUEST, AttributeErrorMessage.COLOR_NOT_FOUND));

            detail.setProduct(product);
            detail.setSize(size);
            detail.setColor(color);
            detail.setImages(request.getImages());
            detail.setUpdateBy(jwtResponse.getUserId());

            detail.setCreateByUser(getInfoUserValue(detail.getCreateBy()));
            detail.setUpdateByUser(getInfoUserValue(jwtResponse.getUserId()));
            detail = productDetailRepository.save(detail);

            return detail;
        } else {
            throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.ERROR_WHEN_CREATE);
        }
    }

    @Override
    public ResponsePage<ProductDetail, ProductDetail> getAllPage(
            Pageable pageable, ProductDetailParam productDetailParam) {
        Page<ProductDetail> productDetailPage = filterProductDetail(productDetailParam, pageable)
                .map(detail -> {
                    List<Promotion> promotionList = promotionRepository.findAllByProductDetailId(detail.getId());
                    detail.setPromotion(promotionList.isEmpty() ? null : promotionList.get(0));
//                    if (detail.getUpdateBy() != null) {
//                        detail.setUpdateByUser(getInfoUserValue(detail.getUpdateBy()));
//                    }
                    return detail;
                });
        return new ResponsePage<>(productDetailPage);
    }

    @Override
    public MessageResponse delete(Long id) {
        ProductDetail detail = findById(id);
        detail.setDeleted(true);
        productDetailRepository.save(detail);

        return MessageResponse.builder().message("Product Detail deleted successfully").build();
    }

    ProductDetailResponse mapDetailToResponse(ProductDetail detail) {
        ProductDetailResponse response = new ProductDetailResponse();
        FnCommon.copyNonNullProperties(response, detail);
        response.setCreateBy(getInfoUser(detail.getCreateBy()));
        response.setUpdateBy(getInfoUser(detail.getUpdateBy()));
        response.setColorName(detail.getColor().getName());
        response.setSizeName(detail.getSize().getName());

        response.setProduct(mapProductToResponse(detail.getProduct()));
        return response;
    }

    ProductResponse mapProductToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .code(product.getCode())
                .name(product.getName())
                .createdAt(product.getCreateAt())
                .updatedAt(product.getUpdateAt())
                .createdBy(getInfoUser(product.getCreateBy()))
                .deleted(product.getDeleted())
                .brandName(product.getBrand().getName())
                .categoryName(product.getCategory().getName())
                .materialName(product.getMaterial().getName())
                .originName(product.getOrigin().getName())
                .build();
    }

    public Page<ProductDetail> filterProductDetail(ProductDetailParam param, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ProductDetail> cq = cb.createQuery(ProductDetail.class);
        Root<ProductDetail> root = cq.from(ProductDetail.class);

        List<Predicate> predicates = new ArrayList<>();

        // Tìm kiếm từ khóa
        if (param.getKeyword() != null && !param.getKeyword().isEmpty()) {
            String keyword = "%" + param.getKeyword().toLowerCase().trim() + "%";
            Predicate sizePredicate = cb.like(cb.lower(root.get("size").get("name")), keyword);
            Predicate colorPredicate = cb.like(cb.lower(root.get("color").get("name")), keyword);
            Predicate productPredicate = cb.like(
                    cb.function("TRIM", String.class, cb.lower(root.get("product").get("name"))),
                    keyword
            );
            predicates.add(cb.or(sizePredicate, colorPredicate, productPredicate));
        }

        if (!param.isAllowZero()) {
            predicates.add(cb.notEqual(root.get("quantity"), 0));
        }

        // Lọc theo idColor
        if (param.getIdColor() != null) {
            predicates.add(cb.equal(root.get("color").get("id"), param.getIdColor()));
        }

        // Lọc theo idProduct
        if (param.getIdProduct() != null) {
            predicates.add(cb.equal(root.get("product").get("id"), param.getIdProduct()));
        }

        // Lọc theo idSize
        if (param.getIdSize() != null) {
            predicates.add(cb.equal(root.get("size").get("id"), param.getIdSize()));
        }

        // Lọc theo minPrice
        if (param.getMinPrice() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("price"), param.getMinPrice()));
        }

        // Lọc theo maxPrice
        if (param.getMaxPrice() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("price"), param.getMaxPrice()));
        }

        // Thêm các điều kiện vào query
        cq.where(predicates.toArray(new Predicate[0]));

        // Sắp xếp
        cq.orderBy(cb.desc(root.get("id")));

        // Thực thi query và phân trang
        TypedQuery<ProductDetail> query = entityManager.createQuery(cq);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        // Tổng số lượng
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<ProductDetail> countRoot = countQuery.from(ProductDetail.class);
        countQuery.select(cb.count(countRoot)).where(predicates.toArray(new Predicate[0]));
        long total = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(query.getResultList(), pageable, total);
    }

}
