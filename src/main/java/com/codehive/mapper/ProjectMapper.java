package com.codehive.mapper;

import com.codehive.Enum.ProjectStage;
import com.codehive.dto.CreateProjectRequest;
import com.codehive.dto.ProjectResponseDto;
import com.codehive.entity.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;


@Mapper(componentModel = "spring", uses = {ProjectPositionMapper.class})
public interface ProjectMapper {

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "creator", ignore = true),
            @Mapping(target = "positions", ignore = true),
            @Mapping(target = "stage", expression = "java( mapStage(dto.getStage()) )"),
            @Mapping(target = "category", ignore = true)
    })
    Project toEntity(CreateProjectRequest dto);

    @Mappings({
            @Mapping(target = "stage", expression = "java( entity.getStage().name() )"),
            @Mapping(target = "positions", source = "positions"),
            @Mapping(target = "category", source = "category.name")
    })
    ProjectResponseDto toDto(Project entity);

    default ProjectStage mapStage(String stage) {
        if(stage == null) return null;
        return ProjectStage.valueOf(stage);
    }


}
