package com.capstone.kkumteul.domain.fairytale.voice.service;

import com.capstone.kkumteul.domain.user.entity.User;
import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Operations;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Component
public class S3Uploader {

    private final S3Operations s3Operations;
    private final String AWS_S3_BUCKET_NAME;

    // for dependency injection with environment variable
    public S3Uploader(
            S3Operations s3Operations,
            @Value("${AWS_S3_BUCKET_NAME}")
            String AWS_S3_BUCKET_NAME
    ) {
        this.s3Operations = s3Operations;
        this.AWS_S3_BUCKET_NAME = AWS_S3_BUCKET_NAME;
    }

    // upload S3 bucket
    public String upload(MultipartFile wavFile, User user) throws IOException {
        return putS3(
                convert(wavFile),
                createFilename(
                        wavFile.getOriginalFilename(),
                        user.getUsername()
                ),
                wavFile.getContentType()
        );
    }

    private InputStream convert(MultipartFile wavFile) throws IOException {
        return wavFile.getInputStream();
    }

    // with UUID
    private String createFilename(String originalFilename, String username) {
        String uuid = UUID.randomUUID().toString();
        String uniqueFilename = uuid + "-" + originalFilename.replaceAll("\\s", "-");
        return username + "/" + uniqueFilename;
    }

    private String putS3(InputStream inputStream,String filename, String contentType) throws IOException {
        return s3Operations.upload(
                AWS_S3_BUCKET_NAME,
                        filename,
                        inputStream,
                        ObjectMetadata.builder()
                                .contentType(contentType)
                                .build()
                ).getURL().toString();
    }
}
