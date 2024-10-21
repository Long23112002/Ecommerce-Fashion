package org.example.ecommercefashion.utils;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
  public class FcmNotiMessage {

  private String subject;
  private String content;
  private Map<String, Object> data;

  public Map<String, String> getData() {
    if (data == null) return new HashMap<>();
    Map<String, String> mapData = new HashMap<>();
    for (Map.Entry<String, Object> entry : data.entrySet())
      mapData.put(entry.getKey(), entry.getValue() == null ? "" : entry.getValue().toString());

    return mapData;
  }

  public String getContent() {
    if (getData() == null || getData().isEmpty() || content == null) return content;
    String replaceContent = content;
    for (Map.Entry<String, String> entry : getData().entrySet())
      replaceContent = replaceContent.replace("#" + entry.getKey() + "#", entry.getValue());

    return replaceContent;
  }

  public String getSubject() {
    if (getData() == null || getData().isEmpty() || subject == null) return subject;
    String replaceSubject = subject;
    for (Map.Entry<String, String> entry : getData().entrySet())
      replaceSubject = replaceSubject.replace("#" + entry.getKey() + "#", entry.getValue());

    return replaceSubject;
  }
}
