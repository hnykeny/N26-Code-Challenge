package com.n26.dto;

import lombok.Data;

@Data
public class StatsDto {
    private String sum;
    private String avg;
    private String max;
    private String min;
    private Long count;
}
