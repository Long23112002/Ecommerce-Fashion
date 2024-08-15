package org.example.ecommercefashion;

import com.longnh.annotions.EnableCommon;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableCommon
@EnableFeignClients
public class EcommerceFashionApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcommerceFashionApplication.class, args);
    }

}
