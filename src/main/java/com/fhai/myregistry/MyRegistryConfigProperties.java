package com.fhai.myregistry;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "myregistry")
public class MyRegistryConfigProperties {
    private List<String> serverList;
}
