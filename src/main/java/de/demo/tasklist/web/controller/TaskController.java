package de.demo.tasklist.web.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import de.demo.tasklist.domain.task.Task;
import de.demo.tasklist.domain.task.TaskImage;
import de.demo.tasklist.service.TaskService;
import de.demo.tasklist.web.dto.task.TaskDto;
import de.demo.tasklist.web.dto.validation.OnUpdate;
import de.demo.tasklist.web.mappers.TaskMapper;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@Tag(name = "Task Controller", description = "Task API")
public class TaskController {

    private final TaskService taskService;
    private final TaskMapper taskMapper;

    @PutMapping
    @Operation(summary = "Update task")
    @PreAuthorize("@cse.canAccessTask(#dto.id)")
    public TaskDto update(@Validated(OnUpdate.class) @RequestBody final TaskDto dto) {
        Task task = this.taskMapper.toEntity(dto);
        Task updatedTask = this.taskService.update(task);
        return this.taskMapper.toDto(updatedTask);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get TaskDto by id")
    @PreAuthorize("@cse.canAccessTask(#id)")
    public TaskDto getById(@PathVariable final Long id) {
        Task task = this.taskService.getById(id);
        return this.taskMapper.toDto(task);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete task by id")
    @PreAuthorize("@cse.canAccessTask(#id)")
    public void deleteById(@PathVariable final Long id) {
        this.taskService.delete(id);
    }

    @PostMapping(value = "/{id}/image", consumes = {"multipart/form-data"})
    @Operation(summary = "Upload image to task by id")
    @PreAuthorize("@cse.canAccessTask(#id)")
    public void uploadImage(@PathVariable final Long id, @RequestParam("image") final MultipartFile file) {
        TaskImage image = new TaskImage();
        image.setFile(file);
        this.taskService.uploadImage(id, image);
    }
}
