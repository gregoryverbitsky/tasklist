package de.demo.tasklist.web.mappers;

import de.demo.tasklist.domain.task.TaskImage;
import de.demo.tasklist.web.dto.task.TaskImageDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TaskImageMapper extends Mappable<TaskImage, TaskImageDto> {
}
