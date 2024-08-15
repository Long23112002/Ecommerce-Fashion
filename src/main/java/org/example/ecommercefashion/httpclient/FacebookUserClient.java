package org.example.ecommercefashion.httpclient;

import org.example.ecommercefashion.dtos.response.FacebookUserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "outbound-facebook-client", url = "https://graph.facebook.com")
public interface FacebookUserClient {

  @GetMapping(value = "/me")
  FacebookUserInfo getUserInfo(
      @RequestParam("fields") String fields, @RequestParam("access_token") String accessToken);
}
