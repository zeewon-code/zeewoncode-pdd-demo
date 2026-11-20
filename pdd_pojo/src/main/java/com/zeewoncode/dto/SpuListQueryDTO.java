package com.zeewoncode.dto;

import jakarta.validation.constraints.Pattern;
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
public class SpuListQueryDTO implements Serializable {

        private Long categoryId; //二级分类筛选
        @Pattern(regexp = "^\\S+$", message = "标题不能包含任何空白字符")
        private String keyword; //标题模糊搜索
        /**
         * 排序方式: sales, priceAsc, priceDesc, newest
         * 默认值: sales
         */

        @Builder.Default
        private String sort = "sales";
        @Builder.Default
        private Integer page = 1;
        @Builder.Default
        private Integer pageSize = 10;
}
