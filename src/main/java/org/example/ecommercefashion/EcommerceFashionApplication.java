package org.example.ecommercefashion;

import com.longnh.annotions.EnableCommon;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCommon
@EnableFeignClients
@EnableScheduling
public class EcommerceFashionApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcommerceFashionApplication.class, args);
    }

}
