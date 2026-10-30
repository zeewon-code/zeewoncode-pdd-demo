package com.zeewoncode.pdd_server.b.service;

import com.zeewoncode.result.PageResult;
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
}
