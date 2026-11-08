package com.zeewoncode.pdd_server.a.service;

import com.zeewoncode.result.PageResult;
import com.zeewoncode.vo.GrouponAdminItemVO;

public interface GrouponService {
    /**
     * 获取拼团活动监控列表
     * @param page
     * @param size
     * @return
     */
    PageResult<GrouponAdminItemVO> listGroupon(Integer page, Integer size);
}
