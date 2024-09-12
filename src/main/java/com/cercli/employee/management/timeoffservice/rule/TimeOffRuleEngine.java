package com.cercli.employee.management.timeoffservice.rule;

import com.cercli.employee.management.timeoffservice.dto.TimeOffRequestDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

//@Component
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TimeOffRuleEngine {
    List<TimeOffRule> rules;

    public boolean validate(TimeOffRequestDto newRequest, List<TimeOffRequestDto> existingRequests) {
        for(TimeOffRule timeOffRule : rules) {
            if(!timeOffRule.isValid(newRequest, existingRequests)) {
                return false;
            }
        }
        return true;
    }
}
