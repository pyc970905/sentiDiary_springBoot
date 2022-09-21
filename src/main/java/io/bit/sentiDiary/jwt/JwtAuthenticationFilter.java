package io.bit.sentiDiary.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Log4j2
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate redisTemplate;

    @Value("${jwt.secretkey}")
    private String secretKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 헤더에서 jwt토큰 받아오기
        String accessToken = jwtTokenProvider.resolveAccessToken(request);
        String refreshToken = jwtTokenProvider.resolveRefreshToken(request);

        // 유효한 토큰이 있는지 확인하고 레디스 서버에서 해당 토큰안에 있는 이메일이 로그아웃해서 생긴 토큰의 밸류값을 불러옴
        if (accessToken != null) {
            String aa = jwtTokenProvider.getUsername(accessToken);
            String name = aa + "isLogout";
            String isLogout = (String) redisTemplate.opsForValue().get(name);
            // 어세스 토큰이 유효하고 해당 이메일의 로그아웃한 토큰이 같지 않다면
            if (jwtTokenProvider.validateToken(accessToken) && (accessToken != isLogout)) {
                this.setAuthentication(accessToken);

            }
            //어세스토큰이 유효한데 로그아웃한 토큰과 값이 같다면?? 토큰 탈취의 위험으로 어세스토큰을 헤더에서 지워서 로그인 못하게만든다.
            else if (jwtTokenProvider.validateToken(accessToken) && (accessToken == isLogout)) {
                /// 수정필요함 뚫림 ㄷㄷ
                SecurityContextHolder.getContext().setAuthentication(null);

            }

            // 어세스 토큰이 만료됐는데 리프레쉬 토큰이 있는 경우
            else if (!jwtTokenProvider.validateToken(accessToken) && refreshToken != null) {
                // 재발급 후, 컨텍스트에 다시 넣기

                /// 리프레시 토큰 검증
                boolean validateRefreshToken = jwtTokenProvider.validateToken(refreshToken);

                /// 리프레시 토큰 저장소 존재유무 확인
                boolean isRefreshToken = jwtTokenProvider.existsRefreshToken(jwtTokenProvider.getUsername(refreshToken));

                if (validateRefreshToken && isRefreshToken) {
                    /// 리프레시 토큰으로 이메일 정보 가져오기
                    String username = jwtTokenProvider.getUsername(refreshToken);
                    /// 이메일로 권한정보 받아오기
                    List<String> roles = jwtTokenProvider.getRoles(username);
                    /// 토큰 발급
                    String newAccessToken = jwtTokenProvider.createAccessToken(username, roles);
                    /// 헤더에 어세스 토큰 추가
                    jwtTokenProvider.setHeaderAccessToken(response, newAccessToken);
                    /// 컨텍스트에 넣기
                    this.setAuthentication(newAccessToken);}
                else{
                    SecurityContextHolder.getContext().setAuthentication(null);

                }filterChain.doFilter(request, response);
            }
        }
        filterChain.doFilter(request, response);
    }

    // SecurityContext 에 Authentication 객체를 저장합니다.
    public void setAuthentication(String token) {
        // 토큰으로부터 유저 정보를 받아옵니다.
        Authentication authentication = jwtTokenProvider.getAuthentication(token);
        // SecurityContext 에 Authentication 객체를 저장합니다.
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
