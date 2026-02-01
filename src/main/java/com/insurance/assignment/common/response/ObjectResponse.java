package com.insurance.assignment.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ObjectResponse extends BaseResponse{
    private Object object;
    private String message;
}
