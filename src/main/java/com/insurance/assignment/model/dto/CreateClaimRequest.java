package com.insurance.assignment.model.dto;

import com.insurance.assignment.common.enumvalue.ClaimType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;


import java.math.BigDecimal;

@Data
public class CreateClaimRequest {

    @NotNull(message = "{error.claim.policyId.required}")
    private Long policyId;

    @NotNull(message = "{error.claim.claimAmount.required}")
    @Positive(message = "{error.claim.claimAmount.greater.than.zero}")
    private BigDecimal claimAmount;

    @NotNull(message = "{error.claim.claimType.required}")
    private ClaimType claimType;

    @Size(max = 500, message = "{error.claim.description.max500}")
    private String description;
}

