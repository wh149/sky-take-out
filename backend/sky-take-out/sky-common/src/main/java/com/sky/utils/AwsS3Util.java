package com.sky.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Data
@AllArgsConstructor
@Slf4j
public class AwsS3Util {

    private String accessKeyId;
    private String secretAccessKey;
    private String region;
    private String bucketName;

    /**
     * 上传文件到 AWS S3 并设为公开可访问
     *
     * @param bytes      文件字节数组
     * @param objectName S3中的对象键，如 uploads/image.jpg
     * @return 可公开访问的文件 URL
     */
    public String upload(byte[] bytes, String objectName) {
        // 创建 S3 客户端
        S3Client s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(accessKeyId, secretAccessKey)
                        )
                )
                .build();

        try {
            // 构建上传请求，并设为 public-read
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectName)
                    // .acl("public-read")  // 关键：设置对象为公开可读
                    .build();

            // 执行上传
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(bytes));

            // 构建公开访问 URL
            String encodedObjectName = URLEncoder.encode(objectName, StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20");
            String fileUrl = String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, encodedObjectName);

            log.info("文件上传成功，访问地址: {}", fileUrl);
            return fileUrl;

        } catch (Exception e) {
            log.error("上传文件到 S3 失败: {}", e.getMessage(), e);
            return null;

        } finally {
            s3Client.close();
        }
    }
}
