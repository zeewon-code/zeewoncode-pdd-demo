package com.zeewoncode.pdd_server.b.service;

import com.zeewoncode.dto.GrouponCreateDTO;
import com.zeewoncode.result.PageResult;
import com.zeewoncode.result.Result;
import com.zeewoncode.vo.GrouponAdminItemVO;

public interface GrouponService {
    /**
     * 店家获取店铺拼团列表
     * @param status
     * @param page
     * @param size
     * @return
     */
    PageResult<GrouponAdminItemVO> getGrouponsList(Integer status, Integer page, Integer size);

    /**
     * 店家创建拼团活动
     * @param grouponCreateDTO
     * @return
     */
    Result createGroupon(GrouponCreateDTO grouponCreateDTO);

    /**
     * 店家获取拼团活动详情
     * @param defineId
     * @return
     */
    Result<GrouponAdminItemVO> getGrouponDetail(Long defineId);
}
