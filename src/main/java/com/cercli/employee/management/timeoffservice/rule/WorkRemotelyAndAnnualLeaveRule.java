package com.cercli.employee.management.timeoffservice.rule;

import com.cercli.employee.management.timeoffservice.dto.TimeOffRequestDto;
import com.cercli.employee.management.timeoffservice.entity.RequestCategory;
import com.cercli.employee.management.timeoffservice.reopository.RequestCategoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Optional;

@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class WorkRemotelyAndAnnualLeaveRule implements TimeOffRule {

    private static final String WORK_REMOTELY = "Work Remotely";
    private static final String ANNUAL_LEAVE = "Annual Leave";

    final RequestCategoryRepository categoryRepository;

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
        return request1.getStartDate().isBefore(request2.getEndDate()) &&
                request1.getEndDate().isAfter(request2.getStartDate());
    }

    private boolean isWorkRemotelyAndAnnualLeave(final TimeOffRequestDto request1, final TimeOffRequestDto request2) {
        Optional<RequestCategory> category1Opt = categoryRepository.findById(request1.getRequestCategoryId());
        Optional<RequestCategory> category2Opt = categoryRepository.findById(request2.getRequestCategoryId());

        if(category1Opt.isEmpty() ||
                category2Opt.isEmpty() ||
                ObjectUtils.isEmpty(category1Opt.get().getName()) ||
                ObjectUtils.isEmpty(category2Opt.get().getName())) {
            return false;
        }

        String category1 = category1Opt.get().getName();
        String category2 = category2Opt.get().getName();
        return (WORK_REMOTELY.equals(category1) && ANNUAL_LEAVE.equals(category2)) ||
                (ANNUAL_LEAVE.equals(category1) && WORK_REMOTELY.equals(category2));
    }
}