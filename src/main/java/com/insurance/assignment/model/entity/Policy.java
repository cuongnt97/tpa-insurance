package com.insurance.assignment.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "policy")
@Data
public class Policy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "policy_code", nullable = false, unique = true)
    private String policyCode;

    @Column(nullable = false)
    private Integer status;
}
