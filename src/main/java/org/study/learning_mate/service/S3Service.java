package org.study.learning_mate.service;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
public class S3Service {

    private final AmazonS3 amazonS3;
    private final AmazonS3Client amazonS3Client;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    public S3Service(AmazonS3 amazonS3, AmazonS3Client amazonS3Client) {
        this.amazonS3 = amazonS3;
        this.amazonS3Client = amazonS3Client;
    }

    public String uploadFile(MultipartFile file) throws IOException {
        try {
            log.info("fileName");
            String fileName = file.getOriginalFilename();
            String fileUrl = "https://" + bucket + "+" + fileName;
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());
            amazonS3Client.putObject(bucket, fileName, file.getInputStream(), metadata);

            String objectUrl = amazonS3Client.getUrl(bucket, fileName).toString();

            return objectUrl;
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Error with upload to S3");
        }
    }
}
