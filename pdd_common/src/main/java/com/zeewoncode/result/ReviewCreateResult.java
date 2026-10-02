package com.zeewoncode.result;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class ReviewCreateResult implements Serializable {

    private Long reviewId;  // 评价ID
}
