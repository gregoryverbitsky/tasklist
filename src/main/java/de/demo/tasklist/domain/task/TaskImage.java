package de.demo.tasklist.domain.task;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskImage {

    private MultipartFile file;
}
