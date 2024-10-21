package org.example.ecommercefashion.utils;

public class Slug {
  public Slug() {}

  public static String makeSlug(String input) {
    if (input == null) {
      return "";
    } else {
      if (input.charAt(0) == ' ') {
        input = input.replace(" ", "");
      }

      String slug =
          input
              .toLowerCase()
              .replaceAll("[àáảãạăắằẳẵặâấầẩẫậ]", "a")
              .replaceAll("[đ]", "d")
              .replaceAll("[èéẻẽẹêềếểễệ]", "e")
              .replaceAll("[ìíỉĩị]", "i")
              .replaceAll("[òóỏõọôồốổỗộơờớởỡợ]", "o")
              .replaceAll("[ùúủũụưừứửữự]", "u")
              .replaceAll("[ỳýỷỹỵ]", "y");
      slug = slug.replaceAll("[^a-zA-Z0-9\\s]+", "").replaceAll("\\s+", "-");
      return slug;
    }
  }
}
