package org.example.ecommercefashion.services.impl;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.longnh.exceptions.ExceptionHandle;
import com.longnh.utils.FnCommon;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.dtos.filter.OrderParam;
import org.example.ecommercefashion.dtos.request.CartRequest;
import org.example.ecommercefashion.dtos.request.GhtkOrderRequest;
import org.example.ecommercefashion.dtos.request.OrderAddressUpdate;
import org.example.ecommercefashion.dtos.request.OrderAtStoreUpdateRequest;
import org.example.ecommercefashion.dtos.request.OrderChangeState;
import org.example.ecommercefashion.dtos.request.OrderCreateRequest;
import org.example.ecommercefashion.dtos.request.OrderUpdateRequest;
import org.example.ecommercefashion.dtos.response.DiscountResponse;
import org.example.ecommercefashion.dtos.response.GhtkFeeResponse;
import org.example.ecommercefashion.dtos.response.JwtResponse;
import org.example.ecommercefashion.dtos.response.OrderResponse;
import org.example.ecommercefashion.dtos.response.UserResponse;
import org.example.ecommercefashion.entities.Cart;
import org.example.ecommercefashion.entities.EmailJob;
import org.example.ecommercefashion.entities.Order;
import org.example.ecommercefashion.entities.OrderDetail;
import org.example.ecommercefashion.entities.OrderLog;
import org.example.ecommercefashion.entities.ProductDetail;
import org.example.ecommercefashion.entities.User;
import org.example.ecommercefashion.entities.value.Address;
import org.example.ecommercefashion.entities.value.CartValue;
import org.example.ecommercefashion.entities.value.OrderDetailValue;
import org.example.ecommercefashion.enums.OrderStatus;
import org.example.ecommercefashion.enums.PaymentMethodEnum;
import org.example.ecommercefashion.enums.TypeDiscount;
import org.example.ecommercefashion.exceptions.ErrorMessage;
import org.example.ecommercefashion.repositories.OrderDetailRepository;
import org.example.ecommercefashion.repositories.OrderRepository;
import org.example.ecommercefashion.repositories.ProductDetailRepository;
import org.example.ecommercefashion.repositories.UserRepository;
import org.example.ecommercefashion.security.JwtService;
import org.example.ecommercefashion.services.CartService;
import org.example.ecommercefashion.services.DiscountService;
import org.example.ecommercefashion.services.GhtkService;
import org.example.ecommercefashion.services.OrderLogService;
import org.example.ecommercefashion.services.OrderService;
import org.example.ecommercefashion.services.ProductDetailService;
import org.example.ecommercefashion.strategies.TransactionDTO;
import org.example.ecommercefashion.strategies.TransactionRequest;
import org.example.ecommercefashion.strategies.TransactionStrategy;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final ApplicationContext applicationContext;
    private final OrderRepository orderRepository;
    private final JwtService jwtService;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductDetailService productDetailService;
    private final UserRepository userRepository;
    private final GhtkService ghtkService;
    private final OrderLogService orderLogService;
    private final DiscountService discountService;
    private final EmailJob emailJob;
    private final CartService cartService;
    private final ProductDetailRepository productDetailRepository;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderResponse createOrder(OrderCreateRequest dto, String token) {
        Order order = new Order();
        if (token != null) {
            JwtResponse userJWT = jwtService.decodeToken(token);
            User user = getUserById(userJWT.getUserId());
            order.setUser(user);
        }
        order.setCode("HD" + orderRepository.getLastValue());
        order.setStatus(OrderStatus.DRAFT);
        order.setTotalMoney(calculateTotalOrderMoney(dto.getOrderDetails()));
        order.setAddress(new Address());
        order.setPaymentMethod(PaymentMethodEnum.CASH);
        order = orderRepository.save(order);
        order.setOrderDetails(createOrderDetailsWithStockDeduction(dto.getOrderDetails(), order));
        order = orderRepository.save(order);
        return toDto(order);
    }

    @Override
    public OrderResponse updateAddress(Long id, OrderAddressUpdate dto) {
        Order order = getById(id);
        if (order.getStatus() != OrderStatus.DRAFT) {
            throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.ORDER_NOT_IN_DRAFT);
        }
        int quantity = order.getOrderDetails().stream()
                .mapToInt(OrderDetail::getQuantity)
                .sum();

        GhtkOrderRequest ghtkReq = GhtkOrderRequest.builder()
                .totalMoney(order.getTotalMoney())
                .quantity(quantity)
                .toDistrictId(dto.getDistrictID())
                .toWardCode(dto.getWardCode())
                .build();

        GhtkFeeResponse ghtkRes = ghtkService.getShippingFee(ghtkReq);
        double moneyShip = ghtkRes.getData().getService_fee();
        order.setMoneyShip(moneyShip);
        order.setAddress(Address.builder()
                .districtID(dto.getDistrictID())
                .districtName(dto.getDistrictName())
                .provinceID(dto.getProvinceID())
                .provinceName(dto.getProvinceName())
                .wardCode(dto.getWardCode())
                .wardName(dto.getWardName())
                .specificAddress((order.getAddress().getSpecificAddress()))
                .build());

        order = orderRepository.save(order);
        return toDto(order);
    }

    @Override
    public String orderUpdateAndPay(Long id, OrderUpdateRequest dto) throws JobExecutionException {
        TransactionStrategy strategy = (TransactionStrategy) applicationContext.getBean(dto.getPaymentMethod().getVal());
        Order order = getById(id);
        order.setFullName(dto.getFullName());
        order.setPhoneNumber(dto.getPhoneNumber());
        order.setAddress(updateAddress(dto, order));
        order.setNote(dto.getNote());
        if (!validateAddress(order)) {
            throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.INVALID_ADDRESS);
        }
        if (order.getTotalMoney() - order.getDiscountAmount() < 0) {
            throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.INVALID_PAY_AMOUNT);
        }
        String redirect = strategy.processPayment(toDto(order));
        orderRepository.save(order);
        return redirect;
    }

    @Override
    public OrderResponse updateDiscount(Long id, Long discountId) {
        Order order = getById(id);
        if (discountId != null) {
            handleUpdateDiscount(order, discountId);
        } else {
            handleRemoveDiscount(order);
        }
        return toDto(orderRepository.save(order));
    }

    @Override
    public OrderResponse confirmOrder(TransactionRequest request) throws JobExecutionException {
        Order order = getById(request.getOrderId());
        TransactionStrategy strategy = (TransactionStrategy) applicationContext.getBean(request.getPaymentMethod().getVal());
        TransactionDTO dto = TransactionDTO.builder()
                .order(order)
                .confirmationCode(request.getConfirmationCode())
                .status(request.getStatus())
                .build();
        order = strategy.confirmPayment(dto);
        for (OrderDetail orderDetail : order.getOrderDetails()) {
            ProductDetail productDetail =
                    productDetailService.detail(orderDetail.getProductDetail().getId());
            productDetailService.handleMinusQuantity(orderDetail.getQuantity(), productDetail);
        }
        order.setStatus(OrderStatus.PENDING);
        orderRepository.save(order);
        OrderResponse response = toDto(orderRepository.save(order));
        if (response.getUser() != null) {
            updateCartAfterPayment(order);
            emailJob.orderSuccessfulEmail(response);
        }
        return response;
    }

    @Override
    public OrderResponse updateStateOrder(Long id, OrderChangeState dto) {
        Order order = getById(id);
        Order prev = order.clone();

        order.setStatus(dto.getStatus());
        if (dto.getAddress() != null) {
            order.setAddress(dto.getAddress());
            order.setTotalMoney(dto.getTotalMoney());
        }
        if (dto.getPhoneNumber() != null) {
            order.setPhoneNumber(dto.getPhoneNumber());
        }
        if (dto.getNote() != null) {
            order.setNote(dto.getNote());
        }
        if (dto.getPaymentMethod() != null) {
            order.setPaymentMethod(dto.getPaymentMethod());
        }
        if (order.getStatus() == OrderStatus.SUCCESS) {
            order.setSuccessAt(Timestamp.from(Instant.now()));
        }

        orderLogService.create(OrderLog.builder()
                .newValue(order.getStatus())
                .oldStatus(prev.getStatus())
                .order(order)
                .user(order.getUser())
                .build());

        return toDto(orderRepository.save(order));
    }

    @Override
    public void deleteOrder(Long id) {
        Order order =
                orderRepository
                        .findById(id)
                        .orElseThrow(() -> new ExceptionHandle(HttpStatus.NOT_FOUND, "Không tìm thấy order"));
        for (OrderDetail orderDetail : order.getOrderDetails()) {
            orderDetail.setDeleted(true);
            orderDetailRepository.save(orderDetail);
        }
        order.setDeleted(true);
        orderRepository.save(order);
    }

    @Override
    public OrderResponse getOrderById(Long id) {
        return toDto(getById(id));
    }

    @Override
    public Page<OrderResponse> filter(OrderParam param, Pageable pageable) {
        return orderRepository.filter(param, pageable).map(this::toDto);
    }

    @Transactional(rollbackFor = Exception.class)
    public List<OrderDetail> createOrderDetailsWithStockDeduction(
            List<OrderDetailValue> orderDetailValues, Order order) {
        validateStockAvailability(orderDetailValues);

        List<OrderDetail> orderDetails = new ArrayList<>();
        for (OrderDetailValue orderDetailValue : orderDetailValues) {
            ProductDetail productDetail =
                    productDetailService.detail(orderDetailValue.getProductDetailId());

            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setProductDetail(productDetail);
            orderDetail.setQuantity(orderDetailValue.getQuantity());
            orderDetail.setPrice(productDetailService.getPricePromotion(productDetail));
            orderDetail.setTotalMoney(productDetail.getPrice() * orderDetailValue.getQuantity());
            orderDetail.setOrder(order);

            orderDetails.add(orderDetail);
        }
        orderDetailRepository.saveAll(orderDetails);
        return orderDetails;
    }

    public Double calculateTotalOrderMoney(List<OrderDetailValue> orderDetails) {
        double totalMoney = 0;
        for (OrderDetailValue orderDetailValue : orderDetails) {
            ProductDetail productDetail =
                    productDetailService.detail(orderDetailValue.getProductDetailId());
            double price = productDetailService.getPricePromotion(productDetail);
            totalMoney += price * orderDetailValue.getQuantity();
        }

        return totalMoney;
    }

    private void validateStockAvailability(List<OrderDetailValue> orderDetailValues) {
        for (OrderDetailValue orderDetailValue : orderDetailValues) {
            ProductDetail productDetail =
                    productDetailService.detail(orderDetailValue.getProductDetailId());

            int availableQuantity = productDetail.getQuantity();
            int requestedQuantity = orderDetailValue.getQuantity();

            if (requestedQuantity > availableQuantity) {
                throw new ExceptionHandle(HttpStatus.BAD_REQUEST, "Số lượng sản phẩm không đủ");
            }
        }
    }

    private User getUserById(Long id) {
        return userRepository
                .findById(id)
                .orElseThrow(() -> new ExceptionHandle(HttpStatus.NOT_FOUND, "Không tìm thấy user"));
    }

    private Address updateAddress(OrderUpdateRequest dto, Order order) {
        Address address = order.getAddress();
        address.setSpecificAddress(dto.getSpecificAddress());
        return address;
    }

    private boolean validateAddress(Order order) {
        Address address = order.getAddress();
        return address.getProvinceID() != null
                && address.getDistrictID() != null
                && address.getWardCode() != null
                && address.getSpecificAddress() != null;
    }

    private void updateCartAfterPayment(Order order) {
        Long userId = order.getUser().getId();
        Cart cart = cartService.getCartByUserId(userId);

        Map<Long, CartValue> mapCartValue = order.getOrderDetails()
                .stream()
                .collect(Collectors.toMap(
                        o -> o.getProductDetail().getId(),
                        o -> CartValue.builder()
                                .productDetailId(o.getProductDetail().getId())
                                .quantity(o.getQuantity())
                                .build()
                ));

        List<CartValue> cartValues = cart.getCartValues();

        List<CartValue> newCartValues =
                cartValues.stream()
                        .map(c -> {
                            CartValue cv = mapCartValue.get(c.getProductDetailId());
                            if (cv == null) {
                                return c;
                            }
                            Integer quantity = c.getQuantity() - cv.getQuantity();
                            if (quantity <= 0) {
                                return null;
                            }
                            c.setQuantity(quantity);
                            return c;
                        })
                        .filter(Objects::nonNull)
                        .toList();

        CartRequest request = CartRequest.builder()
                .userId(userId)
                .cartValues(newCartValues)
                .build();
        cartService.update(request, userId);
    }

    @Override
    public OrderResponse createOrderAtStore(String token) {
        JwtResponse userJWT = jwtService.decodeToken(token);
        User user = getUserById(userJWT.getUserId());
        if (orderRepository.countOrderPendingStore(user.getId()) >= 4) {
            throw new ExceptionHandle(HttpStatus.BAD_REQUEST, "Đã đạt giới hạn lượng hóa đơn chờ");
        }
        Order order = new Order();
        order.setCode("HD" + orderRepository.getLastValue());
        order.setStatus(OrderStatus.PENDING_AT_STORE);
        order.setStaffId(user.getId());
        order.setFullName("Khách lẻ");
//        ZonedDateTime gmtPlus7Time = ZonedDateTime.now(ZoneId.of("Asia/Bangkok"));
//        order.setCreatedAt(Timestamp.valueOf(gmtPlus7Time.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)));
        order.setPaymentMethod(PaymentMethodEnum.CASH);
        order = orderRepository.save(order);

        return toDto(order);
    }

    @Override
    public List<OrderResponse> getOrderPendingAtStore(String token) {
        JwtResponse userJWT = jwtService.decodeToken(token);
        User user = getUserById(userJWT.getUserId());
        return toDtos(orderRepository.findPendingOrders(OrderStatus.PENDING_AT_STORE, user.getId()));
    }

    public void updateStateOrderAtStore(Long id) {
        Order order = getById(id);
        // Lấy danh sách chi tiết hóa đơn (OrderDetail) liên quan
        List<OrderDetail> orderDetails = orderDetailRepository.findAllByOrder(order);

        // Trừ số lượng sản phẩm
        for (OrderDetail orderDetail : orderDetails) {
            ProductDetail productDetail = orderDetail.getProductDetail();
            int newQuantity = productDetail.getQuantity() - orderDetail.getQuantity();

            if (newQuantity < 0) {
                throw new ExceptionHandle(HttpStatus.BAD_REQUEST,
                        "Không đủ số lượng sản phẩm: " + productDetail.getProduct().getName());
            }

            // Cập nhật số lượng sản phẩm
            productDetail.setQuantity(newQuantity);
            productDetailRepository.save(productDetail);
        }

        // Cập nhật trạng thái đơn hàng thành SUCCESS
        order.setStatus(OrderStatus.SUCCESS);
        order.setSuccessAt(Timestamp.from(Instant.now()));
        orderRepository.save(order);
    }

    @Override
    public byte[] generateOrderPdf(Long orderId) {
        Order order = getById(orderId);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            String fontPath = "src/main/resources/msttcorefonts/Times_New_Roman.ttf";
            PdfFont font = PdfFontFactory.createFont(fontPath, PdfEncodings.IDENTITY_H);

            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            pdf.setDefaultPageSize(new PageSize(200, 600));
            Document document = new Document(pdf);

            document.setFont(font);
            document.setFontSize(8);

            String logoPath = "src/main/resources/msttcorefonts/logo.png";
            ImageData imageData = ImageDataFactory.create(logoPath);
            Image logo = new Image(imageData);
            logo.setHorizontalAlignment(HorizontalAlignment.CENTER);
            logo.setWidth(50);
            logo.setHeight(50);
            document.add(logo);

            document.add(
                    new Paragraph("HÓA ĐƠN BÁN HÀNG")
                            .setTextAlignment(TextAlignment.CENTER)
                            .setBold()
                            .setMarginBottom(10));

            document.add(new Paragraph("Mã đơn hàng: " + order.getCode()).setBold());
            document.add(new Paragraph("Khách hàng: " + order.getFullName()).setBold());
            if(order.getPhoneNumber() != null){
                document.add(new Paragraph("Số ĐT: " + order.getPhoneNumber()).setBold());
            }
            LocalDateTime createdAt = order.getCreatedAt().toLocalDateTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            String formattedDate = createdAt.format(formatter);
            document.add(new Paragraph("Ngày mua: " + formattedDate).setBold());

            Table table = new Table(new float[]{1, 7, 2, 3});
            table.setWidth(UnitValue.createPercentValue(100));
            table.addHeaderCell(
                    new Cell().add(new Paragraph("STT").setBold()).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(
                    new Cell()
                            .add(new Paragraph("Tên sản phẩm").setBold())
                            .setTextAlignment(TextAlignment.LEFT));
            table.addHeaderCell(
                    new Cell().add(new Paragraph("SL").setBold()).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(
                    new Cell()
                            .add(new Paragraph("Thành tiền").setBold())
                            .setTextAlignment(TextAlignment.RIGHT));

            int index = 1;
            double totalWithoutDiscount = 0.0;
            for (var detail : order.getOrderDetails()) {
                double lineTotal = detail.getPrice() * detail.getQuantity();
                totalWithoutDiscount += lineTotal;

                table.addCell(
                        new Cell()
                                .add(new Paragraph(String.valueOf(index++)))
                                .setTextAlignment(TextAlignment.CENTER));
                table.addCell(
                        new Cell()
                                .add(new Paragraph(detail.getProductDetail().getProduct().getName()))
                                .setTextAlignment(TextAlignment.LEFT));
                table.addCell(
                        new Cell()
                                .add(new Paragraph(String.valueOf(detail.getQuantity())))
                                .setTextAlignment(TextAlignment.CENTER));
                table.addCell(
                        new Cell()
                                .add(new Paragraph(String.format("%,.0f", lineTotal)))
                                .setTextAlignment(TextAlignment.RIGHT));
            }
            document.add(table);

            double discount = order.getDiscountAmount() != null ? order.getDiscountAmount() : 0.0;
            double totalWithDiscount = totalWithoutDiscount - discount;

            document.add(
                    new Paragraph("Tổng tiền hoá đơn: " + String.format("%,.0f VNĐ", totalWithoutDiscount))
                            .setTextAlignment(TextAlignment.RIGHT)
                            .setBold()
                            .setMarginTop(10));
            if (discount > 0) {
                document.add(
                        new Paragraph("Khuyến mãi: -" + String.format("%,.0f VNĐ", discount))
                                .setTextAlignment(TextAlignment.RIGHT)
                                .setBold());
            }
            document
                    .add(
                            new Paragraph("Số tiền thanh toán: " + String.format("%,.0f VNĐ", totalWithDiscount))
                                    .setTextAlignment(TextAlignment.RIGHT)
                                    .setBold())
                    .setTopMargin(10);

            String imageUrlBank = genImageBanking(order.getCode(), totalWithDiscount);
            Image image = new Image(ImageDataFactory.create(imageUrlBank));

            image.setWidth(100);
            image.setHeight(100);
            image.setHorizontalAlignment(HorizontalAlignment.CENTER);
            document.add(image);

            document.close();
        } catch (Exception e) {
            throw new ExceptionHandle(HttpStatus.BAD_REQUEST, "Lỗi xuất hoá đơn");
        }

        return out.toByteArray();
    }

    @Override
    public OrderResponse updateOrderAtStore(Long id, OrderAtStoreUpdateRequest request) {
        Order order = getById(id);

        if (request.getIdGuest() != null) {
            User user = getUserById(request.getIdGuest());
            order.setUser(user);
            order.setFullName(user.getFullName());
        }

        if (request.getIdDiscount() != null) {
            DiscountResponse discount = discountService.getByDiscountId(request.getIdDiscount());
            order.setDiscountId(discount.getId());
            if (discount.getType().equals(TypeDiscount.PERCENTAGE)) {
                order.setDiscountAmount(order.getTotalMoney() * discount.getValue());
                if (order.getDiscountAmount() >= discount.getMaxValue()) {
                    order.setDiscountAmount(discount.getMaxValue());
                }
            } else if (discount.getType().equals(TypeDiscount.FIXED_AMOUNT)) {
                order.setDiscountAmount(discount.getValue());
            }
        }
        return toDto(orderRepository.save(order));
    }

    private static String genImageBanking(String code, Double amount) {
        return String.format(
                "https://img.vietqr.io/image/%s-%s-compact2.jpg?amount=%.2f&addInfo=%s&accountName=%s",
                "tpb",
                "99992036666",
                amount,
                code,
                "NGUYEN HAI LONG".replace(" ", "%20"),
                "Ngân hàng TMCP Tiên Phong".replace(" ", "%20"));
    }

    private Order handleUpdateDiscount(Order order, Long discountId) {
        DiscountResponse discount = discountService.getByDiscountId(discountId);
        order.setDiscountId(discountId);
        Double total = order.getTotalMoney();
        if (discount.getType() == TypeDiscount.PERCENTAGE) {
            Double value = discount.getValue();
            Double discountAmount = Math.min(total * (value / 100), discount.getMaxValue());
            order.setDiscountAmount(discountAmount);
        } else {
            Double discountAmount = discount.getValue();
            order.setDiscountAmount(discountAmount);
        }
        return order;
    }

    private Order handleRemoveDiscount(Order order) {
        order.setDiscountId(null);
        order.setDiscountAmount(0.0);
        return order;
    }

    private Order getById(Long id) {
        return orderRepository
                .findById(id)
                .orElseThrow(() ->
                        new ExceptionHandle(HttpStatus.NOT_FOUND, "Không tìm thấy order"));
    }

    private List<OrderResponse> toDtos(Collection<Order> entities) {
        return entities.stream()
                .map(this::toDto)
                .toList();
    }

    private OrderResponse toDto(Order entity) {
        UserResponse user = FnCommon.copyNonNullProperties(UserResponse.class, entity.getUser());
        OrderResponse response = FnCommon.copyProperties(OrderResponse.class, entity);
        response.setUser(user);
        response.setPayAmount((entity.getTotalMoney() - entity.getDiscountAmount()) + entity.getMoneyShip());
        response.setRevenueAmount(entity.getTotalMoney() - entity.getDiscountAmount());
        return response;
    }
}