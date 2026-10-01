package com.zeewoncode.pdd_server.interceptor;


import com.zeewoncode.context.BaseContext;
import com.zeewoncode.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT拦截器
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;


    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        // 获取请求头中的Authorization字段
        String header = request.getHeader("Authorization");
        if (header == null || header.isEmpty() || !header.toLowerCase().startsWith("bearer ")) {
            // 没有token，放行，交给controller判断是否需要登录
            return true;
        }
        // 截取bearer后的token
        String token = header.substring(7).trim();
        Claims claims = jwtUtil.parseToken(token);
        if (claims == null) {
            // token无效/过期，返回401
            response.setContentType("application/json;charset=utf-8");
            response.setStatus(401);
            response.getWriter().write("{\"code\":401,\"msg\":\"token无效或已过期\",\"data\":null}");
            return false;
        }
        // 解析成功，将用户id存入线程，方便controller使用
        Integer userId = JwtUtil.getUserId(claims);
        BaseContext.setCurrentId(userId);
        return true;
    }


    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) throws Exception {
        // 请求结束必须清掉，否则线程池复用会导致下一个用户拿到上一个用户的 ID（越权）
        BaseContext.removeCurrentId();
    }

}
