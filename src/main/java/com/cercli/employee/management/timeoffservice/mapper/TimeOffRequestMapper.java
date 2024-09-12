package com.cercli.employee.management.timeoffservice.mapper;

import com.cercli.employee.management.timeoffservice.dto.TimeOffRequestDto;
import com.cercli.employee.management.timeoffservice.entity.TimeOffRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TimeOffRequestMapper {

    TimeOffRequest mapToEntity(TimeOffRequestDto source);

    TimeOffRequestDto mapToDto(TimeOffRequest source);
}
