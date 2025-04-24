package com.mszq.alert.transform.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "alert")
public class CommonConfig {
    private String url;
    private String cluster;
    private String severity;
}
