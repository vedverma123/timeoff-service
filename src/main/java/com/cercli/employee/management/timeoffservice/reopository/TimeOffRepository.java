package com.cercli.employee.management.timeoffservice.reopository;

import com.cercli.employee.management.timeoffservice.entity.TimeOffRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public interface TimeOffRepository extends JpaRepository<TimeOffRequest, Long> {

    @Query("SELECT t FROM TimeOffRequest t WHERE t.employeeId = :employeeId " +
            "AND (t.startDate <= :endDate AND t.endDate >= :startDate)")
    List<TimeOffRequest> findOverlappingRequests(@Param("employeeId") UUID employeeId,
                                                 @Param("startDate") ZonedDateTime startDate,
                                                 @Param("endDate") ZonedDateTime endDate);

    List<TimeOffRequest> findByEmployeeId(UUID employeeId);
}
