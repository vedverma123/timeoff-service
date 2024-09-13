package com.cercli.employee.management.timeoffservice.service;

import com.cercli.employee.management.timeoffservice.dto.TimeOffRequestDto;
import com.cercli.employee.management.timeoffservice.entity.TimeOffRequest;
import com.cercli.employee.management.timeoffservice.mapper.TimeOffRequestMapper;
import com.cercli.employee.management.timeoffservice.reopository.TimeOffRepository;
import com.cercli.employee.management.timeoffservice.rule.TimeOffRuleEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static com.cercli.employee.management.timeoffservice.constants.AppConstants.UTC_TIME_ZONE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TimeOffServiceTest {

    @Mock
    private TimeOffRuleEngine ruleEngine;

    @Mock
    private TimeOffRepository repository;

    @Mock
    private TimeOffRequestMapper requestMapper;

    @InjectMocks
    private TimeOffService timeOffService;

    private TimeOffRequestDto newRequest;
    private TimeOffRequest timeOffRequestEntity;
    private List<TimeOffRequestDto> existingRequests;
    private UUID employeeId;
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        employeeId = UUID.randomUUID();
        startDate = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
        endDate = startDate.plusDays(3);

        // Setup new request DTO
        newRequest = new TimeOffRequestDto();
        newRequest.setEmployeeId(employeeId);
        newRequest.setStartDate(startDate);
        newRequest.setEndDate(endDate);

        // Setup existing requests
        TimeOffRequestDto existingRequest = new TimeOffRequestDto();
        existingRequest.setEmployeeId(employeeId);
        existingRequest.setStartDate(startDate.minusDays(5));
        existingRequest.setEndDate(startDate.minusDays(2));

        existingRequests = List.of(existingRequest);

        // Setup entity mapping
        timeOffRequestEntity = new TimeOffRequest();
        timeOffRequestEntity.setEmployeeId(employeeId);
        timeOffRequestEntity.setStartDate(startDate);
        timeOffRequestEntity.setEndDate(endDate);
    }

    @Test
    void testAddTimeOffRequest_withValidRequest_shouldSave() {
        // Convert start/end date to UTC
        ZonedDateTime utcStartDate = startDate.withZoneSameInstant(ZoneId.of(UTC_TIME_ZONE));
        ZonedDateTime utcEndDate = endDate.withZoneSameInstant(ZoneId.of(UTC_TIME_ZONE));

        // Mock repository to return existing requests
        when(repository.findOverlappingRequests(employeeId, utcStartDate, utcEndDate))
                .thenReturn(List.of());
        when(ruleEngine.validate(newRequest, List.of())).thenReturn(true);
        when(requestMapper.mapToEntity(newRequest)).thenReturn(timeOffRequestEntity);

        // Test service method
        timeOffService.addTimeOffRequest(newRequest);

        // Verify saving the request
        verify(repository, times(1)).save(timeOffRequestEntity);
        assertEquals(utcStartDate, timeOffRequestEntity.getStartDate());
        assertEquals(utcEndDate, timeOffRequestEntity.getEndDate());
    }

    @Test
    void testGetRequestsForEmployee_withValidRequests_shouldReturnConvertedRequests() {
        ZoneId employeeZoneId = ZoneId.of("Asia/Kolkata");

        // Mock repository to return requests in UTC
        timeOffRequestEntity.setStartDate(startDate.withZoneSameInstant(ZoneId.of(UTC_TIME_ZONE)));
        timeOffRequestEntity.setEndDate(endDate.withZoneSameInstant(ZoneId.of(UTC_TIME_ZONE)));

        when(repository.findByEmployeeId(employeeId)).thenReturn(List.of(timeOffRequestEntity));
        when(requestMapper.mapToDto(timeOffRequestEntity)).thenReturn(newRequest);

        // Test service method
        List<TimeOffRequestDto> result = timeOffService.getRequestsForEmployee(employeeId, employeeZoneId);

        // Verify that the dates were converted back to employee's time zone
        assertEquals(1, result.size());
        assertEquals(employeeZoneId, result.get(0).getStartDate().getZone());
        assertEquals(employeeZoneId, result.get(0).getEndDate().getZone());

        verify(repository, times(1)).findByEmployeeId(employeeId);
        verify(requestMapper, times(1)).mapToDto(timeOffRequestEntity);
    }

    @Test
    void testGetRequestsForEmployee_withNoRequests_shouldReturnEmptyList() {
        // Mock repository to return no requests
        when(repository.findByEmployeeId(employeeId)).thenReturn(List.of());

        // Test service method
        List<TimeOffRequestDto> result = timeOffService.getRequestsForEmployee(employeeId, ZoneId.of("Asia/Kolkata"));

        // Verify empty list is returned
        assertTrue(result.isEmpty());
        verify(repository, times(1)).findByEmployeeId(employeeId);
    }

    @Test
    void testAddTimeOffRequest_withNullDates_shouldThrowException() {
        // Setup newRequest with null dates
        newRequest.setStartDate(null);
        newRequest.setEndDate(null);

        // Expect NullPointerException due to null dates
        assertThrows(NullPointerException.class, () -> {
            timeOffService.addTimeOffRequest(newRequest);
        });

        verifyNoInteractions(repository);
        verifyNoInteractions(ruleEngine);
    }
}
