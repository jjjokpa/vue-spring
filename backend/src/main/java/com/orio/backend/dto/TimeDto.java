package com.orio.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TimeDto{
    private String day;
    @JsonProperty("start_time")
    private String startTime;
    private String time;
    private String memo;
    private String file;
}