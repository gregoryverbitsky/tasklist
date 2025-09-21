package de.demo.tasklist.web.mappers;

import org.mapstruct.Mapper;

import de.demo.tasklist.domain.task.TaskImage;
import de.demo.tasklist.web.dto.task.TaskImageDto;

@Mapper(componentModel = "spring")
public interface TaskImageMapper extends Mappable<TaskImage, TaskImageDto> {
}
