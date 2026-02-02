package com.insurance.assignment.common.response;

import com.insurance.assignment.common.object.DataTable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DataTableResponse extends BaseResponse {
    private DataTable dataTable;
    private String message;
}
