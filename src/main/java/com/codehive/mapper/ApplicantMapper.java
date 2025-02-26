package com.codehive.mapper;

import com.codehive.dto.ApplicantResponseDto;
import com.codehive.entity.PositionApplication;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ApplicantMapper {

    ApplicantMapper INSTANCE = Mappers.getMapper(ApplicantMapper.class);

    @Mapping(source = "applicant.fullName", target = "applicantName")
    @Mapping(source = "applicant.username", target = "applicantUsername")
    @Mapping(source = "position.roleName", target = "positionName")
    @Mapping(source = "status", target = "applicationStatus")
    @Mapping(source = "position.project.name", target = "projectName")
    ApplicantResponseDto toDto(PositionApplication application);
}
