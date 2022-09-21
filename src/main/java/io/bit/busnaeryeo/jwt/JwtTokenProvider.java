package io.bit.busnaeryeo.jwt;

import io.bit.busnaeryeo.repository.UserRepository;

import io.bit.busnaeryeo.service.RedisServiceImpl;
import io.bit.busnaeryeo.service.UserDetailServiceImpl;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

    // 키
    @Value("${jwt.secretkey}")
    private String secretKey; //final로 통한 의존성 주입이 가능한가???? 의문점

    // 어세스 토큰 유효시간 | 30m
    private long accessTokenValidTime = 30 * 60 * 1000L; // 30 * 60 * 1000L;
    // 리프레시 토큰 유효시간 | 24h
    private long refreshTokenValidTime = 24 * 60 * 60 * 1000L;
    private final UserDetailServiceImpl userDetailService;
    private final RedisServiceImpl redisService;
    private final UserRepository userRepository;

    // 의존성 주입 후, 초기화를 수행
    // 객체 초기화, secretKey를 Base64로 인코딩한다.
    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    // Access Token 생성.
    public String createAccessToken(String username, List<String> roles){
        return this.createToken(username, roles, accessTokenValidTime);
    }
    // Refresh Token 생성.
    public String createRefreshToken(String username, List<String> roles) {
        return this.createToken(username, roles, refreshTokenValidTime);
    }

    // Create token
    public String createToken(String username, List<String> roles, long tokenValid) {
        Claims claims = Jwts.claims().setSubject(username); // claims 생성 및 payload 설정
        claims.put("roles", roles); // 권한 설정, key/ value 쌍으로 저장

        Date date = new Date();
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setClaims(claims) // 발행 유저 정보 저장
                .setIssuedAt(date) // 발행 시간 저장
                .setExpiration(new Date(date.getTime() + tokenValid)) // 토큰 유효 시간 저장
                .signWith(SignatureAlgorithm.HS256, secretKey) // 해싱 알고리즘 및 키 설정
                .compact(); // 생성
    }

    // JWT 에서 인증 정보 조회
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailService.loadUserByUsername(this.getUsername(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 토큰에서 회원 정보 추출
    public String getUsername(String token) {

        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    public Long getExpireTime(String token) {
        Date expriation = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getExpiration();
        Long now = new Date().getTime();
        return (expriation.getTime() -now);
    }


    // Request의 Header에서 AccessToken 값을 가져옵니다. "authorization" : "token'
    public String resolveAccessToken(HttpServletRequest request) {
        if(request.getHeader("Authorization") != null )
            return request.getHeader("Authorization").substring(7);
        return null;
    }
    // Request의 Header에서 RefreshToken 값을 가져옵니다. "RefreshToken" : "token'
    public String resolveRefreshToken(HttpServletRequest request) {
        if(request.getHeader("refreshToken") != null )
            return request.getHeader("refreshToken").substring(7);
        return null;
    }

    // 토큰의 유효성 + 만료일자 확인
    public boolean validateToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());

        } catch (ExpiredJwtException | SecurityException e) {
            log.error("Expried JWT Token : ",e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Malformed JWT Token : ", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT Token", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims String is empty.  ", e.getMessage());
        }
            return false;
        }


    // 어세스 토큰 헤더 설정
    public void setHeaderAccessToken(HttpServletResponse response, String accessToken) {
        response.setHeader("Authorization", "Bearer "+ accessToken);
    }

//     리프레시 토큰 헤더 설정  ,,,추가로 삭제 예정
    public void setHeaderRefreshToken(HttpServletResponse response, String refreshToken) {
        response.setHeader("refreshToken", "Bearer "+ refreshToken);
    }

    // RefreshToken 존재유무 확인
    public boolean existsRefreshToken(String username) {
        return redisService.getValues(username) != null;
        //// return tokenRepository.existsByRefreshToken(refreshToken);
    }

    // username으로 권한 정보 가져오기
    public List<String> getRoles(String username) {
        return userRepository.findByUsername(username).get().getRoles();
    }
}
