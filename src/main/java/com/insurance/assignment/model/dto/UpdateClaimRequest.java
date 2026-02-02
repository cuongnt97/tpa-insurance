package com.insurance.assignment.model.dto;

import com.insurance.assignment.common.annotation.ValidApproveAmount;
import com.insurance.assignment.common.enumvalue.ClaimStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ValidApproveAmount
public class UpdateClaimRequest {

    @NotNull(message = "{error.claim.claimStatus.required}")
    private ClaimStatus newStatus;
    private BigDecimal approvedAmount;
    @Size(max = 4000, message = "{error.claim.note.max4000}")
    private String note;
}
