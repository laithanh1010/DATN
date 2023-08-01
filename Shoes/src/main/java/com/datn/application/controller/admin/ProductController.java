package com.datn.application.controller.admin;

import com.datn.application.config.Contant;
import com.datn.application.entity.*;
import com.datn.application.model.request.CreateProductRequest;
import com.datn.application.model.request.CreateSizeCountRequest;
import com.datn.application.model.request.UpdateFeedBackRequest;
import com.datn.application.security.CustomUserDetails;
import com.datn.application.service.BrandService;
import com.datn.application.service.CategoryService;
import com.datn.application.service.ImageService;
import com.datn.application.service.ProductService;
import com.datn.application.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.*;
import java.nio.file.Files;
import java.util.List;

@Slf4j
@Controller
public class ProductController {

    private String xlsx = ".xlsx";
    private static final int BUFFER_SIZE = 4096;
    private static final String TEMP_EXPORT_DATA_DIRECTORY = "\\resources\\reports";
    private static final String EXPORT_DATA_REPORT_FILE_NAME = "San_pham";

    @Autowired
    private ServletContext context;

    @Autowired
    private ProductService productService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ImageService imageService;

    @GetMapping("/admin/products")
    public String homePages(Model model,
                            @RequestParam(defaultValue = "", required = false) String id,
                            @RequestParam(defaultValue = "", required = false) String name,
                            @RequestParam(defaultValue = "", required = false) String category,
                            @RequestParam(defaultValue = "", required = false) String brand,
                            @RequestParam(defaultValue = "1", required = false) Integer page) {

        //Lấy danh sách nhãn hiệu
        List<Brand> brands = brandService.getListBrand();
        model.addAttribute("brands", brands);
        //Lấy danh sách danh mục
        List<Category> categories = categoryService.getListCategories();
        model.addAttribute("categories", categories);
        //Lấy danh sách sản phẩm
        Page<Product> products = productService.adminGetListProduct(id, name, category, brand, page);
        model.addAttribute("products", products.getContent());
        model.addAttribute("totalPages", products.getTotalPages());
        model.addAttribute("currentPage", products.getPageable().getPageNumber() + 1);

        return "admin/product/list";
    }

    @GetMapping("/admin/products/create")
    public String getProductCreatePage(Model model) {
        //Lấy danh sách anh của user
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        List<String> images = imageService.getListImageOfUser(user.getId());
        model.addAttribute("images", images);

        //Lấy danh sách nhãn hiệu
        List<Brand> brands = brandService.getListBrand();
        model.addAttribute("brands", brands);
        //Lấy danh sách danh mục
        List<Category> categories = categoryService.getListCategories();
        model.addAttribute("categories", categories);

        return "admin/product/create";
    }

    @GetMapping("/admin/products/{slug}/{id}")
    public String getProductUpdatePage(Model model, @PathVariable String id) {

        // Lấy thông tin sản phẩm theo id
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);

        // Lấy danh sách ảnh của user
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        List<String> images = imageService.getListImageOfUser(user.getId());
        model.addAttribute("images", images);

        // Lấy danh sách danh mục
        List<Category> categories = categoryService.getListCategories();
        model.addAttribute("categories", categories);

        // Lấy danh sách nhãn hiệu
        List<Brand> brands = brandService.getListBrand();
        model.addAttribute("brands", brands);

        //Lấy danh sách size
        model.addAttribute("sizeVN", Contant.SIZE_VN);

        //Lấy size của sản phẩm
        List<ProductSize> productSizes = productService.getListSizeOfProduct(id);
        model.addAttribute("productSizes", productSizes);

        return "admin/product/edit";
    }

    @GetMapping("/api/admin/products")
    public ResponseEntity<Object> getListProducts(@RequestParam(defaultValue = "", required = false) String id,
                                                  @RequestParam(defaultValue = "", required = false) String name,
                                                  @RequestParam(defaultValue = "", required = false) String category,
                                                  @RequestParam(defaultValue = "", required = false) String brand,
                                                  @RequestParam(defaultValue = "1", required = false) Integer page) {
        Page<Product> products = productService.adminGetListProduct(id, name, category, brand, page);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/api/admin/products/{id}")
    public ResponseEntity<Object> getProductDetail(@PathVariable String id) {
        Product rs = productService.getProductById(id);
        return ResponseEntity.ok(rs);
    }

    @PostMapping("/api/admin/products")
    public ResponseEntity<Object> createProduct(@Valid @RequestBody CreateProductRequest createProductRequest) {
        Product product = productService.createProduct(createProductRequest);
        return ResponseEntity.ok(product);
    }

    @PutMapping("/api/admin/products/{id}")
    public ResponseEntity<Object> updateProduct(@Valid @RequestBody CreateProductRequest createProductRequest, @PathVariable String id) {
        productService.updateProduct(createProductRequest, id);
        return ResponseEntity.ok("Sửa sản phẩm thành công!");
    }

    @DeleteMapping("/api/admin/products")
    public ResponseEntity<Object> deleteProduct(@RequestBody String[] ids) {
        productService.deleteProduct(ids);
        return ResponseEntity.ok("Xóa sản phẩm thành công!");
    }

    @DeleteMapping("/api/admin/products/{id}")
    public ResponseEntity<Object> deleteProductById(@PathVariable String id) {
        productService.deleteProductById(id);
        return ResponseEntity.ok("Xóa sản phẩm thành công!");
    }

    @PutMapping("/api/admin/products/sizes")
    public ResponseEntity<?> updateSizeCount(@Valid @RequestBody CreateSizeCountRequest createSizeCountRequest) {
        productService.createSizeCount(createSizeCountRequest);

        return ResponseEntity.ok("Cập nhật thành công!");
    }

    @PutMapping("/api/admin/products/{id}/update-feedback-image")
    public ResponseEntity<?> updatefeedBackImages(@PathVariable String id, @Valid @RequestBody UpdateFeedBackRequest req) {
        productService.updatefeedBackImages(id, req);

        return ResponseEntity.ok("Cập nhật thành công");
    }

    @GetMapping("/api/products/export/excel")
    public void exportProductDataToExcelFile(HttpServletResponse response) {
        //xuất dữ liệu của các sản phẩm (Product) vào một tập tin Excel (.xlsx) và gửi tập tin xuất ra phản hồi (response) để người dùng có thể tải xuống.
        List<Product> result = productService.getAllProduct();
        String fullPath = this.generateProductExcel(result, context, EXPORT_DATA_REPORT_FILE_NAME);
        if (fullPath != null) {
            this.fileDownload(fullPath, response, EXPORT_DATA_REPORT_FILE_NAME, "xlsx");
        }
    }

    private String generateProductExcel(List<Product> products, ServletContext context, String fileName) {
        String filePath = context.getRealPath(TEMP_EXPORT_DATA_DIRECTORY);
        File file = new File(filePath);
        if (!file.exists()) {
            new File(filePath).mkdirs();
        }
        try (FileOutputStream fos = new FileOutputStream(file + "\\" + fileName + xlsx);
             XSSFWorkbook workbook = new XSSFWorkbook();) {

            // Tạo một trang Excel mới có tên "Product"
            XSSFSheet worksheet = workbook.createSheet("Product");
            worksheet.setDefaultColumnWidth(20);

            // Tạo hàng đầu tiên (header) chứa tên các cột
            XSSFRow headerRow = worksheet.createRow(0);

            // Tạo kiểu cột cho header
            XSSFCellStyle headerCellStyle = workbook.createCellStyle();
            XSSFFont font = workbook.createFont();
            font.setFontName(XSSFFont.DEFAULT_FONT_NAME);
            font.setColor(new XSSFColor(java.awt.Color.WHITE));
            headerCellStyle.setFont(font);
            headerCellStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(135, 206, 250)));
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Tạo các ô cho các cột của header
            XSSFCell productId = headerRow.createCell(0);
            productId.setCellValue("Mã sản phẩm");
            productId.setCellStyle(headerCellStyle);

            XSSFCell productName = headerRow.createCell(1);
            productName.setCellValue("Tên sản phẩm");
            productName.setCellStyle(headerCellStyle);

            XSSFCell productBrand = headerRow.createCell(2);
            productBrand.setCellValue("Thương hiệu");
            productBrand.setCellStyle(headerCellStyle);

            XSSFCell price = headerRow.createCell(3);
            price.setCellValue("Giá nhập");
            price.setCellStyle(headerCellStyle);

            XSSFCell priceSell = headerRow.createCell(4);
            priceSell.setCellValue("Giá bán");
            priceSell.setCellStyle(headerCellStyle);

            XSSFCell createdAt = headerRow.createCell(5);
            createdAt.setCellValue("Ngày tạo");
            createdAt.setCellStyle(headerCellStyle);

            XSSFCell modifiedAt = headerRow.createCell(6);
            modifiedAt.setCellValue("Ngày sửa");
            modifiedAt.setCellStyle(headerCellStyle);

            XSSFCell totalSold = headerRow.createCell(7);
            totalSold.setCellValue("Đã bán");
            totalSold.setCellStyle(headerCellStyle);

            // Tạo các hàng dữ liệu (body) chứa thông tin của các sản phẩm
            if (!products.isEmpty()) {
                for (int i = 0; i < products.size(); i++) {
                    Product product = products.get(i);
                    XSSFRow bodyRow = worksheet.createRow(i + 1);
                    XSSFCellStyle bodyCellStyle = workbook.createCellStyle();
                    bodyCellStyle.setFillForegroundColor(new XSSFColor(java.awt.Color.WHITE));

                    // Tạo các ô cho các cột của body
                    XSSFCell productIDValue = bodyRow.createCell(0);
                    productIDValue.setCellValue(product.getId());
                    productIDValue.setCellStyle(bodyCellStyle);

                    XSSFCell productNameValue = bodyRow.createCell(1);
                    productNameValue.setCellValue(product.getName());
                    productNameValue.setCellStyle(bodyCellStyle);

                    XSSFCell productBrandValue = bodyRow.createCell(2);
                    productBrandValue.setCellValue(product.getBrand().getName());
                    productBrandValue.setCellStyle(bodyCellStyle);

                    XSSFCell priceValue = bodyRow.createCell(3);
                    priceValue.setCellValue(product.getPrice());
                    priceValue.setCellStyle(bodyCellStyle);

                    XSSFCell priceSellValue = bodyRow.createCell(4);
                    priceSellValue.setCellValue(product.getSalePrice());
                    priceSellValue.setCellStyle(bodyCellStyle);

                    // Định dạng ngày tháng cho các ô Ngày tạo và Ngày sửa
                    CreationHelper creationHelper = workbook.getCreationHelper();
                    CellStyle cellStyle = workbook.createCellStyle();
                    cellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("dd/MM/yyyy HH:mm:ss"));

                    XSSFCell createdAtValue = bodyRow.createCell(5);
                    createdAtValue.setCellValue(product.getCreatedAt());
                    createdAtValue.setCellStyle(cellStyle);

                    XSSFCell updatedAtValue = bodyRow.createCell(6);
                    updatedAtValue.setCellValue(product.getModifiedAt());
                    updatedAtValue.setCellStyle(cellStyle);

                    XSSFCell totalSoldValue = bodyRow.createCell(7);
                    totalSoldValue.setCellValue(product.getTotalSold());
                    totalSoldValue.setCellStyle(bodyCellStyle);
                }
            }
            // Ghi nội dung của workbook vào FileOutputStream để tạo tập tin Excel
            workbook.write(fos);
            return file + "\\" + fileName + xlsx;
        } catch (Exception e) {
            return null;
        }
    }

    private void fileDownload(String fullPath, HttpServletResponse response, String fileName, String type) {
        //tải xuống tập tin Excel đã tạo từ phương thức generateProductExcel
        File file = new File(fullPath);
        if (file.exists()) {
            OutputStream os = null;
            try(FileInputStream fis = new FileInputStream(file);) {
                // Xác định kiểu MIME của tập tin để trả về cho trình duyệt
                String mimeType = context.getMimeType(fullPath);
                response.setContentType(mimeType);
                // Xác định tên file khi trình duyệt tải xuống tập tin
                response.setHeader("content-disposition", "attachment; filename=" + fileName + "." + type);
                os = response.getOutputStream();
                byte[] buffer = new byte[BUFFER_SIZE];
                int bytesRead = -1;
                // Đọc dữ liệu từ tập tin và ghi vào OutputStream để trả về cho trình duyệt
                while ((bytesRead = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                // Xóa tập tin sau khi đã truyền dữ liệu hoàn tất
                Files.delete(file.toPath());
            } catch (Exception e) {
                log.error("Can't download file, detail: {}", e.getMessage());
            } finally {
                // Đóng OutputStream để giải phóng tài nguyên
                if(os != null) {
                    try {
                        os.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
