package de.demo.tasklist.service.impl;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import de.demo.tasklist.domain.exception.ResourceNotFoundException;
import de.demo.tasklist.domain.task.Status;
import de.demo.tasklist.domain.task.Task;
import de.demo.tasklist.domain.task.TaskImage;
import de.demo.tasklist.repository.TaskRepository;
import de.demo.tasklist.service.ImageService;
import de.demo.tasklist.service.TaskService;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final ImageService imageService;
    private final ApplicationContext applicationContext;

    @Override
    @Cacheable(value = "TaskService::getById", key = "#id")
    public Task getById(final Long id) {
        return this.taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task not found."));
    }

    @Override
    public List<Task> getAllByUserId(final Long id) {
        return taskRepository.findAllByUserId(id);
    }

    @Override
    public List<Task> getAllSoonTasks(final Duration duration) {
        LocalDateTime now = LocalDateTime.now();
        return this.taskRepository.findAllSoonTasks(Timestamp.valueOf(now), Timestamp.valueOf(now.plus(duration)));
    }

    @Override
    @Transactional
    @CachePut(value = "TaskService::getById", key = "#task.id")
    public Task update(final Task task) {
        Task existing = this.applicationContext.getBean(TaskService.class).getById(task.getId());
        if (task.getStatus() == null) {
            existing.setStatus(Status.TODO);
        } else {
            existing.setStatus(task.getStatus());
        }
        existing.setTitle(task.getTitle());
        existing.setDescription(task.getDescription());
        existing.setExpirationDate(task.getExpirationDate());
        this.taskRepository.save(task);
        return task;
    }

    @Override
    @Transactional
    @Cacheable(value = "TaskService::getById", condition = "#task.id!=null", key = "#task.id")
    public Task create(final Task task, final Long userId) {
        if (task.getStatus() == null) {
            task.setStatus(Status.TODO);
        }
        this.taskRepository.save(task);
        this.taskRepository.assignTask(userId, task.getId());
        return task;
    }

    @Override
    @Transactional
    @CacheEvict(value = "TaskService::getById", key = "#id")
    public void delete(final Long id) {
        this.taskRepository.deleteById(id);
    }

    @Override
    @Transactional
    @CacheEvict(value = "TaskService::getById", key = "#id")
    public void uploadImage(final Long id, final TaskImage image) {
        String fileName = this.imageService.upload(image);
        this.taskRepository.addImage(id, fileName);
    }
}
