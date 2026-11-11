package com.zeewoncode.pdd_server.b.service;

import com.zeewoncode.dto.GrouponCreateDTO;
import com.zeewoncode.dto.SpuStatusDTO;
import com.zeewoncode.result.PageResult;
import com.zeewoncode.result.Result;
import com.zeewoncode.vo.GrouponAdminItemVO;

public interface GrouponService {
    /**
     * 店家获取店铺拼团列表
     * @param status
     * @param page
     * @param size
     * @param userId
     * @return
     */
    PageResult<GrouponAdminItemVO> getGrouponsList(Integer status, Integer page, Integer size, Long userId);

    /**
     * 店家创建拼团活动
     * @param grouponCreateDTO
     * @param userId
     * @return
     */
    Result createGroupon(GrouponCreateDTO grouponCreateDTO, Long userId);

    /**
     * 店家获取拼团活动详情
     * @param userId
     * @param defineId
     * @return
     */
    Result<GrouponAdminItemVO> getGrouponDetail(Long defineId, Long userId);

    /**
     * 启用 / 停用拼团活动
     * @param defineId
     * @param dto
     * @param userId
     * @return
     */
    Result updateGrouponDefineStatus(Long defineId, SpuStatusDTO dto, Long userId);
}
