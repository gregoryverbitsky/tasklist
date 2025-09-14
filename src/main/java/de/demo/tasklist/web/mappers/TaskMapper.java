package de.demo.tasklist.web.mappers;

import de.demo.tasklist.domain.task.Task;
import de.demo.tasklist.web.dto.task.TaskDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TaskMapper extends Mappable<Task, TaskDto> {
}
