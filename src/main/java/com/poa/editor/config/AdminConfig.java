package com.poa.editor.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "poa")
@Getter
@Setter
public class AdminConfig {

    private List<String> adminUsers = new ArrayList<>(List.of("admin"));

    public boolean isAdmin(String userId) {
        return userId != null && adminUsers.contains(userId);
    }
}
