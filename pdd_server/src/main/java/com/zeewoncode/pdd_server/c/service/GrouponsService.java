package com.zeewoncode.pdd_server.c.service;

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
     * @return
     */
    GrouponInitiateVO initiateGroupon(GrouponInitiateDTO grouponInitiateDTO);

    /**
     * 拼团加入（团员下单）,进入他人团
     * @param instanceId
     * @param grouponJoinDTO
     * @return
     */
    GrouponJoinVO joinGroupon(Long instanceId, GrouponJoinDTO grouponJoinDTO);

    /**
     * 获取我的拼团列表
     * @return
     */
    PageResult<GrouponInstanceVO> getGrouponInstanceList(Integer pageNum, Integer pageSize);
}
