package com.zeewoncode.pdd_server.c.service;

import com.zeewoncode.dto.GrouponInitiateDTO;
import com.zeewoncode.vo.GrouponInitiateVO;

public interface GrouponsService {
    /**
     * 拼团发起（团长下单）
     * @param grouponInitiateDTO
     * @return
     */
    GrouponInitiateVO initiateGroupon(GrouponInitiateDTO grouponInitiateDTO);
}
