package com.insurance.assignment.common.annotation;

import com.insurance.assignment.common.validator.ApproveAmountValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ApproveAmountValidator.class)
public @interface ValidApproveAmount {
    String message() default "{error.claim.approvedAmount.required}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
