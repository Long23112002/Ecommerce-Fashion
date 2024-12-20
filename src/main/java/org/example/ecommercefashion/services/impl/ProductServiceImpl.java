package org.example.ecommercefashion.services.impl;

import static org.example.ecommercefashion.annotations.normalized.normalizeString;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.longnh.exceptions.ExceptionHandle;
import com.longnh.utils.FnCommon;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.ecommercefashion.dtos.filter.ProductParam;
import org.example.ecommercefashion.dtos.request.ExcelDto;
import org.example.ecommercefashion.dtos.request.ProductRequest;
import org.example.ecommercefashion.dtos.response.JwtResponse;
import org.example.ecommercefashion.dtos.response.MessageResponse;
import org.example.ecommercefashion.dtos.response.ResponsePage;
import org.example.ecommercefashion.entities.*;
import org.example.ecommercefashion.entities.Color;
import org.example.ecommercefashion.entities.value.Identifiable;
import org.example.ecommercefashion.entities.value.UserInfo;
import org.example.ecommercefashion.entities.value.UserValue;
import org.example.ecommercefashion.enums.promotion.StatusPromotionEnum;
import org.example.ecommercefashion.exceptions.AttributeErrorMessage;
import org.example.ecommercefashion.exceptions.ErrorMessage;
import org.example.ecommercefashion.repositories.*;
import org.example.ecommercefashion.security.JwtService;
import org.example.ecommercefashion.services.ProductService;
import org.example.ecommercefashion.utils.ExcelCommon;
import org.example.ecommercefashion.utils.InMemoryMultipartFile;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
  private final UserRepository userRepository;
  private final JwtService jwtService;
  private final ProductRepository productRepository;
  private final BrandRepository brandRepository;
  private final CategoryRepository categoryRepository;
  private final MaterialRepository materialRepository;
  private final OriginRepository originRepository;
  private final SizeRepository sizeRepository;
  private final ColorRepository colorRepository;
  private final ProductDetailRepository productDetailRepository;
  private final RestTemplate restTemplate;

  @PersistenceContext private EntityManager entityManager;

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

  private Product findById(Long id) {
    return productRepository
        .findById(id)
        .orElseThrow(
            () -> new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.PRODUCT_NOT_FOUND));
  }

  @Override
  public ResponsePage<Product, Product> filterProduct(ProductParam param, Pageable pageable) {
    Page<Product> productPage = buildQuery(param, pageable);
    Page<Product> responses = productPage.map(this::toDto);

    return new ResponsePage<>(responses);
  }

  @Override
  public ResponsePage<Product, Product> similarProduct(Long id, Pageable pageable) {
    Product product = getProductById(id);
    Page<Product> similarProducts = productRepository.findSimilarProducts(product, pageable);
    Page<Product> responses = similarProducts.map(this::toDto);

    return new ResponsePage<>(responses);
  }

  @Override
  public ResponsePage<Product, Product> hotProducts(Pageable pageable) {
    List<Product> hotProducts =
        productRepository.findHotProducts(pageable).stream().map(this::toDto).toList();
    Page<Product> responses = new PageImpl<>(hotProducts);

    return new ResponsePage<>(responses);
  }

  @Override
  public ResponsePage<Product, Product> productInPromotion(Pageable pageable) {
    Page<Product> pip = productRepository.findProductInPromotion(pageable);
    Page<Product> responses = pip.map(this::toDto);

    return new ResponsePage<>(responses);
  }

  @Override
  public Product createProduct(ProductRequest request, String token) {
    if (token != null) {
      JwtResponse jwtResponse = jwtService.decodeToken(token);

      if (productRepository.existsByNameIgnoreCase(request.getName().trim())) {
        throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.PRODUCT_NAME_EXISTED);
      }

      Brand brand =
          brandRepository
              .findById(request.getIdBrand())
              .orElseThrow(
                  () -> new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.BRAND_NOT_FOUND));
      Category category =
          categoryRepository
              .findById(request.getIdCategory())
              .orElseThrow(
                  () ->
                      new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.CATEGORY_NOT_FOUND));
      Material material =
          materialRepository
              .findById(request.getIdMaterial())
              .orElseThrow(
                  () ->
                      new ExceptionHandle(
                          HttpStatus.BAD_REQUEST, AttributeErrorMessage.MATERIAL_NOT_FOUND));
      Origin origin =
          originRepository
              .findById(request.getIdOrigin())
              .orElseThrow(
                  () -> new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.ORIGIN_NOT_FOUND));

      Product productCreate = new Product();
      FnCommon.copyNonNullProperties(productCreate, request);
      productCreate.setCode("PH" + productRepository.getLastValue());
      productCreate.setCreateBy(jwtResponse.getUserId());
      productCreate.setBrand(brand);
      productCreate.setCategory(category);
      productCreate.setMaterial(material);
      productCreate.setOrigin(origin);
      productCreate.setProductDetails(new ArrayList<>());

      productCreate.setCreateByUser(getInfoUserValue(jwtResponse.getUserId()));
      productRepository.save(productCreate);

      return toDto(productCreate);
    } else {
      throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.USER_NOT_FOUND);
    }
  }

  @Override
  public Product updateProduct(Long id, ProductRequest request, String token) {
    if (token != null) {
      JwtResponse jwtResponse = jwtService.decodeToken(token);
      Product product =
          productRepository
              .findById(id)
              .orElseThrow(
                  () ->
                      new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.PRODUCT_NOT_FOUND));

      String normalizedProductName;
      try {
        normalizedProductName = normalizeString(request.getName());
      } catch (IOException e) {
        throw new RuntimeException("Failed to normalize string", e);
      }
      if (productRepository.existsByNameAndIdNot(normalizedProductName, product.getId())) {
        throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.PRODUCT_NAME_EXISTED);
      }

      Brand brand =
          brandRepository
              .findById(request.getIdBrand())
              .orElseThrow(
                  () -> new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.BRAND_NOT_FOUND));
      Category category =
          categoryRepository
              .findById(request.getIdCategory())
              .orElseThrow(
                  () ->
                      new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.CATEGORY_NOT_FOUND));
      Material material =
          materialRepository
              .findById(request.getIdMaterial())
              .orElseThrow(
                  () ->
                      new ExceptionHandle(
                          HttpStatus.BAD_REQUEST, AttributeErrorMessage.MATERIAL_NOT_FOUND));
      Origin origin =
          originRepository
              .findById(request.getIdOrigin())
              .orElseThrow(
                  () -> new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.ORIGIN_NOT_FOUND));

      FnCommon.copyNonNullProperties(product, request);
      product.setDescription(request.getDescription());
      product.setUpdateBy(jwtResponse.getUserId());
      product.setBrand(brand);
      product.setCategory(category);
      product.setMaterial(material);
      product.setOrigin(origin);
      if (request.getImage().isEmpty()) {
        product.setImage(product.getImage());
      } else {
        product.setImage(request.getImage());
      }

      product.setCreateByUser(getInfoUserValue(product.getCreateBy()));
      product.setUpdateByUser(getInfoUserValue(jwtResponse.getUserId()));
      productRepository.save(product);

      return toDto(product);
    } else {
      throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.USER_NOT_FOUND);
    }
  }

  @Override
  public Product getProductById(Long id) {
    Product product = findById(id);
    return toDto(product);
  }

  @Override
  public MessageResponse updateStatus(Long id) {
    Product product = findById(id);
    if (productDetailRepository.existsByProduct(product)) {
      throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.PRODUCT_HAS_PRODUCT_DETAIL);
    }
    product.setDeleted(true);
    productRepository.save(product);
    return MessageResponse.builder().message("Product deleted successfully").build();
  }

  @Override
  public byte[] exSampleTemplate() throws IOException {
    Workbook workbook = new XSSFWorkbook();
    Sheet sheet = workbook.createSheet("Import Template");
    createHeaderRow(sheet);

    List<String> categoryNames = loadEntityNamesByPage(0, categoryRepository, Category::getName);
    setupDataValidation(sheet, categoryNames.toArray(new String[0]), 1, 100, 1, 1);

    List<String> brandNames = loadEntityNamesByPage(0, brandRepository, Brand::getName);
    setupDataValidation(sheet, brandNames.toArray(new String[0]), 1, 100, 2, 2);

    List<String> materialNames = loadEntityNamesByPage(0, materialRepository, Material::getName);
    setupDataValidation(sheet, materialNames.toArray(new String[0]), 1, 100, 3, 3);

    List<String> originNames = loadEntityNamesByPage(0, originRepository, Origin::getName);
    setupDataValidation(sheet, originNames.toArray(new String[0]), 1, 100, 4, 4);

    List<String> sizeNames = loadEntityNamesByPage(0, sizeRepository, Size::getName);
    setupDataValidation(sheet, sizeNames.toArray(new String[0]), 1, 100, 8, 8);

    List<String> colorNames = loadEntityNamesByPage(0, colorRepository, Color::getName);
    setupDataValidation(sheet, colorNames.toArray(new String[0]), 1, 100, 9, 9);

    createSampleData(
        sheet, categoryNames, brandNames, materialNames, originNames, sizeNames, colorNames);

    return writeWorkbookToByteArray(workbook);
  }

  private void createSampleData(
      Sheet sheet,
      List<String> categoryNames,
      List<String> brandNames,
      List<String> materialNames,
      List<String> originNames,
      List<String> sizeNames,
      List<String> colorNames) {
    Row row = sheet.createRow(1);

    row.createCell(0).setCellValue("Sản phẩm mẫu");
    row.createCell(1).setCellValue(categoryNames.isEmpty() ? "" : categoryNames.get(0));
    row.createCell(2).setCellValue(brandNames.isEmpty() ? "" : brandNames.get(0));
    row.createCell(3).setCellValue(materialNames.isEmpty() ? "" : materialNames.get(0));
    row.createCell(4).setCellValue(originNames.isEmpty() ? "" : originNames.get(0));
    row.createCell(5).setCellValue("Mô tả sản phẩm mẫu");
    row.createCell(6).setCellValue("1000000");
    row.createCell(7).setCellValue("100");
    row.createCell(8).setCellValue(sizeNames.isEmpty() ? "" : sizeNames.get(0));
    row.createCell(9).setCellValue(colorNames.isEmpty() ? "" : colorNames.get(0));

    Row row2 = sheet.createRow(2);
    row2.createCell(6).setCellValue("2000000");
    row2.createCell(7).setCellValue("100");
    row2.createCell(8).setCellValue(sizeNames.isEmpty() ? "" : sizeNames.get(1));
    row2.createCell(9).setCellValue(colorNames.isEmpty() ? "" : colorNames.get(1));

    autoSizeAndCenterColumns(sheet, 50);
  }

  private void createHeaderRow(Sheet sheet) {
    Row headerRow = sheet.createRow(0);
    String[] columns = {
      "Tên sản phẩm *",
      "Danh mục *",
      "Thương hiệu *",
      "Chất liệu *",
      "Xuất xứ *",
      "Mô tả sản phẩm",
      "Giá tiền *",
      "Số lượng *",
      "Kích thước *",
      "Màu sắc *",
    };

    CellStyle headerStyle = sheet.getWorkbook().createCellStyle();
    Font headerFont = sheet.getWorkbook().createFont();
    headerFont.setBold(true);
    headerStyle.setFont(headerFont);
    headerStyle.setAlignment(HorizontalAlignment.CENTER);

    Font redFont = sheet.getWorkbook().createFont();
    redFont.setColor(IndexedColors.RED.getIndex());

    for (int i = 0; i < columns.length; i++) {
      Cell cell = headerRow.createCell(i);

      RichTextString richTextString = styleRedHeader(columns[i], headerFont, redFont);

      cell.setCellValue(richTextString);
      cell.setCellStyle(headerStyle);
    }
  }

  private RichTextString styleRedHeader(String text, Font defaultFont, Font redFont) {
    RichTextString richTextString = new XSSFRichTextString(text);
    int starIndex = text.indexOf("*");

    if (starIndex != -1) {
      richTextString.applyFont(0, starIndex, defaultFont);
      richTextString.applyFont(starIndex, starIndex + 1, redFont);
      if (starIndex + 1 < text.length()) {
        richTextString.applyFont(starIndex + 1, text.length(), defaultFont);
      }
    } else {
      richTextString.applyFont(defaultFont);
    }

    return richTextString;
  }

  private byte[] writeWorkbookToByteArray(Workbook workbook) throws IOException {
    try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
      workbook.write(byteArrayOutputStream);
      return byteArrayOutputStream.toByteArray();
    }
  }

  private void autoSizeAndCenterColumns(Sheet sheet, int maxWidth) {
    int noOfColumns = sheet.getRow(0).getPhysicalNumberOfCells();
    for (int i = 0; i < noOfColumns; i++) {
      sheet.autoSizeColumn(i);
      int currentWidth = sheet.getColumnWidth(i);
      if (currentWidth > maxWidth * 256) {
        sheet.setColumnWidth(i, maxWidth * 256);
      }
    }
  }

  private void setupDataValidation(
      Sheet sheet, String[] values, int firstRow, int lastRow, int firstCol, int lastCol) {
    DataValidationHelper validationHelper = sheet.getDataValidationHelper();
    DataValidationConstraint constraint = validationHelper.createExplicitListConstraint(values);
    CellRangeAddressList addressList =
        new CellRangeAddressList(firstRow, lastRow, firstCol, lastCol);
    DataValidation validation = validationHelper.createValidation(constraint, addressList);
    validation.setShowErrorBox(true);
    sheet.addValidationData(validation);
  }

  public <T extends Identifiable> List<String> loadEntityNamesByPage(
      int pageNumber, JpaRepository<T, Long> repository, Function<T, String> nameMapper) {
    Pageable pageable = PageRequest.of(pageNumber, 100);
    Page<T> entityPage = repository.findAll(pageable);
    return entityPage.stream()
        .map(entity -> entity.getId() + " - " + nameMapper.apply(entity))
        .collect(Collectors.toList());
  }

  private Page<Product> buildQuery(ProductParam param, Pageable pageable) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Product> query = cb.createQuery(Product.class);
    Root<Product> product = query.from(Product.class);

    List<Predicate> predicates = new ArrayList<>();

    if (!param.isAllowEmpty()) {
      predicates.add(cb.isNotEmpty(product.get("productDetails")));
    }
    if (param.getIdMaterial() != null) {
      predicates.add(cb.equal(product.get("material").get("id"), param.getIdMaterial()));
    }
    if (param.getIdBrand() != null) {
      predicates.add(cb.equal(product.get("brand").get("id"), param.getIdBrand()));
    }
    if (param.getIdCategory() != null) {
      predicates.add(cb.equal(product.get("category").get("id"), param.getIdCategory()));
    }
    if (param.getIdOrigin() != null) {
      predicates.add(cb.equal(product.get("origin").get("id"), param.getIdOrigin()));
    }
    if (param.getKeyword() != null && !param.getKeyword().isEmpty()) {
      String keywordPattern = "%" + param.getKeyword().toLowerCase() + "%";
      predicates.add(
          cb.or(
              cb.like(cb.lower(product.get("name")), keywordPattern),
              cb.like(cb.lower(product.get("brand").get("name")), keywordPattern),
              cb.like(cb.lower(product.get("material").get("name")), keywordPattern),
              cb.like(cb.lower(product.get("category").get("name")), keywordPattern),
              cb.like(cb.lower(product.get("origin").get("name")), keywordPattern)));
    }
    if (param.getCode() != null && !param.getCode().isEmpty()) {
      String codePattern = "%" + param.getCode().toLowerCase() + "%";
      predicates.add(cb.like(cb.lower(product.get("code")), codePattern));
    }
    if (param.getMinPrice() != null) {
      predicates.add(cb.greaterThanOrEqualTo(product.get("minPrice"), param.getMinPrice()));
    }
    if (param.getMaxPrice() != null) {
      predicates.add(cb.lessThanOrEqualTo(product.get("maxPrice"), param.getMaxPrice()));
    }
    if (param.getIdColors() != null && !param.getIdColors().isEmpty()) {
      Subquery<Long> colorSubquery = query.subquery(Long.class);
      Root<ProductDetail> productDetail = colorSubquery.from(ProductDetail.class);
      colorSubquery
          .select(productDetail.get("product").get("id"))
          .where(productDetail.get("color").get("id").in(param.getIdColors()));
      predicates.add(product.get("id").in(colorSubquery));
    }
    if (param.getIdSizes() != null && !param.getIdSizes().isEmpty()) {
      Subquery<Long> sizeSubquery = query.subquery(Long.class);
      Root<ProductDetail> productDetail = sizeSubquery.from(ProductDetail.class);
      sizeSubquery
          .select(productDetail.get("product").get("id"))
          .where(productDetail.get("size").get("id").in(param.getIdSizes()));
      predicates.add(product.get("id").in(sizeSubquery));
    }

    query.where(cb.and(predicates.toArray(new Predicate[0])));

    List<Order> orders = new ArrayList<>();
    pageable
        .getSort()
        .forEach(
            order -> {
              String property = order.getProperty();
              if (order.isAscending()) {
                orders.add(cb.asc(product.get(property)));
              } else {
                orders.add(cb.desc(product.get(property)));
              }
            });

    if (orders.isEmpty()) {
      orders.add(cb.desc(product.get("id")));
    }

    query.orderBy(orders);

    List<Product> products =
        entityManager
            .createQuery(query)
            .setFirstResult((int) pageable.getOffset())
            .setMaxResults(pageable.getPageSize())
            .getResultList();

    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
    Root<Product> countRoot = countQuery.from(Product.class);
    countQuery.select(cb.count(countRoot)).where(cb.and(predicates.toArray(new Predicate[0])));
    Long count = entityManager.createQuery(countQuery).getSingleResult();

    return new PageImpl<>(products, pageable, count);
  }

  @SneakyThrows
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void importData(MultipartFile file, String token) {
    InputStream inputStream = file.getInputStream();
    Workbook workbook = new XSSFWorkbook(inputStream);
    Sheet sheet = workbook.getSheetAt(0);

    // Kiểm tra template
    validateTemplate(sheet);

    JwtResponse jwtResponse = jwtService.decodeToken(token);

    // Thêm tiêu đề "Kết quả" ở cột thứ 10 (cột K)
    Row headerRow = sheet.getRow(0);
    if (headerRow == null) {
      headerRow = sheet.createRow(0);
    }
    Cell resultHeaderCell = headerRow.createCell(10, CellType.STRING);
    resultHeaderCell.setCellValue("Kết quả");

    // Khai báo các biến
    Long currentProductId = null;
    int savedProductCount = 0;
    int failedProductCount = 0;
    int totalRows = sheet.getLastRowNum();

    // Tạo style kết quả
    CellStyle successStyle = createCellStyle(workbook, IndexedColors.GREEN);
    CellStyle errorStyle = createCellStyle(workbook, IndexedColors.RED);

    for (int i = 1; i <= totalRows; i++) {
      Row row = sheet.getRow(i);
      if (row == null) {
        continue;
      }

      StringBuilder resultMessage = new StringBuilder(); // Tạo mới StringBuilder cho mỗi dòng
      Cell resultCell = row.createCell(10, CellType.STRING);

      try {
        // Xóa nội dung của StringBuilder trước khi validate
        resultMessage.setLength(0);

        // Validate dữ liệu
        validateRow(row, resultMessage);

        // Nếu có lỗi validate, ném ngoại lệ
        if (resultMessage.length() > 0) {
          throw new Exception(resultMessage.toString());
        }

        // Xử lý sản phẩm mới
        boolean isNewProduct =
            row.getCell(0) != null && !row.getCell(0).getStringCellValue().isEmpty();
        if (isNewProduct) {
          String productName = ExcelCommon.convertCell("name", String.class, row.getCell(0));
          String categoryName = ExcelCommon.convertCell("category", String.class, row.getCell(1));
          String brandName = ExcelCommon.convertCell("brand", String.class, row.getCell(2));
          String materialName = ExcelCommon.convertCell("material", String.class, row.getCell(3));
          String originName = ExcelCommon.convertCell("origin", String.class, row.getCell(4));
          String description = ExcelCommon.convertCell("description", String.class, row.getCell(5));

          Product product =
              buildProduct(
                  productName,
                  categoryName,
                  brandName,
                  materialName,
                  originName,
                  description,
                  jwtResponse);
          currentProductId = product.getId();
          savedProductCount++;
        }

        // Xử lý chi tiết sản phẩm
        if (currentProductId != null) {
          Double price = ExcelCommon.convertCell("price", Double.class, row.getCell(6));
          Integer quantity = ExcelCommon.convertCell("quantity", Integer.class, row.getCell(7));
          String sizeName = ExcelCommon.convertCell("size", String.class, row.getCell(8));
          String colorName = ExcelCommon.convertCell("color", String.class, row.getCell(9));

          buildProductDetail(price, quantity, sizeName, colorName, currentProductId, jwtResponse);
          resultCell.setCellValue("Thành công đã lưu vào hệ thống.");
          resultCell.setCellStyle(successStyle);
        } else {
          throw new Exception("Không tìm thấy sản phẩm.");
        }
      } catch (Exception e) {
        failedProductCount++;
        resultCell.setCellValue("Lỗi: " + e.getMessage());
        resultCell.setCellStyle(errorStyle);
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      }
    }

    sheet.autoSizeColumn(10);

    // Ghi workbook thành file kết quả
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    workbook.write(outputStream);
    byte[] byteArray = outputStream.toByteArray();

    workbook.close();
    inputStream.close();
    outputStream.close();

    MultipartFile fileResult =
        new InMemoryMultipartFile(
            "fileResult",
            "result.xlsx",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            byteArray);

    // Gửi file kết quả
    User user = getUserId(jwtResponse.getUserId());
    sendExcelData(
        "http://ecommerce-fashion.site:9099/api/v1/files/upload",
        ExcelDto.builder()
            .count(totalRows)
            .success(savedProductCount)
            .error(failedProductCount)
            .file(file)
            .fileResult(fileResult)
            .process(100L)
            .objectName("EXCEL_IMPORT_PRODUCT")
            .typeFile(failedProductCount > 0 ? "ERROR" : "SUCCESS")
            .userInfo(
                new UserInfo(user.getId(), user.getEmail(), user.getFullName(), user.getAvatar()))
            .build());
  }

  private void validateRow(Row row, StringBuilder resultMessage) {
    Set<String> errorMessages = new LinkedHashSet<>(); // Dùng LinkedHashSet để giữ thứ tự lỗi

    // Đọc dữ liệu từ các cột Excel
    String productName = ExcelCommon.convertCell("name", String.class, row.getCell(0));
    String categoryName = ExcelCommon.convertCell("category", String.class, row.getCell(1));
    String brandName = ExcelCommon.convertCell("brand", String.class, row.getCell(2));
    String materialName = ExcelCommon.convertCell("material", String.class, row.getCell(3));
    String originName = ExcelCommon.convertCell("origin", String.class, row.getCell(4));
    Double price = ExcelCommon.convertCell("price", Double.class, row.getCell(6));
    Integer quantity = ExcelCommon.convertCell("quantity", Integer.class, row.getCell(7));
    String sizeName = ExcelCommon.convertCell("size", String.class, row.getCell(8));
    String colorName = ExcelCommon.convertCell("color", String.class, row.getCell(9));

    if (categoryName != null || brandName != null || materialName != null || originName != null) {

      if (productName == null || productName.isEmpty()) {
        errorMessages.add("Lỗi: Tên sản phẩm không được để trống.\n");
      }
    }
    if (productName != null) {
      if (categoryName == null || categoryName.isEmpty()) {
        errorMessages.add("Lỗi: Danh mục không được để trống.\n");
      }
      if (brandName == null || brandName.isEmpty()) {
        errorMessages.add("Lỗi: Thương hiệu không được để trống.\n");
      }
      if (materialName == null || materialName.isEmpty()) {
        errorMessages.add("Lỗi: Chất liệu không được để trống.\n");
      }
      if (originName == null || originName.isEmpty()) {
        errorMessages.add("Lỗi: Xuất xứ không được để trống.\n");
      }
    } else if (productRepository.existsByName(productName)) {
      errorMessages.add("Lỗi: Tên sản phẩm đã tồn tại.\n");
    }

    if (price == null || price <= 0) {
      errorMessages.add("Lỗi: Giá phải lớn hơn 0.\n");
    }
    if (quantity == null || quantity <= 0) {
      errorMessages.add("Lỗi: Số lượng phải lớn hơn 0.\n");
    }
    if (sizeName == null || sizeName.isEmpty()) {
      errorMessages.add("Lỗi: Kích thước không được để trống.\n");
    }
    if (colorName == null || colorName.isEmpty()) {
      errorMessages.add("Lỗi: Màu sắc không được để trống.\n");
    }
    // 4. Ghi kết quả vào ô thứ 10 (cột K)
    Cell resultCell = row.createCell(10, CellType.STRING);
    if (!errorMessages.isEmpty()) {
      resultMessagesToString(errorMessages, resultMessage); // Kết hợp lỗi vào resultMessage
      resultCell.setCellValue(resultMessage.toString());
    } else {
      resultCell.setCellValue("Thành công"); // Không có lỗi
    }
  }

  private void convertResultMessagesToString(
      Set<String> errorMessages, StringBuilder resultMessage) {
    for (String error : errorMessages) {
      if (resultMessage.length() > 0) {
        resultMessage.append("; "); // Ngăn cách giữa các lỗi
      }
      resultMessage.append(error);
    }
  }

  @Override
  public byte[] exportData(Pageable pageable, ProductParam param) throws IOException {
    Workbook workbook = new XSSFWorkbook();
    Sheet sheet = workbook.createSheet("Import Template");

    createHeaderRow(sheet);

    Page<Product> filter = buildQuery(param, pageable);

    int rowIndex = 1;
    for (Product product : filter.getContent()) {
      boolean isFirstDetailRow = true;
      for (ProductDetail detail : product.getProductDetails()) {
        Row row = sheet.createRow(rowIndex++);

        if (isFirstDetailRow) {
          row.createCell(0).setCellValue(formatWithIdAndName(product.getId(), product.getName()));
          row.createCell(1)
              .setCellValue(
                  formatWithIdAndName(
                      product.getCategory().getId(), product.getCategory().getName()));
          row.createCell(2)
              .setCellValue(
                  formatWithIdAndName(product.getBrand().getId(), product.getBrand().getName()));
          row.createCell(3)
              .setCellValue(
                  formatWithIdAndName(
                      product.getMaterial().getId(), product.getMaterial().getName()));
          row.createCell(4)
              .setCellValue(
                  formatWithIdAndName(product.getOrigin().getId(), product.getOrigin().getName()));
          row.createCell(5).setCellValue(product.getDescription());

          isFirstDetailRow = false;
        }

        row.createCell(6).setCellValue(detail.getPrice());
        row.createCell(7).setCellValue(detail.getQuantity());
        row.createCell(8)
            .setCellValue(
                formatWithIdAndName(detail.getSize().getId(), detail.getSize().getName()));
        row.createCell(9)
            .setCellValue(
                formatWithIdAndName(detail.getColor().getId(), detail.getColor().getName()));
      }
    }

    autoSizeAndCenterColumns(sheet, 50);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    workbook.write(bos);
    bos.close();
    return bos.toByteArray();
  }

  @Transactional(rollbackFor = Exception.class, propagation = Propagation.MANDATORY)
  public Product buildProduct(
      String productName,
      String categoryName,
      String brandName,
      String materialName,
      String originName,
      String description,
      JwtResponse jwtResponse) {
    Product product = new Product();
    product.setName(productName);
    product.setDescription(description);
    product.setCode("PH" + productRepository.getLastValue());
    product.setBrand(brandRepository.getById(parseIdFromInfo(brandName)));
    product.setCategory(categoryRepository.getById(parseIdFromInfo(categoryName)));
    product.setOrigin(originRepository.getById(parseIdFromInfo(originName)));
    product.setMaterial(materialRepository.getById(parseIdFromInfo(materialName)));
    product.setCreateBy(jwtResponse.getUserId());
    return productRepository.save(product);
  }

  @Transactional(rollbackFor = Exception.class, propagation = Propagation.MANDATORY)
  public void buildProductDetail(
      Double price,
      Integer quantity,
      String sizeName,
      String colorName,
      Long currentProductId,
      JwtResponse jwtResponse) {
    ProductDetail productDetail = new ProductDetail();
    productDetail.setPrice(price);
    productDetail.setQuantity(quantity);
    productDetail.setCreateBy(jwtResponse.getUserId());
    productDetail.setProduct(productRepository.getById(currentProductId));
    productDetail.setSize(sizeRepository.getById(parseIdFromInfo(sizeName)));
    productDetail.setColor(colorRepository.getById(parseIdFromInfo(colorName)));
    productDetailRepository.save(productDetail);
    updateProductPriceRange(currentProductId);
  }

  private void updateProductPriceRange(Long productId) {
    List<ProductDetail> productDetails = productDetailRepository.getDetailByIdProduct(productId);

    Double minPrice =
        productDetails.stream().map(ProductDetail::getPrice).min(Double::compare).orElse(null);

    Double maxPrice =
        productDetails.stream().map(ProductDetail::getPrice).max(Double::compare).orElse(null);

    Product product =
        productRepository
            .findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));
    product.setMinPrice(minPrice != null ? minPrice.longValue() : null);
    product.setMaxPrice(maxPrice != null ? maxPrice.longValue() : null);
    productRepository.save(product);
  }

  public void sendExcelData(String apiUrl, ExcelDto excelDto) throws IOException {
    if (excelDto.getFile() == null || excelDto.getFileResult() == null) {
      throw new IllegalArgumentException("Both file and fileResult must not be null.");
    }

    MultiValueMap<String, Object> body = getStringObjectMultiValueMap(excelDto);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);

    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

    try {
      ResponseEntity<String> response =
          restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, String.class);

      if (response.getStatusCode() == HttpStatus.OK) {
        System.out.println("File uploaded successfully: " + response.getBody());
      } else {
        System.out.println("Error uploading file: " + response.getStatusCode());
      }
    } catch (HttpClientErrorException | HttpServerErrorException e) {
      System.out.println(e.getMessage());
    }
  }

  private static MultiValueMap<String, Object> getStringObjectMultiValueMap(ExcelDto excelDto)
      throws IOException {
    MultipartFile multipartFile = excelDto.getFile();
    MultipartFile multipartFileResult = excelDto.getFileResult();

    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

    body.add(
        "file",
        new ByteArrayResource(multipartFile.getBytes()) {
          @Override
          public String getFilename() {
            return multipartFile.getOriginalFilename();
          }
        });

    body.add(
        "fileResult",
        new ByteArrayResource(multipartFileResult.getBytes()) {
          @Override
          public String getFilename() {
            return multipartFileResult.getOriginalFilename();
          }
        });

    body.add("objectName", excelDto.getObjectName());
    body.add("count", excelDto.getCount());
    body.add("success", excelDto.getSuccess());
    body.add("error", excelDto.getError());
    body.add("typeFile", excelDto.getTypeFile());
    body.add("process", excelDto.getProcess());
    body.add("description", excelDto.getDescription());

    ObjectMapper objectMapper = new ObjectMapper();
    String userInfoJson = objectMapper.writeValueAsString(excelDto.getUserInfo());
    body.add("userInfo", userInfoJson);
    return body;
  }

  private User getUserId(Long id) {
    return userRepository
        .findById(id)
        .orElseThrow(() -> new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.USER_NOT_FOUND));
  }

  private Long parseIdFromInfo(String info) {
    String[] parts = info.split(" - ");
    return Long.parseLong(parts[0].trim());
  }

  // Hàm chuyển Set lỗi thành chuỗi kết quả
  private void resultMessagesToString(Set<String> errorMessages, StringBuilder resultMessage) {
    for (String error : errorMessages) {
      resultMessage.append(error).append("\n");
    }
  }

  private void validateTemplate(Sheet sheet) throws Exception {
    Row headerRow = sheet.getRow(0);
    if (headerRow == null) {
      throw new ExceptionHandle(HttpStatus.BAD_REQUEST, "Template does not contain any headers.");
    }

    List<String> expectedHeaders =
        Arrays.asList(
            "Tên sản phẩm *",
            "Danh mục *",
            "Thương hiệu *",
            "Chất liệu *",
            "Xuất xứ *",
            "Mô tả sản phẩm",
            "Giá tiền *",
            "Số lượng *",
            "Kích thước *",
            "Màu sắc *");

    for (int i = 0; i < expectedHeaders.size(); i++) {
      Cell cell = headerRow.getCell(i);
      if (cell == null || !cell.getStringCellValue().equals(expectedHeaders.get(i))) {
        throw new ExceptionHandle(
            HttpStatus.BAD_REQUEST,
            "Vui lòng kiểm tra lại file import chưa đúng định dạng file mẫu");
      }
    }
  }

  private String formatWithIdAndName(Long id, String name) {
    return id + " - " + name;
  }

  private Product toDto(Product product) {
    if (product.getCreateBy() != null) {
      product.setCreateByUser(getInfoUserValue(product.getCreateBy()));
    }
    if (product.getUpdateBy() != null) {
      product.setUpdateByUser(getInfoUserValue(product.getUpdateBy()));
    }
    List<ProductDetail> productDetails = product.getProductDetails();
    if (productDetails != null && productDetails.size() > 0) {
      productDetails = product.getProductDetails().stream().map(this::toProductDetailDto).toList();
      product.setProductDetails(productDetails);
      ProductDetail firstProudctDetail = productDetails.get(0);
      Double min =
          firstProudctDetail.getOriginPrice() == null
              ? firstProudctDetail.getPrice()
              : firstProudctDetail.getOriginPrice();
      product.setPromotion(firstProudctDetail.getPromotion());
      product.setMinPrice(min.longValue());
      for (ProductDetail pd : productDetails) {
        Double price = pd.getOriginPrice() == null ? pd.getPrice() : pd.getOriginPrice();
        if (min > price) {
          min = price;
          product.setMinPrice(min.longValue());
          product.setPromotion(pd.getPromotion());
        }
      }
    }
    return product;
  }

  private ProductDetail toProductDetailDto(ProductDetail productDetail) {
    if (productDetail.getCreateBy() != null) {
      productDetail.setCreateByUser(getInfoUserValue(productDetail.getCreateBy()));
    }
    if (productDetail.getUpdateBy() != null) {
      productDetail.setUpdateByUser(getInfoUserValue(productDetail.getUpdateBy()));
    }
    List<Promotion> promotions = productDetail.getPromotionList();
    Optional<Promotion> optionalPromotion =
        promotions.stream()
            .filter(p -> p.getStatusPromotionEnum() == StatusPromotionEnum.ACTIVE)
            .findFirst();
    if (optionalPromotion.isPresent()) {
      Promotion promotion = optionalPromotion.get();
      productDetail.setPromotion(promotion);
    }
    return productDetail;
  }

  private CellStyle createCellStyle(Workbook workbook, IndexedColors color) {
    CellStyle style = workbook.createCellStyle();
    Font font = workbook.createFont();
    font.setColor(color.getIndex());
    style.setFont(font);
    return style;
  }
}
