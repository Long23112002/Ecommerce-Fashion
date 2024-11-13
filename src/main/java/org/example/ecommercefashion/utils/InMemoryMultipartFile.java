package org.example.ecommercefashion.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.web.multipart.MultipartFile;

public class InMemoryMultipartFile implements MultipartFile {
  private final String name;
  private final String originalFilename;
  private final String contentType;
  private final byte[] content;

  public InMemoryMultipartFile(
      String name, String originalFilename, String contentType, byte[] content) {
    this.name = name;
    this.originalFilename = originalFilename;
    this.contentType = contentType;
    this.content = content;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getOriginalFilename() {
    return originalFilename;
  }

  @Override
  public String getContentType() {
    return contentType;
  }

  @Override
  public boolean isEmpty() {
    return content.length == 0;
  }

  @Override
  public long getSize() {
    return content.length;
  }

  @Override
  public byte[] getBytes() {
    return content;
  }

  @Override
  public InputStream getInputStream() throws IOException {
    return new ByteArrayInputStream(content);
  }

  @Override
  public void transferTo(java.io.File dest) throws IOException, IllegalStateException {}
}