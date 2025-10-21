package com.example.demo.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@ConfigurationProperties(prefix="spring")
public class ProfileProperties {

    private Datasource datasource =new Datasource();
    private Jpa jpa = new Jpa();

// getter setter
    @Getter @Setter
    public static class Datasource{
        String url;
        String username;
        String password;
        String driverClassName;
        // getter setter
    }

    @Getter
    @Setter
    public static class Jpa {
        Map<String, String> properties = new HashMap<>();
        // getter setter
    }
}
