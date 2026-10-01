package com.zeewoncode.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewStat implements Serializable {

    private Double avgRating;
    private Integer total;
    private Map<String, Integer> ratingDist; // 评分分布  { "5": 30, "4": 3, "3": 1, "2": 1, "1": 1 }
}
