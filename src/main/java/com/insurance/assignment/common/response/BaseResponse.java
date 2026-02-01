package com.insurance.assignment.common.response;

import com.insurance.assignment.common.CONSTANTS;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseResponse {
    protected Integer status = CONSTANTS.HTTP_RESPONSE.STATUS_SUCCESS;
}
