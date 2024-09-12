package com.cercli.employee.management.timeoffservice.rule;

import com.cercli.employee.management.timeoffservice.dto.TimeOffRequestDto;

import java.util.List;

public interface TimeOffRule {
    boolean isValid(TimeOffRequestDto newRequest, List<TimeOffRequestDto> existingRequests);
}
