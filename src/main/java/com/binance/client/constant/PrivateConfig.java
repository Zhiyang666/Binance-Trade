package com.binance.client.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PrivateConfig {

    public static String API_KEY;
    public static String SECRET_KEY;

    @Value("${binance.api.api-key}")
    public void setApiKey(String apiKey) {
        API_KEY = apiKey;
    }

    @Value("${binance.api.secret-key}")
    public void setSecretKey(String secretKey) {
        SECRET_KEY = secretKey;
    }
}
