package io.bit.busnaeryeo.config;

import io.bit.busnaeryeo.jwt.JwtAccessDeniedHandler;
import io.bit.busnaeryeo.jwt.JwtAuthenticationEntryPoint;
import io.bit.busnaeryeo.jwt.JwtAuthenticationFilter;
import io.bit.busnaeryeo.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final RedisTemplate redisTemplate;


    // AuthenticationManager를 Bean으로 등록합니다.

    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }



    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors()

                .and()
                .csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint) ///엔트리포인트 공부
                .accessDeniedHandler(jwtAccessDeniedHandler)

                .and()
                .formLogin().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 토큰 기반 인증이므로 세션은 꺼두기

                .and()
                .authorizeRequests()
                .antMatchers("/","/login","/join","/join/admin","/signout","/v2/api-docs").permitAll()
//                .antMatchers("/diary/**").hasRole("ADMIN") 관리자 계정은 사용하지 않기 때문에 꺼두기
                .antMatchers("/diary/**").hasRole("USER")
                .anyRequest().authenticated()

                .and()
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, redisTemplate), UsernamePasswordAuthenticationFilter.class);
        // JwtAuthenticationFilter를 UsernamePasswordAuthenticationFilter 전에 넣는다
    }

}