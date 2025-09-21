package de.demo.tasklist.service.impl;

import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import de.demo.tasklist.domain.exception.ImageUploadException;
import de.demo.tasklist.domain.task.TaskImage;
import de.demo.tasklist.service.ImageService;
import de.demo.tasklist.service.props.MinioProperties;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    @Override
    public String upload(@NotNull final TaskImage image) {
        try {
            createBucket();
        } catch (Exception e) {
            throw new ImageUploadException("Image upload failed: " + e.getMessage());
        }
        MultipartFile file = image.getFile();
        if (file.isEmpty() || file.getOriginalFilename() == null) {
            throw new ImageUploadException("Image must have name.");
        }
        String fileName = generateFileName(file);
        InputStream inputStream;
        try {
            inputStream = file.getInputStream();
        } catch (Exception e) {
            throw new ImageUploadException("Image upload failed: " + e.getMessage());
        }
        saveImage(inputStream, fileName);
        return fileName;
    }

    @SneakyThrows
    private void createBucket() {
        boolean found = this.minioClient
                .bucketExists(BucketExistsArgs.builder().bucket(this.minioProperties.getBucket()).build());
        if (!found) {
            this.minioClient.makeBucket(MakeBucketArgs.builder().bucket(minioProperties.getBucket()).build());
        }
    }

    private String generateFileName(final MultipartFile file) {
        String extension = getExtension(file);
        return UUID.randomUUID() + "." + extension;
    }

    private String getExtension(final MultipartFile file) {
        String filename = Optional.of(file.getOriginalFilename())
                .orElseThrow(() -> new ImageUploadException("Image must have name."));
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    @SneakyThrows
    private void saveImage(final InputStream inputStream, final String fileName) {
        minioClient.putObject(PutObjectArgs.builder().stream(inputStream, inputStream.available(), -1)
                .bucket(this.minioProperties.getBucket()).object(fileName).build());
    }
}
