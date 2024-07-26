package com.kboticket.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "payment")
@Getter @Setter
public class PaymentConfig {
    private String secretKey;
    private String clientKey;
    private String baseUrl;
    private String confirmEndpoint;
    private String cancelEndpoint;
}
