package com.insurance.assignment.model.entity;

import com.insurance.assignment.common.enumvalue.ClaimStatus;
import com.insurance.assignment.common.enumvalue.ClaimType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Builder
@Data
@Entity(name = "claim")
@NoArgsConstructor
@AllArgsConstructor
public class Claim {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "policy_id", nullable = false)
    private Long policyId;

    @Column(name = "claim_number", nullable = false, unique = true)
    private String claimNumber;

    @Column(name = "claim_date", nullable = false)
    private LocalDate claimDate;

    @Column(name = "claim_amount", nullable = false)
    private BigDecimal claimAmount;

    @Column(name = "approved_amount")
    private BigDecimal approvedAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "claim_status", nullable = false)
    private ClaimStatus claimStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "claim_type", nullable = false)
    private ClaimType claimType;

    @Column(length = 500)
    private String description;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

}
