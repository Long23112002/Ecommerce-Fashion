package org.example.ecommercefashion.httpclient;

import feign.QueryMap;
import org.example.ecommercefashion.dtos.request.FacebookLoginRequest;
import org.example.ecommercefashion.dtos.response.TokenFacebookResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "outbound-identity-facebook", url = "https://graph.facebook.com/v9.0/oauth")
public interface FacebookIdentityClient {
    @PostMapping(value = "/access_token", produces = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    TokenFacebookResponse exchangeToken(@QueryMap FacebookLoginRequest request);
}
