package com.zeewoncode.pdd_server.mapper;

import com.zeewoncode.entity.GrouponDefine;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface GrouponMapper {
    /**
     * 根据spu_id查询该商品的拼团活动
     * @param id
     * @return
     */
    @Select("select * from groupon_define where spu_id = #{id} order by start_time ASC")
    List<GrouponDefine> selectGrouponDefineListBySpuId(Integer id);
}
