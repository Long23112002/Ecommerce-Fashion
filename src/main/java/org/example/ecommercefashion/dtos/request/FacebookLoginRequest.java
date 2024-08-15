package org.example.ecommercefashion.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FacebookLoginRequest {

    private String client_id;

    private String redirect_uri;

    private String client_secret;

    private String code;
}
