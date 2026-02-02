package com.insurance.assignment.model.dto;

import com.insurance.assignment.common.enumvalue.ClaimStatus;
import com.insurance.assignment.common.enumvalue.ClaimType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
public class ClaimResponse {
    private Long claimId;
    private Long policyId;
    private String claimNumber;
    private LocalDate claimDate;
    private BigDecimal claimAmount;
    private BigDecimal approvedAmount;
    private ClaimStatus claimStatus;
    private ClaimType claimType;
    private String description;
    private Instant createdAt;
    private String note;
}
