package org.example.ecommercefashion.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ExcelService {
    List<String> getListEmailFromExcel(MultipartFile file) throws IOException;
}
