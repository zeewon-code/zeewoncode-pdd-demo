package com.zeewoncode.pdd_server.config;

import com.zeewoncode.pdd_server.security.JsonAccessDeniedHandler;
import com.zeewoncode.pdd_server.security.JsonAuthenticationEntryPoint;
import com.zeewoncode.pdd_server.security.JwtAuthenticationFilter;
import com.zeewoncode.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final JsonAuthenticationEntryPoint authenticationEntryPoint;
    private final JsonAccessDeniedHandler accessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 前后端分离 + JWT，关闭 CSRF
                .csrf(csrf -> csrf.disable())
                // 关闭默认登录页和 HTTP Basic
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .logout(logout -> logout.disable())
                // 无状态，不使用 Session
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 1. 注册、登录：放行
                        .requestMatchers("/api/auth/**").permitAll()
                        // 2. 错误页、接口文档：放行，避免异常被 401 掩盖
                        .requestMatchers("/error", "/doc.html", "/webjars/**",
                                "/v3/api-docs/**", "/swagger-resources/**", "/favicon.ico").permitAll()
                        // 3. A 端：管理员
                        .requestMatchers("/api/a/**").hasRole("ADMIN")
                        // 4. 开店申请：必须登录，但不限制角色（必须写在 /api/b/** 之前）
                        .requestMatchers("/api/b/merchant/apply").hasRole("USER")
                        // 5. B 端其他：商家
                        .requestMatchers("/api/b/**").hasRole("BUSINESS")
                        // 6. 当前用户资料：所有已登录角色都可以访问自己的
                        .requestMatchers("/api/users/me", "/api/users/me/**").authenticated()
                        // 7. C 端其余接口：买家
                        .requestMatchers("/api/**").hasRole("USER")
                        // 8. 兜底：其他路径也要登录
                        .anyRequest().authenticated()
                )
                .exceptionHandling(eh -> eh
                        .authenticationEntryPoint(authenticationEntryPoint) // 401
                        .accessDeniedHandler(accessDeniedHandler)           // 403
                )
                // JWT 过滤器放在用户名密码过滤器之前
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}