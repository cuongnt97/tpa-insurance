package com.insurance.assignment.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Setter
@Getter
public class MessageResponse extends BaseResponse {
    private String message;
    public MessageResponse(String message) {
        this.message = message;
    }

    public MessageResponse(Integer status, String message) {
        this.message = message;
        this.status = status;
    }
}
