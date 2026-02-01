package com.insurance.assignment.common.response;

import com.insurance.assignment.common.object.DataTable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class DataTableResponse extends BaseResponse{
    private DataTable dataTable;
    private String message;
}
