package de.demo.tasklist.web.mappers;

import org.mapstruct.Mapper;

import de.demo.tasklist.domain.task.Task;
import de.demo.tasklist.web.dto.task.TaskDto;

@Mapper(componentModel = "spring")
public interface TaskMapper extends Mappable<Task, TaskDto> {
}
