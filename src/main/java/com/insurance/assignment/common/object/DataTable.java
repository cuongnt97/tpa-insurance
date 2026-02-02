package com.insurance.assignment.common.object;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
public class DataTable {
    private List<?> content;
    private Long total;
    private int limit;
    private int offset;
}
