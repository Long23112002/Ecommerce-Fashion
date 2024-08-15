package org.example.ecommercefashion.dtos.response;

import lombok.Data;

@Data
public class FacebookUserInfo {
  private String id;
  private String name;
  private String email;
  private PictureData picture;

  @Data
  public static class PictureData {
    private PictureUrl data;

    @Data
    public static class PictureUrl {
      private int height;
      private boolean is_silhouette;
      private String url;
      private int width;
    }
  }

  public String getPictureUrl() {
    if (picture != null && picture.getData() != null) {
      return picture.getData().getUrl();
    }
    return null;
  }
}
