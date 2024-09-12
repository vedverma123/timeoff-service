package com.cercli.employee.management.timeoffservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.ZonedDateTime;
import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TimeOffRequestDto {
    @NotNull
    UUID employeeId;
    @NotNull
    Long requestCategoryId;
    @NotNull
    ZonedDateTime startDate;
    @NotNull
    ZonedDateTime endDate;
}
