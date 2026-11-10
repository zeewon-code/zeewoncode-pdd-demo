package com.zeewoncode.pdd_server.security;

import com.zeewoncode.constant.StatusConstant;
import com.zeewoncode.entity.User;
import com.zeewoncode.pdd_server.mapper.UserMapper;
import com.zeewoncode.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 从 Authorization: Bearer xxx 中解析 JWT，
 * 认证成功后写入 SecurityContext，同时兼容现有 BaseContext。
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String HEADER = "Authorization";
    private static final String PREFIX = "Bearer ";

    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserMapper userMapper) {
        this.jwtUtil = jwtUtil;
        this.userMapper = userMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String header = request.getHeader(HEADER);
            if (header != null && header.toLowerCase().startsWith(PREFIX.toLowerCase())) {
                String token = header.substring(PREFIX.length()).trim();
                Claims claims = jwtUtil.parseToken(token);
                if (claims != null) {
                    Integer userId = JwtUtil.getUserId(claims);
                    if (userId != null) {
//                        // 从jwt令牌中拿到用户类型
//                        Integer userType = JwtUtil.getUserType(claims);
                        // 从数据库中获取用户类型
                        User user = userMapper.selectById(userId);

                        // 用户被删除或已冻结
                        if (user == null || StatusConstant.DISABLE == user.getStatus()) {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write(
                                    "{\"code\":401,\"msg\":\"账号已被冻结或不存在\",\"data\":null}");
                            return;
                        }

                        LoginUser loginUser = new LoginUser(userId, user.getUserType());
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        loginUser, null, loginUser.getAuthorities());

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }
            filterChain.doFilter(request, response);
        } finally {
            // 必须清理，避免线程池复用导致串号
            SecurityContextHolder.clearContext();
        }
    }
}