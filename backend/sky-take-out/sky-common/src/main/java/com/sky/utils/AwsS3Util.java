package com.sky.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.time.Duration;
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
     * 文件上传
     *
     * @param bytes      文件字节数组
     * @param objectName S3中对象的路径，如 uploads/image.jpg
     * @return S3文件的访问URL
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
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectName)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(bytes));

            // 返回公共 URL（前提是 bucket 是公开的或设置了正确的权限）
            String encodedObjectName = URLEncoder.encode(objectName, StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20");
            String fileUrl =generatePresignedUrl(encodedObjectName, 60);
            
            log.info("文件上传到: {}", fileUrl);
            return fileUrl;

        } catch (Exception e) {
            log.error("上传文件到S3失败: {}", e.getMessage(), e);
            return null;
        } finally {
            s3Client.close();
        }
    }

    public String generatePresignedUrl(String objectName, int expiresMinutes) {
        S3Presigner presigner = S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(accessKeyId, secretAccessKey)
                        )
                )
                .build();

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(objectName)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .getObjectRequest(getObjectRequest)
                .signatureDuration(Duration.ofMinutes(expiresMinutes))
                .build();

        PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);

        String url = presignedRequest.url().toString();
        log.info("预签名访问链接生成成功: {}", url);

        presigner.close();
        return url;
    }
}
