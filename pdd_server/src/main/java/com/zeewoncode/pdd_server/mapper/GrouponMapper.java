package com.zeewoncode.pdd_server.mapper;

import com.zeewoncode.entity.GrouponDefine;
import com.zeewoncode.entity.GrouponInstance;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
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

    /**
     * 根据拼团活动id查询拼团活动
     * @param defineId
     * @return
     */
    @Select("select * from groupon_define where id = #{defineId}")
    GrouponDefine selectGrouponDefineById(Integer defineId);

    /**
     * 插入拼团实例
     * @param grouponInstance
     */
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into groupon_instance (define_id, leader_user_id, current_count, " +
            "target_count, status, expire_at) " +
            "values (#{defineId}, #{leaderUserId},#{currentCount}, #{targetCount}, " +
            "#{status}, #{expireAt})")
    void insertGrouponInstance(GrouponInstance grouponInstance);
}
