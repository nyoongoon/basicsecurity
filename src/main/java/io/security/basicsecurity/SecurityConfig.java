package io.security.basicsecurity;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Configuration
@EnableWebSecurity // WebSecurityConfiguration 등의 클래스 추가
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception { //템플릿-콜백..?
        http // 인가
                .authorizeRequests() // 요청에 대한 보안 검사 시작됨
                .anyRequest().authenticated(); // 모든 요청에 대해 인증 받기
        http // 인증
                .formLogin() //폼 로그린
//                .loginPage("/loginPage")
                .defaultSuccessUrl("/")
                .failureUrl("/login")
                .usernameParameter("userId") //프론트와 맞춰야함
                .passwordParameter("passwd") //프론트와 맞춰야함
                .loginProcessingUrl("/login_proc") //프론트와 맞춰야함
                // AuthenticationSuccessHandler 구현체 콜백
                .successHandler(new AuthenticationSuccessHandler() {
                    @Override
                    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                        System.out.println("authentication " + authentication.getName());
                        response.sendRedirect("/");
                    }
                })
                // AuthenticationFailureHandler 구현체 콜백
                .failureHandler(new AuthenticationFailureHandler() {
                    @Override
                    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
                        System.out.println("exception " + exception.getMessage());
                        response.sendRedirect("/login");
                    }
                })
                .permitAll(); // "/loginPage"에 접근하는 사용자는 모두 허용
        http.logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .deleteCookies("JSESSIONID", "remember-me")
                .addLogoutHandler(new LogoutHandler() { // 세션 무효화 예시
                    @Override
                    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
                        HttpSession session = request.getSession();
                        session.invalidate();
                    }
                })
                .logoutSuccessHandler(new LogoutSuccessHandler() { //리다이렉트예시
                    @Override
                    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                        response.sendRedirect("/login");
                    }
                })
                .deleteCookies("remember-me");

        http.rememberMe() // remember-me 기능 활성화
                .rememberMeParameter("remember") //기본파라미터명은 remember-me
                .tokenValiditySeconds(3600) // Default는 14일
                .alwaysRemember(true) // 리멤버미 기능이 활성화되있지 않아도 항상 실행
                .userDetailsService(userDetailsService());
    }
}