package com.orio.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TimeDto{
    private String day;
    private String time;
    private String memo;
    private String file;
}