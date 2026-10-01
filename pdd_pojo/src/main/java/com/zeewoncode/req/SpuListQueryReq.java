package com.zeewoncode.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 商品列表查询请求参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SpuListQueryReq implements Serializable {

        private Integer categoryId; //二级分类筛选
        private String keyword; //标题模糊搜索
        /**
         * 排序方式: sales, priceAsc, priceDesc, newest
         * 默认值: sales
         */

        private String sort = "sales";
        private Integer page = 1;
        private Integer pageSize = 10;
}
