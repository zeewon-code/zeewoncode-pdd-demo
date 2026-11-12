package com.zeewoncode.pdd_server.c.service;

import com.zeewoncode.dto.GrouponCancelDTO;
import com.zeewoncode.dto.GrouponInitiateDTO;
import com.zeewoncode.dto.GrouponJoinDTO;
import com.zeewoncode.result.PageResult;
import com.zeewoncode.vo.GrouponInitiateVO;
import com.zeewoncode.vo.GrouponInstanceVO;
import com.zeewoncode.vo.GrouponJoinVO;

public interface GrouponsService {
    /**
     * 拼团发起（团长下单）
     * @param grouponInitiateDTO
     * @param userId
     * @return
     */
    GrouponInitiateVO initiateGroupon(GrouponInitiateDTO grouponInitiateDTO,
                                      Long userId);

    /**
     * 拼团加入（团员下单）,进入他人团
     * @param instanceId
     * @param grouponJoinDTO
     * @param userId
     * @return
     */
    GrouponJoinVO joinGroupon(Long instanceId,
                              GrouponJoinDTO grouponJoinDTO,
                              Long userId);

    /**
     * 获取我的拼团列表
     * @param pageNum
     * @param pageSize
     * @param userId
     * @return
     */
    PageResult<GrouponInstanceVO> getGrouponInstanceList(Integer pageNum, Integer pageSize,
                                                         Long userId);

    /**
     * 取消拼团(团长取消拼团)
     * @param dto
     */
    void cancelGrouponByLeader(GrouponCancelDTO dto);

    /**
     * 团员取消拼团
     * @param dto
     * @param userId
     */
    void cancelGroupon(GrouponCancelDTO dto, Long userId);

    /**
     * 商品进行中的拼团（凑人页）
     * @param id
     * @param page
     * @param size
     * @return
     */
    PageResult<GrouponInstanceVO> getGrouponInstanceInSpu(Long id, Integer page, Integer size, Long userId);

    /**
     * 获取拼团详情
     * @param instanceId
     * @return
     */
    GrouponInstanceVO getGrouponInstanceDetail(Long instanceId);
}
