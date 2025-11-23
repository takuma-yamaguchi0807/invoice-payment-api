package com.example.invoicepaymentapi.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import jakarta.annotation.PostConstruct;

/**
 * .envファイルの設定をSystem.getProperty()で取得できるようにする設定クラス
 * spring-dotenvがSpringのEnvironmentに読み込んだ値を、System.setProperty()で設定する
 */
@Configuration
public class DotenvConfig {
    private final Environment environment;

    public DotenvConfig(Environment environment) {
        this.environment = environment;
    }

    @PostConstruct
    public void init() {
        // SpringのEnvironmentから値を取得して、System.setProperty()で設定
        // これにより、AccessTokenなどの静的フィールドからSystem.getProperty()で値を取得できる
        setPropertyIfExists("JWT_SECRET");
        setPropertyIfExists("JWT_EXPIRATION");
        setPropertyIfExists("DB_HOST");
        setPropertyIfExists("DB_PORT");
        setPropertyIfExists("DB_NAME");
        setPropertyIfExists("DB_USERNAME");
        setPropertyIfExists("DB_PASSWORD");
        setPropertyIfExists("SERVER_PORT");
        setPropertyIfExists("LOGGING_LEVEL");
    }

    private void setPropertyIfExists(String key) {
        String value = environment.getProperty(key);
        if (value != null && !value.isEmpty()) {
            System.setProperty(key, value);
        }
    }
}

