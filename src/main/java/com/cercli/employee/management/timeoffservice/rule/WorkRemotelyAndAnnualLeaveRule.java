package com.cercli.employee.management.timeoffservice.rule;

import com.cercli.employee.management.timeoffservice.dto.TimeOffRequestDto;
import com.cercli.employee.management.timeoffservice.entity.RequestCategory;
import com.cercli.employee.management.timeoffservice.reopository.RequestCategoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static com.cercli.employee.management.timeoffservice.constants.AppConstants.ANNUAL_LEAVE;
import static com.cercli.employee.management.timeoffservice.constants.AppConstants.WORK_REMOTELY;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
@Component
public class WorkRemotelyAndAnnualLeaveRule implements TimeOffRule {

    RequestCategoryRepository categoryRepository;

    @Override
    public boolean isValid(final TimeOffRequestDto newRequest,
                           final List<TimeOffRequestDto> existingRequests) {
        for (TimeOffRequestDto existing : existingRequests) {
            // Check if the existing request overlaps with the new one
            if (datesOverlap(existing, newRequest)) {
                // Check if the overlapping requests are "Work Remotely" and "Annual Leave"
                if (!isWorkRemotelyAndAnnualLeave(existing, newRequest)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean datesOverlap(final TimeOffRequestDto request1, final TimeOffRequestDto request2) {
        ZonedDateTime start1 = request1.getStartDate().withZoneSameInstant(ZoneOffset.UTC);
        ZonedDateTime end1 = request1.getEndDate().withZoneSameInstant(ZoneOffset.UTC);
        ZonedDateTime start2 = request2.getStartDate().withZoneSameInstant(ZoneOffset.UTC);
        ZonedDateTime end2 = request2.getEndDate().withZoneSameInstant(ZoneOffset.UTC);

        return start1.isBefore(end2) && end1.isAfter(start2);
    }

    private boolean isWorkRemotelyAndAnnualLeave(final TimeOffRequestDto request1, final TimeOffRequestDto request2) {
        Optional<RequestCategory> category1Opt = categoryRepository.findById(request1.getRequestCategoryId());
        Optional<RequestCategory> category2Opt = categoryRepository.findById(request2.getRequestCategoryId());

        if (category1Opt.isEmpty() || category2Opt.isEmpty()) {
            log.error("Category not found for request IDs: {}, {}", request1.getRequestCategoryId(), request2.getRequestCategoryId());
            throw new IllegalArgumentException("Invalid request category");
        }

        String category1 = category1Opt.get().getName();
        String category2 = category2Opt.get().getName();
        return (WORK_REMOTELY.equals(category1) && ANNUAL_LEAVE.equals(category2)) ||
                (ANNUAL_LEAVE.equals(category1) && WORK_REMOTELY.equals(category2));
    }
}