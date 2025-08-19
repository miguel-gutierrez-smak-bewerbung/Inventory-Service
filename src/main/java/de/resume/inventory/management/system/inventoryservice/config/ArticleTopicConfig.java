package de.resume.inventory.management.system.inventoryservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "topics.product")
public class ArticleTopicConfig {
    private String upsert;
    private String delete;
    private String productsSnapshot;
    private String fail;
    private String retryFail;
}
