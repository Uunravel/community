package com.nowcoder.community.config;

import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

import java.io.IOException;
import java.io.PrintWriter;

@Configuration
@EnableWebSecurity
public class SecurityConfig implements CommunityConstant {

    // 授权
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
//        httpSecurity.securityMatcher("/user/setting", "/user/upload", "/discuss/add", "/comment/add/**"
//                        , "/letter/**", "/notice/**", "/like", "/follow", "/unfollow");
        httpSecurity.authorizeHttpRequests(
                (auth) -> auth
                        .requestMatchers("/user/setting", "/user/upload", "/discuss/add", "/comment/add/**"
                        , "/letter/**", "/notice/**", "/like", "/follow", "/unfollow")
                        .hasAnyAuthority(
                                AUTHORITY_USER,
                                AUTHORITY_ADMIN,
                                AUTHORITY_MODERATOR)
                        .requestMatchers("/discuss/top", "/discuss/wonderful")
                        .hasAnyAuthority(AUTHORITY_MODERATOR)
                        .requestMatchers("/discuss/delete", "/data/**", "/actuator/**")
                        .hasAnyAuthority(AUTHORITY_ADMIN)
                        .anyRequest().permitAll())
                ;
        httpSecurity.csrf(AbstractHttpConfigurer::disable);
        // 权限不够时的处理
        httpSecurity.exceptionHandling(
                (exception) -> exception
                        .authenticationEntryPoint(new AuthenticationEntryPoint() {
                            // 没有登录时的处理
                            @Override
                            public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
                                String xRequestedWith = request.getHeader("x-requested-with");
                                if("XMLHttpRequest".equals(xRequestedWith)) {  // 异步请求
                                    response.setContentType("application/plain;charset=utf-8");
                                    PrintWriter writer = response.getWriter();
                                    writer.write(CommunityUtil.getJSONString(403, "您还没有登录!"));
                                } else {
                                    response.sendRedirect(request.getContextPath() + "/login");
                                }
                            }
                        })
                        .accessDeniedHandler(new AccessDeniedHandler() {
                            // 权限不足时的处理
                            @Override
                            public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
                                String xRequestedWith = request.getHeader("x-requested-with");
                                if("XMLHttpRequest".equals(xRequestedWith)) {  // 异步请求, 返回JSON
                                    response.setContentType("application/plain;charset=utf-8");
                                    PrintWriter writer = response.getWriter();
                                    writer.write(CommunityUtil.getJSONString(403, "您没有访问此功能的权限!"));
                                } else {
                                    response.sendRedirect(request.getContextPath() + "/denied");
                                }
                            }
                        })
        );

        // Security底层默认拦截logout, 执行自己退出代码需覆盖它
        httpSecurity.logout((logout) -> logout.logoutUrl("/securitylogout"));

        return httpSecurity.build();
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }
}
