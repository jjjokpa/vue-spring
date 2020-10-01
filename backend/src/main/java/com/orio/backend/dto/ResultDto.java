package com.orio.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResultDto {
    private String work_time;
    private String deduct_time;
    private String evaluate_time;
}