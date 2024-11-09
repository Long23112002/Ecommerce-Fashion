package org.example.ecommercefashion.utils;

import com.longnh.exceptions.ExceptionHandle;
import java.io.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

public class ExcelCommon {

  public static Workbook read(String filePath) {
    if (!filePath.endsWith(".xlsx") && !filePath.endsWith(".xls")) {
      throw new ExceptionHandle(HttpStatus.BAD_REQUEST, "Chỉ chấp nhận file excel.");
    }
    File file = new File(filePath);
    if (!file.exists()) {
      throw new ExceptionHandle(HttpStatus.BAD_REQUEST, "File không tồn tại");
    }

    try {
      FileInputStream excelFile = new FileInputStream(filePath);

      return new XSSFWorkbook(excelFile);
    } catch (Exception e) {
      throw new ExceptionHandle(HttpStatus.BAD_REQUEST, "Không thể đọc file excel");
    }
  }

  public static Object getCellValue(Cell cell) {
    try {
      if (cell == null) {
        return null;
      }
      // Kiểm tra loại dữ liệu của ô
      switch (cell.getCellType()) {
        case _NONE:
        case BLANK:
          return null;
        case STRING:
          return Objects.equals(cell.getStringCellValue(), "") ? null : cell.getStringCellValue();
        case NUMERIC:
          if (DateUtil.isCellDateFormatted(cell)) {
            return cell.getDateCellValue();
          }
          return cell.getNumericCellValue();
        case BOOLEAN:
          return cell.getBooleanCellValue();
        case FORMULA:
          return getFormulaCellValue(cell);
        default:
          throw new ExceptionHandle(
              HttpStatus.BAD_REQUEST,
              String.format(
                  "Dữ liệu dòng %o cột %o không đúng định dạng.",
                  cell.getRowIndex() + 1, cell.getColumnIndex() + 1));
      }
    } catch (Exception e) {
      throw new ExceptionHandle(
          HttpStatus.BAD_REQUEST,
          String.format(
              "Dữ liệu dòng %o cột %o không đúng định dạng.",
              cell.getRowIndex() + 1, cell.getColumnIndex() + 1));
    }
  }

  private static Object getFormulaCellValue(Cell cell) {
    try {
      switch (cell.getCachedFormulaResultType()) {
        case STRING:
          return Objects.equals(cell.getStringCellValue(), "") ? null : cell.getStringCellValue();
        case NUMERIC:
          if (DateUtil.isCellDateFormatted(cell)) {
            return cell.getDateCellValue();
          }
          return cell.getNumericCellValue();
        case BOOLEAN:
          return cell.getBooleanCellValue();
        case BLANK:
          return null;
        default:
          throw new RuntimeException(
              String.format(
                  "Dữ liệu công thức dòng %o cột %o không đúng định dạng.",
                  cell.getRowIndex() + 1, cell.getColumnIndex() + 1));
      }
    } catch (Exception e) {
      throw new ExceptionHandle(
          HttpStatus.BAD_REQUEST,
          String.format(
              "Dữ liệu dòng %o cột %o không đúng định dạng.",
              cell.getRowIndex() + 1, cell.getColumnIndex() + 1));
    }
  }

  public static <T> T convertCell(Class<T> targetType, Cell cell) {
    try {
      if (cell != null && !cell.toString().isEmpty()) {
        if (targetType == String.class) {
          return targetType.cast(getStringValue(cell));
        } else if (targetType.isEnum()) {
          return (T) Enum.valueOf((Class<Enum>) targetType, cell.getStringCellValue());
        } else if (targetType == Double.class || targetType == double.class) {
          return targetType.cast(getDoubleValue(cell));
        } else if (targetType == Long.class || targetType == long.class) {
          return targetType.cast(getLongValue(cell));
        } else if (targetType == Boolean.class || targetType == boolean.class) {
          return targetType.cast(getBooleanValue(cell));
        } else if (targetType == Timestamp.class) {
          return targetType.cast(getTimestampValue(cell));
        }
      }
    } catch (Exception e) {
      ExcelCommon.addCommentToCell(cell, "Không đúng định dạng dữ liệu");
    }
    return null;
  }

  public static String getStringValue(Cell cell) {
    if (cell.getCellType() == CellType.STRING) {
      return cell.getStringCellValue();
    } else if ((Arrays.asList(CellType.NUMERIC, CellType.FORMULA).contains(cell.getCellType()))) {
      if (DateUtil.isCellDateFormatted(cell)) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(cell.getDateCellValue());
      } else {
        return BigDecimal.valueOf(cell.getNumericCellValue()).toPlainString();
      }
    } else if (cell.getCellType() == CellType.BLANK || cell.getCellType() == CellType._NONE) {
      return "";
    }
    throw new RuntimeException(
        String.format("Dữ liệu cột %o không dúng định dạng.", cell.getColumnIndex() + 1));
  }

  private static Double getDoubleValue(Cell cell) {
    if (cell.getCellType() == CellType.STRING) {
      try {
        return Double.parseDouble(cell.getStringCellValue());
      } catch (NumberFormatException e) {
        throw new RuntimeException(
            String.format(
                "Dữ liệu dòng %o cột %o không dúng định dạng.",
                cell.getRowIndex() + 1, cell.getColumnIndex() + 1));
      }
    } else if ((Arrays.asList(CellType.NUMERIC, CellType.FORMULA).contains(cell.getCellType()))) {
      return cell.getNumericCellValue();
    }
    throw new RuntimeException(
        String.format(
            "Dữ liệu dòng %o cột %o không dúng định dạng.",
            cell.getRowIndex() + 1, cell.getColumnIndex() + 1));
  }

  private static Long getLongValue(Cell cell) {
    if (cell.getCellType() == CellType.STRING) {
      try {
        return Long.parseLong(cell.getStringCellValue());
      } catch (NumberFormatException e) {
        throw new RuntimeException(
            String.format(
                "Dữ liệu dòng %o cột %o không dúng định dạng.",
                cell.getRowIndex() + 1, cell.getColumnIndex() + 1));
      }
    } else if ((Arrays.asList(CellType.NUMERIC, CellType.FORMULA).contains(cell.getCellType()))) {
      return (long) cell.getNumericCellValue();
    }
    throw new RuntimeException(
        String.format(
            "Dữ liệu dòng %o cột %o không dúng định dạng.",
            cell.getRowIndex() + 1, cell.getColumnIndex() + 1));
  }

  private static Boolean getBooleanValue(Cell cell) {
    if (cell.getCellType() == CellType.BOOLEAN) {
      return cell.getBooleanCellValue();
    }
    throw new RuntimeException(
        String.format(
            "Dữ liệu dòng %o cột %o không dúng định dạng.",
            cell.getRowIndex() + 1, cell.getColumnIndex() + 1));
  }

  private static Timestamp getTimestampValue(Cell cell) {
    if (cell.getCellType() == CellType.NUMERIC) {

      Date date = cell.getDateCellValue();

      return Timestamp.from(date.toInstant());
    } else if (cell.getCellType() == CellType.STRING) {

      String data = getStringValue(cell);
      if (StringUtils.hasLength(data)) {

        try {
          return Timestamp.valueOf(
              LocalDate.parse(data, (DateTimeFormatter.ofPattern("dd/MM/yyyy"))).atStartOfDay());
        } catch (DateTimeParseException ignore) {

        }
      }
    }
    throw new RuntimeException(
        String.format("Dữ liệu cột %o không dúng định dạng.", cell.getColumnIndex() + 1));
  }

  public static boolean isEmptyRow(Row row, int firstNum, int lastNum) {
    if (row == null) {
      return true;
    }
    if (row.getLastCellNum() <= 0) {
      return true;
    }
    for (int cellNum = firstNum; cellNum <= lastNum; cellNum++) {
      Cell cell = row.getCell(cellNum);
      if (cell != null
          && !Arrays.asList(CellType.BLANK, CellType._NONE, CellType.FORMULA)
              .contains(cell.getCellType())
          && StringUtils.hasLength(cell.toString())) {
        return false;
      }
    }
    return true;
  }

  public static boolean isEmptyRow(Row row) {
    if (row == null) {
      return true;
    }
    if (row.getLastCellNum() <= 0) {
      return true;
    }
    for (int cellNum = row.getFirstCellNum(); cellNum < row.getLastCellNum(); cellNum++) {
      Cell cell = row.getCell(cellNum);
      if (cell != null
          && !Arrays.asList(CellType.BLANK, CellType._NONE, CellType.FORMULA)
              .contains(cell.getCellType())
          && StringUtils.hasLength(cell.toString())) {
        return false;
      }
    }
    return true;
  }

  public static void validRow(
      Row row, List<Integer> notnullCell, List<Integer> stringCell, List<Integer> numberCell)
      throws ExceptionHandle {
    if (notnullCell != null && !notnullCell.isEmpty()) {
      for (Integer cellIndex : notnullCell) {
        if (getCellValue(row.getCell(cellIndex)) == null) {
          throw new ExceptionHandle(
              HttpStatus.BAD_REQUEST,
              String.format(
                  "Dữ liệu dòng %o cột %s không được trống.",
                  row.getRowNum() + 1, CellReference.convertNumToColString(cellIndex)));
        }
      }
    }
    if (stringCell != null && !stringCell.isEmpty()) {
      for (Integer cellIndex : stringCell) {
        if (!(row.getCell(cellIndex).getCellType() != CellType.BLANK
            && row.getCell(cellIndex).getCellType() == CellType.STRING)) {
          throw new ExceptionHandle(
              HttpStatus.BAD_REQUEST,
              String.format(
                  "Dữ liệu dòng %o cột %s chỉ nhận kiểu chữ.",
                  row.getRowNum() + 1, CellReference.convertNumToColString(cellIndex)));
        }
      }
    }
    if (numberCell != null && !numberCell.isEmpty()) {
      for (Integer cellIndex : numberCell) {
        if (getCellValue(row.getCell(cellIndex)) == null) {
          continue;
        }
        if (row.getCell(cellIndex).getCellType() == CellType.STRING) {
          try {
            Double.valueOf(row.getCell(cellIndex).getStringCellValue());
          } catch (ExceptionHandle e) {
            throw new ExceptionHandle(
                HttpStatus.BAD_REQUEST,
                String.format(
                    "Dữ liệu dòng %o cột %s chỉ nhận kiểu số.",
                    row.getRowNum() + 1, CellReference.convertNumToColString(cellIndex)));
          }
        } else if (!(row.getCell(cellIndex).getCellType() != CellType.BLANK
            && row.getCell(cellIndex).getCellType() == CellType.NUMERIC)) {
          throw new ExceptionHandle(
              HttpStatus.BAD_REQUEST,
              String.format(
                  "Dữ liệu dòng %o cột %s chỉ nhận kiểu số.",
                  row.getRowNum() + 1, CellReference.convertNumToColString(cellIndex)));
        }
      }
    }
  }

  public static Row validNewRow(
      Row row,
      CellStyle style,
      List<Integer> notnullCell,
      List<Integer> stringCell,
      List<Integer> numberCell) {
    if (notnullCell != null && !notnullCell.isEmpty()) {
      for (Integer cellIndex : notnullCell) {
        if (getCellValue(row.getCell(cellIndex)) == null) {
          row.getCell(cellIndex).setCellStyle(style);
        }
      }
    }
    if (stringCell != null && !stringCell.isEmpty()) {
      for (Integer cellIndex : stringCell) {
        if (!(row.getCell(cellIndex).getCellType() != CellType.BLANK
            && row.getCell(cellIndex).getCellType() == CellType.STRING)) {
          row.getCell(cellIndex).setCellStyle(style);
        }
      }
    }
    if (numberCell != null && !numberCell.isEmpty()) {
      for (Integer cellIndex : numberCell) {
        if (getCellValue(row.getCell(cellIndex)) == null) {
          continue;
        }
        if (row.getCell(cellIndex).getCellType() == CellType.STRING) {
          try {
            Double.valueOf(row.getCell(cellIndex).getStringCellValue());
          } catch (ExceptionHandle e) {
            row.getCell(cellIndex).setCellStyle(style);
          }
        } else if (!(row.getCell(cellIndex).getCellType() != CellType.BLANK
            && row.getCell(cellIndex).getCellType() == CellType.NUMERIC)) {
          row.getCell(cellIndex).setCellStyle(style);
        }
      }
    }
    return row;
  }

  public static CellStyle styleCell(
      Row row,
      Short backgroundColor,
      Short fontColor,
      Boolean strikeout,
      String fontName,
      Short fontSize,
      Boolean bold) {
    CellStyle style = row.getSheet().getWorkbook().createCellStyle();
    if (backgroundColor != null) {
      style.setFillForegroundColor(backgroundColor);
      style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    }

    Font font = row.getSheet().getWorkbook().createFont();
    if (fontColor != null) {
      font.setColor(fontColor);
    }
    if (strikeout != null) {
      font.setStrikeout(strikeout);
    }
    if (fontName != null) {
      font.setFontName(fontName);
    }
    if (fontSize != null) {
      font.setFontHeightInPoints(fontSize);
    }
    if (bold != null) {
      font.setBold(bold);
    }
    style.setFont(font);

    for (int j = 0; j < row.getLastCellNum(); j++) {
      Cell cell = row.getCell(j);
      if (cell == null) {
        cell = row.createCell(j);
      }
      cell.setCellStyle(style);
    }
    return style;
  }

  public static void setCellStyle(Row row, Cell cell, IndexedColors color) {
    CellStyle style = row.getSheet().getWorkbook().createCellStyle();

    Font font = row.getSheet().getWorkbook().createFont();
    font.setColor(color.getIndex());
    style.setFont(font);
    style.setAlignment(HorizontalAlignment.CENTER);
    style.setVerticalAlignment(VerticalAlignment.CENTER);

    cell.setCellStyle(style);
  }

  public static String getTime(Timestamp time) {
    if (Objects.isNull(time)) {
      return null;
    }

    return time.toLocalDateTime().plusHours(7).format(DateTimeFormatter.ofPattern("dd/MM/YYYY"));
  }

  public static void addCommentToCell(Cell cell, String commentText) {
    // Nếu cell null, tạo cell mới với giá trị trống
    if (cell == null) {
      Row row = cell.getRow();
      int columnIndex = cell.getColumnIndex();
      cell = row.createCell(columnIndex);
      cell.setCellValue("");
    }
    Sheet sheet = cell.getSheet();
    Workbook workbook = sheet.getWorkbook();
    Drawing<?> drawing = sheet.createDrawingPatriarch();

    CreationHelper factory = workbook.getCreationHelper();
    ClientAnchor anchor = factory.createClientAnchor();
    anchor.setCol1(cell.getColumnIndex());
    anchor.setRow1(cell.getRowIndex());

    Comment comment = cell.getCellComment();
    if (comment != null) {
      String existingText = comment.getString().getString();
      comment.setString(factory.createRichTextString(existingText + "\n" + commentText));
    } else {
      comment = drawing.createCellComment(anchor);
      comment.setString(factory.createRichTextString(commentText));
      comment.setAuthor("Validation System");
      cell.setCellComment(comment);
    }
  }

  public static void validateExcelFile(String filePath) throws Exception {
    Workbook workbook = read(filePath);
    Sheet sheet = workbook.getSheetAt(0);
    // remove row empty
    for (int i = sheet.getLastRowNum(); i >= 0; i--) {
      Row row = sheet.getRow(i);
      if (isEmptyRow(row)) {
        sheet.removeRow(row);
      }
    }
    // save file
    try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
      workbook.write(fileOut);
    }

    if (sheet.getPhysicalNumberOfRows() == 0) {
      throw new ExceptionHandle(HttpStatus.BAD_REQUEST, "Không có bản ghi nào được nhập");
    }
  }
}
