package com.insurance.assignment.common.validator;

import com.insurance.assignment.common.annotation.ValidApproveAmount;
import com.insurance.assignment.common.enumvalue.ClaimStatus;
import com.insurance.assignment.model.dto.UpdateClaimRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;

public class ApproveAmountValidator
        implements ConstraintValidator<ValidApproveAmount, UpdateClaimRequest> {

    @Override
    public boolean isValid(UpdateClaimRequest req, ConstraintValidatorContext ctx) {

        if (req == null) {
            return true;
        }

        if (ClaimStatus.APPROVED.equals(req.getNewStatus())) {
            if (req.getApprovedAmount() == null|| req.getApprovedAmount().compareTo(BigDecimal.ZERO) <= 0) {
                ctx.disableDefaultConstraintViolation();
                ctx.buildConstraintViolationWithTemplate("{error.claim.approvedAmount.required}")
                        .addPropertyNode("approvedAmount")
                        .addConstraintViolation();
                return false;
            }
        }
        return true;
    }
}