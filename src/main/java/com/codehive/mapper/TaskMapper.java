package com.codehive.mapper;

import com.codehive.dto.TaskDto;
import com.codehive.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    @Mappings({
            @Mapping(target = "assignedTo", source = "assignedTo.username")
    })
    TaskDto toDto(Task task);
}
