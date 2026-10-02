package com.zeewoncode.pdd_server.mapper;

import com.zeewoncode.entity.ProductReview;
import com.zeewoncode.entity.Review;
import com.zeewoncode.entity.ReviewStat;
import com.zeewoncode.req.ReviewsListQueryReq;
import com.zeewoncode.result.ReviewCreateResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

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


    /**
     * 根据spuId获取该商品的评分列表
     * @param id
     * @return
     */
    @Select("SELECT rating FROM product_review WHERE spu_id = #{id}")
    List<Review> selectReviewListBySpuId(Integer id);

    /**
     * 添加评论
     * @param productReview
     * @return
     */
    void insert(ProductReview productReview);
}
