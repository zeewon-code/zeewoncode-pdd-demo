package com.zeewoncode.pdd_server.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 加密配置
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 密码加密器
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //安全过滤链：关闭默认登录页面，放行注册登录等接口，使用JWT无状态
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                //关闭csrf（前后端分离JWT项目关闭）
                .csrf(csrf -> csrf.disable())
                // 无状态，不用session
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        //放行登录、注册，不需要认证
//                        .requestMatchers("/api/auth/login","/api/auth/register").permitAll()
                        //其他接口后续由我们自己JWT拦截器控制，这里全部放行
                        .anyRequest().permitAll()
                );
        return http.build();
    }
}
