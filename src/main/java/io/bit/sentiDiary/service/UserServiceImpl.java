package io.bit.sentiDiary.service;

import io.bit.sentiDiary.domain.dto.LoginDTO;
import io.bit.sentiDiary.domain.dto.SignUpDTO;
import io.bit.sentiDiary.domain.entity.User;
import io.bit.sentiDiary.jwt.JwtTokenProvider;
import io.bit.sentiDiary.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Service
@RequiredArgsConstructor
@Log4j2
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisServiceImpl redisService;


    //일반 회원용 회원가입
    @Override
    public Long join(SignUpDTO signUpDTO) {
        signUpDTO.setPassword(passwordEncoder.encode(signUpDTO.getPassword()));
        Long userId = userRepository.save(signUpDTO.toEntity()).getId();

        return userId;

    }
    @Override
    public Long joinAdmin(SignUpDTO signUpDTO) {
        signUpDTO.setPassword(passwordEncoder.encode(signUpDTO.getPassword()));
        Long userId = userRepository.save(signUpDTO.toAdminEntity()).getId();

        return userId;

    }

    @Override
    public User findUser(LoginDTO loginDTO) {
        User user = userRepository.findByUsername(loginDTO.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("없는 아이디 입니다."));
        return user;
    }
    @Override
    public boolean checkPassword(User user, LoginDTO loginDTO) {

        boolean result = passwordEncoder.matches(loginDTO.getPassword(),user.getPassword());
        if(!result)
            throw new IllegalArgumentException("비밀번호가 잘못되었습니다.");
        return true;
        //return passwordEncoder.matches(user.getPassword(), member.getPassword());
    }

    @Transactional
    @Override
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String accToken = jwtTokenProvider.resolveAccessToken(request);
        System.out.println(accToken);
        // AccessToken 검증
        if(!jwtTokenProvider.validateToken(accToken)) {
            return  ResponseEntity.badRequest().body("잘못된 요청입니다.");
        }
        // AccessToken에서 이메일 가져오기.
        Authentication authentication = jwtTokenProvider.getAuthentication(accToken);

        //Redis에서 해당 username으로 저장된 refresh토큰이 있으면 삭제하고 액세스토큰 블랙리스트로 생성
        if(jwtTokenProvider.existsRefreshToken(authentication.getName())) {
            String key = authentication.getName() + "isLogout";
            System.out.println(key);
            redisService.delValues(authentication.getName());
            Long expiration = jwtTokenProvider.getExpireTime(accToken);
            System.out.println(expiration);
            System.out.println(accToken);
            redisService.setValuesWithExp(key, accToken, expiration);

        }
        return ResponseEntity.ok().body("Logout Complete.");

    }
    @Transactional
    @Override
    public ResponseEntity<?> login(LoginDTO loginDTO, HttpServletResponse response) {
        // 유저 존재 확인
        User user = findUser(loginDTO);
        // 비밀번호 체크
        checkPassword(user, loginDTO);
        // 어세스, 리프레시 토큰 발급 및 헤더 설정
        String accessToken = jwtTokenProvider.createAccessToken(user.getUsername(), user.getRoles());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getUsername(), user.getRoles());
        jwtTokenProvider.setHeaderAccessToken(response, accessToken);
        jwtTokenProvider.setHeaderRefreshToken(response, refreshToken);

        // Redis 인메모리에 리프레시 토큰 저장
        redisService.setValues(user.getUsername(), refreshToken);
        // 리프레시 토큰 저장소에 저장
        ////tokenRepository.save(new RefreshToken(refreshToken));
        return ResponseEntity.ok().body("Login!");

    }
}
