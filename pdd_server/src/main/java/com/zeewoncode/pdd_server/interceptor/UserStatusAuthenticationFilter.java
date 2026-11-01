//package com.zeewoncode.pdd_server.interceptor;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.authentication.AnonymousAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Component
//@RequiredArgsConstructor
//public class UserStatusAuthenticationFilter extends OncePerRequestFilter {
//
//    private final UserStatusChecker userStatusChecker;
//    private final ObjectMapper objectMapper; // 用于返回 JSON 格式错误
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain filterChain) throws ServletException, IOException {
//
//        // 1. 获取当前 Security 上下文中的认证信息
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        // 2. 核心判断：只有【已认证】且【非匿名】的用户，才需要校验冻结状态
//        // 注册接口、未携带 Token 的请求，这里 authentication 为 null 或 Anonymous，直接跳过
//        if (authentication != null && authentication.isAuthenticated()
//                && !(authentication instanceof AnonymousAuthenticationToken)) {
//
//            // 获取当前登录用户 ID (根据你的 LoginUser 对象结构调整)
//            Long userId = getCurrentUserId(authentication);
//
//            if (userId != null && userStatusChecker.isFrozen(userId)) {
//                // 🚨 发现被冻结：清除 Security 上下文（强制下线），并阻断请求
//                SecurityContextHolder.clearContext();
//
//                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
//                response.setContentType("application/json;charset=UTF-8");
//
//                // 返回统一的错误 JSON 格式
//                Map<String, Object> errorResult = new HashMap<>();
//                errorResult.put("code", 401);
//                errorResult.put("msg", "您的账号已被冻结，强制下线");
//                response.getWriter().write(objectMapper.writeValueAsString(errorResult));
//                return; // 终止过滤链
//            }
//        }
//
//        // 3. 正常放行（包括注册接口、未登录接口、以及状态正常的已登录接口）
//        filterChain.doFilter(request, response);
//    }
//
//    private Long getCurrentUserId(Authentication authentication) {
//        Object principal = authentication.getPrincipal();
//        if (principal instanceof LoginUser) {
//            return ((LoginUser) principal).getUserId();
//        }
//        return null;
//    }
//}