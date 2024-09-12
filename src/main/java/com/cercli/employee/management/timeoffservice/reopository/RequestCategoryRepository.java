package com.cercli.employee.management.timeoffservice.reopository;

import com.cercli.employee.management.timeoffservice.entity.RequestCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestCategoryRepository extends JpaRepository<RequestCategory, Long> {
}
