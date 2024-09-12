package com.cercli.employee.management.timeoffservice.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Table
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    Long id;

    @Column(name = "name", nullable = false, unique = true)
    String name;

}
