package org.example.ecommercefashion.utils;

import java.io.*;
import java.util.*;
import org.apache.poi.ss.usermodel.*;
import org.springframework.util.StringUtils;

public class ExcelCommon {

  public static <T> T convertCell(String key, Class<T> targetType, Cell cell) {
    if (cell != null && !cell.toString().isEmpty()) {
      if (targetType == String.class) {
        return targetType.cast(getStringValue(cell));
      } else if (targetType == Double.class || targetType == double.class) {
        return targetType.cast(getDoubleValue(cell, key));
      } else if (targetType == Long.class || targetType == long.class) {
        return targetType.cast(getLongValue(cell, key));
      } else if (targetType == Boolean.class || targetType == boolean.class) {
        return targetType.cast(getBooleanValue(cell, key));
      } else if (targetType == Integer.class || targetType == int.class) {
        return targetType.cast(getIntegerValue(cell));
      }
    }
    return null;
  }

  private static Integer getIntegerValue(Cell cell) {
    if (cell.getCellType() == CellType.STRING) {
      try {
        // Try parsing the string value to a Double and then convert it to an Integer
        Double doubleValue = Double.valueOf(cell.getStringCellValue());
        return doubleValue.intValue(); // Convert the double to int (removes decimal part)
      } catch (NumberFormatException e) {
        throw new RuntimeException(
            String.format(
                "Dữ liệu dòng %d cột %d không đúng định dạng.",
                cell.getRowIndex() + 1, cell.getColumnIndex() + 1));
      }
    }

    return (int) cell.getNumericCellValue();
  }

  private static String getStringValue(Cell cell) {
    if (cell.getCellType() == CellType.STRING) {
      return cell.getStringCellValue();
    } else if (cell.getCellType() == CellType.NUMERIC) {
      return String.valueOf(cell.getNumericCellValue());
    }
    throw new RuntimeException(
        String.format(
            "Dữ liệu dòng %d cột %d không đúng định dạng.",
            cell.getRowIndex() + 1, cell.getColumnIndex() + 1));
  }

  private static Double getDoubleValue(Cell cell, String key) {
    if (cell.getCellType() == CellType.STRING) {
      try {
        return Double.parseDouble(cell.getStringCellValue());
      } catch (NumberFormatException e) {
        throw new RuntimeException(
            String.format(
                "Dữ liệu dòng %o cột %o không dúng định dạng.",
                cell.getRowIndex() + 1, cell.getColumnIndex() + 1));
      }
    } else if (cell.getCellType() == CellType.NUMERIC) {
      return cell.getNumericCellValue();
    }
    throw new RuntimeException(
        String.format(
            "Dữ liệu dòng %o cột %o không dúng định dạng.",
            cell.getRowIndex() + 1, cell.getColumnIndex() + 1));
  }

  private static Long getLongValue(Cell cell, String key) {
    if (cell.getCellType() == CellType.STRING) {
      try {
        return Long.parseLong(cell.getStringCellValue());
      } catch (NumberFormatException e) {
        throw new RuntimeException(
            String.format(
                "Dữ liệu dòng %o cột %o không dúng định dạng.",
                cell.getRowIndex() + 1, cell.getColumnIndex() + 1));
      }
    } else if (cell.getCellType() == CellType.NUMERIC) {
      return (long) cell.getNumericCellValue();
    }
    throw new RuntimeException(
        String.format(
            "Dữ liệu dòng %o cột %o không dúng định dạng.",
            cell.getRowIndex() + 1, cell.getColumnIndex() + 1));
  }

  private static Boolean getBooleanValue(Cell cell, String key) {
    if (cell.getCellType() == CellType.BOOLEAN) {
      return cell.getBooleanCellValue();
    }
    throw new RuntimeException(
        String.format(
            "Dữ liệu dòng %o cột %o không dúng định dạng.",
            cell.getRowIndex() + 1, cell.getColumnIndex() + 1));
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
          && cell.getCellType() != CellType.BLANK
          && StringUtils.hasLength(cell.toString())) {
        return false;
      }
    }
    return true;
  }
}
