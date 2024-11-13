package org.example.ecommercefashion.services.impl;

import static org.example.ecommercefashion.annotations.normalized.normalizeString;

import com.longnh.exceptions.ExceptionHandle;
import com.longnh.utils.FnCommon;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import javax.persistence.criteria.Order;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.ecommercefashion.dtos.filter.ProductParam;
import org.example.ecommercefashion.dtos.request.ProductRequest;
import org.example.ecommercefashion.dtos.response.JwtResponse;
import org.example.ecommercefashion.dtos.response.MessageResponse;
import org.example.ecommercefashion.dtos.response.ResponsePage;
import org.example.ecommercefashion.dtos.response.UserResponse;
import org.example.ecommercefashion.entities.*;
import org.example.ecommercefashion.entities.value.Identifiable;
import org.example.ecommercefashion.entities.value.UserValue;
import org.example.ecommercefashion.exceptions.AttributeErrorMessage;
import org.example.ecommercefashion.exceptions.ErrorMessage;
import org.example.ecommercefashion.repositories.BrandRepository;
import org.example.ecommercefashion.repositories.CategoryRepository;
import org.example.ecommercefashion.repositories.MaterialRepository;
import org.example.ecommercefashion.repositories.OriginRepository;
import org.example.ecommercefashion.repositories.ProductRepository;
import org.example.ecommercefashion.repositories.UserRepository;
import org.example.ecommercefashion.security.JwtService;
import org.example.ecommercefashion.services.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

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

  @PersistenceContext private EntityManager entityManager;

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

  private Product findById(Long id) {
    return productRepository
            .findById(id)
            .orElseThrow(
                    () -> new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.PRODUCT_NOT_FOUND));
  }

  @Override
  public ResponsePage<Product, Product> filterProduct(ProductParam param, Pageable pageable) {
    Page<Product> productPage = buildQuery(param, pageable);
    Page<Product> responses =
            productPage.map(
                    product -> {
                      if (product.getCreateBy() != null) {
                        product.setCreateByUser(getInfoUserValue(product.getCreateBy()));
                      }
                      if (product.getUpdateBy() != null) {
                        product.setUpdateByUser(getInfoUserValue(product.getUpdateBy()));
                      }
                      return product;
                    });
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

      productCreate.setCreateByUser(getInfoUserValue(jwtResponse.getUserId()));
      productRepository.save(productCreate);

      return productCreate;
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
      if (productRepository.existsByNameIgnoreCase(normalizedProductName)) {
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

      product.setCreateByUser(getInfoUserValue(product.getCreateBy()));
      product.setUpdateByUser(getInfoUserValue(jwtResponse.getUserId()));
      productRepository.save(product);

      return product;
    } else {
      throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.USER_NOT_FOUND);
    }
  }

  @Override
  public Product getProductById(Long id) {
    Product product = findById(id);
    if (product.getCreateBy() != null) {
      product.setCreateByUser(getInfoUserValue(product.getCreateBy()));
    }
    if (product.getUpdateBy() != null) {
      product.setUpdateByUser(getInfoUserValue(product.getUpdateBy()));
    }
    return product;
  }

  @Override
  public MessageResponse updateStatus(Long id) {
    Product product = findById(id);
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

    createSampleData(sheet, categoryNames, brandNames, materialNames, originNames); //char bt o dau

    return writeWorkbookToByteArray(workbook);
  }

  private void createSampleData(
          Sheet sheet,
          List<String> categoryNames,
          List<String> brandNames,
          List<String> materialNames,
          List<String> originNames) {
    Row row = sheet.createRow(1);

    row.createCell(0).setCellValue("Sản phẩm mẫu");
    row.createCell(1).setCellValue(categoryNames.isEmpty() ? "" : categoryNames.get(0));
    row.createCell(2).setCellValue(brandNames.isEmpty() ? "" : brandNames.get(0));
    row.createCell(3).setCellValue(materialNames.isEmpty() ? "" : materialNames.get(0));
    row.createCell(4).setCellValue(originNames.isEmpty() ? "" : originNames.get(0));
    row.createCell(5).setCellValue("Mô tả sản phẩm mẫu");
    autoSizeAndCenterColumns(sheet, 50);
  }

  private void createHeaderRow(Sheet sheet) {
    Row headerRow = sheet.createRow(0);
    String[] columns = {
            "Tên sản phẩm *", "Danh mục *", "Thương hiệu *", "Chất liệu *", "Xuất xứ *", "Mô tả"
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
    pageable.getSort().forEach(order -> {
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

    List<Product> products = entityManager
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
}
