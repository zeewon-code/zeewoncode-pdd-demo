package com.zeewoncode.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewCreateReq implements Serializable {

    private Long orderId;
    private Long orderItemId;
    private Integer rating;
    private String content;
    private List<String> images;
}
