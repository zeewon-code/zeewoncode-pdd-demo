package com.zeewoncode.pdd_server.security;

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

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
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
                        Integer userType = JwtUtil.getUserType(claims);
                        LoginUser loginUser = new LoginUser(userId, userType);

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