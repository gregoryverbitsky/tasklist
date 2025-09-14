package de.demo.tasklist.web.mappers;

import de.demo.tasklist.domain.user.User;
import de.demo.tasklist.web.dto.user.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper extends Mappable<User, UserDto> {
}
