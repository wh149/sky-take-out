package com.sky.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sky.properties.AwsS3Properties;
import com.sky.utils.AwsS3Util;

import lombok.extern.slf4j.Slf4j;

/**
 * 配置类，用于创建S3Util对象
 */
@Configuration
@Slf4j
public class S3Configuration {
    
    @Bean
    @ConditionalOnMissingBean
    public AwsS3Util awsS3Util(AwsS3Properties awsS3Properties) {
        log.info("开始创建aws上传文件工具类对象:{}", awsS3Properties);
        return new AwsS3Util(awsS3Properties.getAccessKey(), awsS3Properties.getSecretKey(), awsS3Properties.getRegion(), awsS3Properties.getBucketName());
    }
}
