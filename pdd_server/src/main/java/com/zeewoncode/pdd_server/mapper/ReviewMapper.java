package com.zeewoncode.pdd_server.mapper;

import com.zeewoncode.entity.Review;
import com.zeewoncode.req.ReviewsListQueryReq;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReviewMapper {
    /**
     * 根据spuId和查询条件获取评论列表
     * @param spuId
     * @param req
     * @return
     */
    List<Review> selectReviewList(Integer spuId, ReviewsListQueryReq req);
}
