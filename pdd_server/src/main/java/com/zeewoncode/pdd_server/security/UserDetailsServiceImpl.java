//package com.zeewoncode.pdd_server.security;
//
//import com.zeewoncode.pdd_server.mapper.UserMapper;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.LockedException;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class UserDetailsServiceImpl implements UserDetailsService {
//
//    @Autowired
//    private UserMapper userMapper;
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        User user = userMapper.selectBy(username);
//        if (user == null) {
//            throw new UsernameNotFoundException("用户不存在");
//        }
//
//        // 🚨 登录时校验冻结状态
//        if (user.getStatus() == 0) { // 假设 0 是冻结
//            // 抛出 LockedException，Security 会将其视为认证失败
//            throw new LockedException("您的账号已被冻结，请联系管理员");
//        }
//
//        // ... 构建并返回 UserDetails 对象
//        return new LoginUser(user);
//    }
//}