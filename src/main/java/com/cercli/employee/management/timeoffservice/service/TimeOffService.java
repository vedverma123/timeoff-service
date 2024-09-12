package com.cercli.employee.management.timeoffservice.service;

import com.cercli.employee.management.timeoffservice.dto.TimeOffRequestDto;
import com.cercli.employee.management.timeoffservice.entity.TimeOffRequest;
import com.cercli.employee.management.timeoffservice.exception.TimeOffRequestConflictException;
import com.cercli.employee.management.timeoffservice.mapper.TimeOffRequestMapper;
import com.cercli.employee.management.timeoffservice.reopository.TimeOffRepository;
import com.cercli.employee.management.timeoffservice.rule.TimeOffRuleEngine;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static com.cercli.employee.management.timeoffservice.constants.AppConstants.UTC_TIME_ZONE;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TimeOffService {

    TimeOffRuleEngine ruleEngine;
    TimeOffRepository repository;
    TimeOffRequestMapper requestMapper;

    public void addTimeOffRequest(final TimeOffRequestDto request) {
        // Convert from employee's local time zone (employeeZoneId) to UTC for storage
        ZonedDateTime utcStartDate = request.getStartDate().withZoneSameInstant(ZoneId.of(UTC_TIME_ZONE));
        ZonedDateTime utcEndDate = request.getEndDate().withZoneSameInstant(ZoneId.of(UTC_TIME_ZONE));

        // Fetch overlapping requests
        List<TimeOffRequestDto> existingRequests = repository.findOverlappingRequests(request.getEmployeeId(),
                        utcStartDate, utcEndDate)
                .stream()
                .map(requestMapper::mapToDto)
                .toList();

        if(!ruleEngine.validate(request, existingRequests)) {
            throw new TimeOffRequestConflictException("Time-off request conflicts with existing request");
        }

        // Store the request with times converted to UTC
        TimeOffRequest timeOffRequest = requestMapper.mapToEntity(request);
        timeOffRequest.setStartDate(utcStartDate);
        timeOffRequest.setEndDate(utcEndDate);
        repository.save(timeOffRequest);
    }

    public List<TimeOffRequestDto> getRequestsForEmployee(UUID employeeId, ZoneId employeeZoneId) {
        List<TimeOffRequest> timeOffRequests = repository.findByEmployeeId(employeeId);

        // Convert stored UTC times back to employee's local time zone
        timeOffRequests.forEach(request -> {
            request.setStartDate(request.getStartDate().withZoneSameInstant(employeeZoneId));
            request.setEndDate(request.getEndDate().withZoneSameInstant(employeeZoneId));
        });

        return timeOffRequests.stream().map(requestMapper::mapToDto).toList();
    }
}
