package org.example.ecommercefashion.dtos.filter;

import lombok.Data;

@Data
public class BrandParam {
  private String name;

  @Override
  public int hashCode() {
    return this.toString().hashCode();
  }
}
