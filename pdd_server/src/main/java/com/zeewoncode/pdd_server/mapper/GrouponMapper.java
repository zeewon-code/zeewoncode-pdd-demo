package com.zeewoncode.pdd_server.mapper;

import com.zeewoncode.entity.GrouponDefine;
import com.zeewoncode.entity.GrouponInstance;
import com.zeewoncode.entity.Order;
import com.zeewoncode.pdd_server.mapper.po.GrouponInstancesCount;
import com.zeewoncode.pdd_server.mapper.po.InstanceRef;
import org.apache.ibatis.annotations.*;

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
     * 增加拼团实例当前拼团人数，当拼团人数凑齐时修改该团状态为已拼成
     * @param instanceId
     * @return
     */
    int addGrouponInstanceCurrentCount(Long instanceId);

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

    /**
     * 根据用户id查询该用户的所有拼团实例id，在每个拼团实例中的角色，开始发起或参与拼团的时间
     * @param userId
     * @return
     */
    List<InstanceRef> selectMyGrouponRefs(Long userId);

    /**
     * 根据拼团实例id列表查询拼团实例
     * @param instanceIds
     * @return
     */
    List<GrouponInstance> selectGrouponInstanceByIds(List<Long> instanceIds);

    /**
     * 根据拼团活动id列表查询拼团活动
     * @param defineIds
     * @return
     */
    List<GrouponDefine> selectGrouponDefineByIds(List<Long> defineIds);

    /**
     * 根据拼团实例id更新拼团实例状态
      * @param status
     */
    @Update("update groupon_instance set status = #{status} where id = #{instanceId}")
    void updateGrouponInstanceStatus(Long instanceId, Integer status);

    /**
     * 减少拼团实例当前拼团人数
     * @param instanceId
     */
    void reduceGrouponInstanceCurrentCount(Long instanceId);

    /**
     * 根据拼团活动id查询拼团实例id列表
     * @param defineId
     * @return
     */
    @Select("select id from groupon_instance where define_id = #{defineId}")
    List<Long> selectGrouponInstanceByDefineId(Long defineId);

    /**
     * 根据spu_id列表查询拼团活动
     * @param spuIds
     * @return
     */
    List<GrouponDefine> selectGrouponDefineListBySpuIds(List<Long> spuIds);

    /**
     * 根据sku_id查询拼团活动
     * @param id
     * @return
     */
    @Select("select * from groupon_define where sku_id = #{id}")
    GrouponDefine selectGrouponDefineBySkuId(Long id);

    /**
     * 根据拼团活动id更新拼团活动状态
     * @param id
     * @param status
     */
    @Update("update groupon_define set status = #{status} where id = #{id}")
    int updateGrouponDefineStatus(Long id, int status);

    /**
     * 根据拼团活动id和状态删除拼团实例
     * @param defineId
     * @param status
     */
    int deleteGrouponInstanceByDefineIdAndStatus(Long defineId, int status);

    /**
     * 根据拼团活动id和状态查询拼团实例
     * @param defineId
     * @param status
     * @return
     */
    List<GrouponInstance> selectGrouponInstanceByDefineIdAndStatus(Long defineId, int status);

    /**
     * 根据拼团实例id列表更新拼团实例状态
     * @param instanceIds
     * @param status
     */
    void updateGrouponInstanceStatusByInstanceIds(List<Long> instanceIds, int status);

    /**
     * 根据spu_id列表查询拼团活动
     * @param spuIds
     * @return
     */
    List<GrouponDefine> selectGrouponDefinesBySpuIdAndStatus(List<Long> spuIds, Integer status);

    /**
     * 根据拼团活动id列表查询拼团实例数量
     * @param grouponInstanceIds
     * @return
     */
    List<GrouponInstancesCount> selectDefineCountByInstanceIds(List<Long> grouponInstanceIds);

    /**
     * 根据拼团活动id列表查询拼团实例id列表
     * @param defineIdsList
     * @return
     */
    List<Long> selectGrouponInstanceIdsByDefineIds(List<Long> defineIdsList);
}
