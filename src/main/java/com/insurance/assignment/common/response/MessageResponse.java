package com.insurance.assignment.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@RequiredArgsConstructor
@Setter
@Getter
public class MessageResponse extends BaseResponse {
    private String message;
    private List<String> messages;
    public MessageResponse(String message) {
        this.message = message;
    }

    public MessageResponse(Integer status, String message) {
        this.message = message;
        this.status = status;
    }



    public MessageResponse(List<String> messages, Integer status) {
        this.messages = messages;
    }
}
