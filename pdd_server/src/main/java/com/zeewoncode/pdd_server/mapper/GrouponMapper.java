package com.zeewoncode.pdd_server.mapper;

import com.zeewoncode.entity.GrouponDefine;
import com.zeewoncode.entity.GrouponInstance;
import com.zeewoncode.entity.Order;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
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

    /**
     * 根据拼团活动id查询拼团活动
     * @param defineId
     * @return
     */
    @Select("select * from groupon_define where id = #{defineId}")
    GrouponDefine selectGrouponDefineById(Long defineId);

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

    /**
     * 根据拼团实例id查询拼团实例
     * @param instanceId
     * @return
     */
    @Select("select * from groupon_instance where id = #{instanceId}")
    GrouponInstance selectGrouponInstanceById(Long instanceId);

    /**
     * 更新拼团实例当前拼团人数，当拼团人数凑齐时修改该团状态为已拼成
     * @param instanceId
     * @return
     */
    int updateGrouponInstanceCurrentCount(Long instanceId);

    /**
     * 根据拼团活动id和团长用户id查询拼团实例
     * @param defineId
     * @param userId
     * @return
     */
    @Select("select * from groupon_instance where define_id = #{defineId} and leader_user_id = #{userId}")
    GrouponInstance selectGrouponInstanceByDefineIdAndLeaderUserId(Long defineId, Long userId);

    /**
     * 根据用户id和拼团实例id查询订单
     * @param userId
     * @param instanceId
     * @return
     */
    @Select("select * from orders where user_id = #{userId} and groupon_instance_id = #{instanceId}")
    Order selectOrderByUserIdAndInstanceId(Long userId, Long instanceId);
}
