package com.cercli.employee.management.timeoffservice.rule;

import com.cercli.employee.management.timeoffservice.dto.TimeOffRequestDto;
import com.cercli.employee.management.timeoffservice.entity.RequestCategory;
import com.cercli.employee.management.timeoffservice.reopository.RequestCategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static com.cercli.employee.management.timeoffservice.constants.AppConstants.ANNUAL_LEAVE;
import static com.cercli.employee.management.timeoffservice.constants.AppConstants.WORK_REMOTELY;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class WorkRemotelyAndAnnualLeaveRuleTest {

    @Mock
    private RequestCategoryRepository categoryRepository;

    @InjectMocks
    private WorkRemotelyAndAnnualLeaveRule workRemotelyAndAnnualLeaveRule;

    private TimeOffRequestDto newRequest;
    private TimeOffRequestDto existingRequest;
    private RequestCategory workRemotelyCategory;
    private RequestCategory annualLeaveCategory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup Request Categories
        workRemotelyCategory = new RequestCategory();
        workRemotelyCategory.setName(WORK_REMOTELY);

        annualLeaveCategory = new RequestCategory();
        annualLeaveCategory.setName(ANNUAL_LEAVE);

        // Setup TimeOffRequestDto for new and existing requests
        newRequest = new TimeOffRequestDto();
        newRequest.setStartDate(ZonedDateTime.now().plusDays(1));
        newRequest.setEndDate(ZonedDateTime.now().plusDays(5));
        newRequest.setRequestCategoryId(1L);

        existingRequest = new TimeOffRequestDto();
        existingRequest.setStartDate(ZonedDateTime.now().plusDays(2));
        existingRequest.setEndDate(ZonedDateTime.now().plusDays(4));
        existingRequest.setRequestCategoryId(2L);
    }

    @Test
    void testIsValid_withNonOverlappingDates_returnsTrue() {
        // Set up requests with non-overlapping dates
        newRequest.setStartDate(ZonedDateTime.now().plusDays(10));
        newRequest.setEndDate(ZonedDateTime.now().plusDays(15));

        // Test case where dates don't overlap
        boolean result = workRemotelyAndAnnualLeaveRule.isValid(newRequest, List.of(existingRequest));

        // Expect true since no overlap exists
        assertTrue(result);
        verifyNoInteractions(categoryRepository);
    }

    @Test
    void testIsValid_withOverlappingWorkRemotelyAndAnnualLeave_returnsTrue() {
        // Mock repository responses for request categories
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(workRemotelyCategory));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(annualLeaveCategory));

        // Test valid case with Work Remotely and Annual Leave overlapping
        boolean result = workRemotelyAndAnnualLeaveRule.isValid(newRequest, List.of(existingRequest));

        // Expect true since overlapping is between Work Remotely and Annual Leave
        assertTrue(result);
        verify(categoryRepository, times(2)).findById(anyLong());
    }

    @Test
    void testIsValid_withOverlappingInvalidCategories_returnsFalse() {
        // Setup invalid category response
        RequestCategory someOtherCategory = new RequestCategory();
        someOtherCategory.setName("Sick Leave");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(someOtherCategory));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(annualLeaveCategory));

        // Test invalid case with overlapping categories that are not Work Remotely and Annual Leave
        boolean result = workRemotelyAndAnnualLeaveRule.isValid(newRequest, List.of(existingRequest));

        // Expect false since categories don't match Work Remotely and Annual Leave
        assertFalse(result);
        verify(categoryRepository, times(2)).findById(anyLong());
    }

    @Test
    void testIsValid_withMissingRequestCategory_throwsException() {
        // Simulate missing request category
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Expect IllegalArgumentException when a request category is missing
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            workRemotelyAndAnnualLeaveRule.isValid(newRequest, List.of(existingRequest));
        });

        assertEquals("Invalid request category", exception.getMessage());
        verify(categoryRepository, times(2)).findById(anyLong());
    }

    @Test
    void testDatesOverlap_withNonOverlappingDates_returnsTrue() {
        // Set up non-overlapping dates
        existingRequest.setStartDate(ZonedDateTime.now().plusDays(10));
        existingRequest.setEndDate(ZonedDateTime.now().plusDays(15));

        boolean result = workRemotelyAndAnnualLeaveRule.isValid(newRequest, List.of(existingRequest));

        // Expect false since the dates don't overlap
        assertTrue(result);
    }
}
