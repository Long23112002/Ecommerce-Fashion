package org.example.ecommercefashion.services.impl;

import static org.example.ecommercefashion.annotations.normalized.normalizeString;

import com.longnh.exceptions.ExceptionHandle;
import com.longnh.utils.FnCommon;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
import org.example.ecommercefashion.entities.Brand;
import org.example.ecommercefashion.entities.Category;
import org.example.ecommercefashion.entities.Material;
import org.example.ecommercefashion.entities.Origin;
import org.example.ecommercefashion.entities.Product;
import org.example.ecommercefashion.entities.User;
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
        Page<Product> productPage = productRepository.filterProduct(param, pageable);
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
            productCreate.setCode("PH"+ productRepository.getLastValue());
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

        createSampleData(sheet, categoryNames, brandNames, materialNames, originNames);

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
}