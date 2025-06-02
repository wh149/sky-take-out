package com.sky.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties(prefix = "sky.aws")
@Data
public class AwsS3Properties {
    private String accessKey;
    private String secretKey;
    private String region;
    private String bucketName;
}
