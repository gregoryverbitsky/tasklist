package de.demo.tasklist.web.dto.task;

import jakarta.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Task Image DTO")
public class TaskImageDto {

    @NotNull(message = "Image must be not null.")
    private MultipartFile file;
}
