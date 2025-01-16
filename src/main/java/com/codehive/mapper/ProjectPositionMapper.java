package com.codehive.mapper;

import com.codehive.dto.PositionRequest;
import com.codehive.dto.PositionResponseDto;
import com.codehive.entity.ProjectPosition;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectPositionMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "project", ignore = true)
    ProjectPosition toEntity(PositionRequest dto);

    PositionResponseDto toDto(ProjectPosition entity);
}
