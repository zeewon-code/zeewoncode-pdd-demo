package com.zeewoncode.pdd_server.a.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zeewoncode.context.BaseContext;
import com.zeewoncode.entity.User;
import com.zeewoncode.pdd_server.a.service.MerchantService;
import com.zeewoncode.pdd_server.mapper.MerchantMapper;
import com.zeewoncode.pdd_server.mapper.UserMapper;
import com.zeewoncode.result.PageResult;
import com.zeewoncode.vo.MerchantVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MerchantServiceImpl implements MerchantService {

    @Autowired
    private MerchantMapper merchantMapper;
    @Autowired
    private UserMapper userMapper;

    /**
     * 获取店铺列表
     * @param status
     * @param keyword
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageResult<MerchantVO> getMerchantsList(Integer status, String keyword, Integer page, Integer size) {
        Long userId = BaseContext.getCurrentId().longValue();
        if (!isAdmin(userId)) {
            return PageResult.empty(new PageInfo());
        }
        PageHelper.startPage(page, size);
        List<MerchantVO> merchantVOList = merchantMapper.selectList(status, keyword);
        PageInfo<MerchantVO> pageInfo = new PageInfo<>(merchantVOList);
        if (merchantVOList == null || merchantVOList.size() == 0) {
            return PageResult.empty(pageInfo);
        }
        return PageResult.of(pageInfo, merchantVOList);
    }

    // 校验当前用户是否为管理员
    private boolean isAdmin(Long userId) {
        User user = userMapper.selectById(userId.intValue());
        if (user == null || user.getUserType() != 9
                || user.getStatus() != 1 || user.getDeletedFlag() != 0) {
            return false;
        }
        return true;
    }
}
