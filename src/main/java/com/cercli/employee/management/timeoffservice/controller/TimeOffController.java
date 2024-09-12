package com.cercli.employee.management.timeoffservice.controller;

import com.cercli.employee.management.timeoffservice.dto.TimeOffRequestDto;
import com.cercli.employee.management.timeoffservice.exception.TimeOffRequestConflictException;
import com.cercli.employee.management.timeoffservice.service.TimeOffService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/time-off")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TimeOffController {

    TimeOffService timeOffService;

    @PostMapping("/add")
    public ResponseEntity<Void> addTimeOffRequest(@RequestBody @Valid TimeOffRequestDto requestDto)
            throws TimeOffRequestConflictException {
        timeOffService.addTimeOffRequest(requestDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{employeeId}")
    public ResponseEntity<List<TimeOffRequestDto>> getRequestsForEmployee(@PathVariable UUID employeeId,
                                                                          @RequestParam String employeeTimeZone) {
        List<TimeOffRequestDto> timeOffRequests = timeOffService.getRequestsForEmployee(employeeId,
                                                                                        ZoneId.of(employeeTimeZone));
        return ResponseEntity.ok(timeOffRequests);
    }
}