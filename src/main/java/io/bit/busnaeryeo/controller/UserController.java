package io.bit.busnaeryeo.controller;

import io.bit.busnaeryeo.domain.dto.LoginDTO;
import io.bit.busnaeryeo.domain.dto.SignUpDTO;
import io.bit.busnaeryeo.domain.entity.User;
import io.bit.busnaeryeo.domain.dto.UserDTO;
import io.bit.busnaeryeo.jwt.JwtTokenProvider;
import io.bit.busnaeryeo.service.RedisServiceImpl;
import io.bit.busnaeryeo.service.UserServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class UserController {

    private final UserServiceImpl userService;

    @ApiOperation(value = "로그인", notes = "로그인 성공시에 response헤더에 AccessToken과 RefreshToken을 추가해서 200코드를 반환한다.")
    @PostMapping(value = "/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginDTO loginDTO, HttpServletResponse response) {

        return userService.login(loginDTO, response);

    }

    @ApiOperation(value = "일반회원 회원가입", notes = "SignUpDTO를 Request Body로 받아서 Role을 User로 가입하게 만들어준다.")
    @ApiImplicitParam(name = "signUpDTO", value = "회원가입 정보가 실린 DTO")
    @PostMapping(value = "/join")
    public ResponseEntity<?> join(@RequestBody @Valid SignUpDTO signUpDTO) {
        Long result = userService.join(signUpDTO);

        return result != null ?
                ResponseEntity.ok().body("Signup Complete!, Please Enjoy Our Homepage!") :
                ResponseEntity.badRequest().build();
    }
    // 사용되지 않는 메소드 : 사용자 위주로 진행된 프로젝트 방향성으로 사용하지 못함.
    @ApiOperation(value = "관리자 회원가입", notes = "SignUpDTO를 Request Body로 받아서 Role을 Admin으로 가입하게 만들어준다.")
    @ApiImplicitParam(name = "signUpDTO", value = "회원가입 정보가 실린 DTO")
    @PostMapping(value = "/join/admin")
    public ResponseEntity<?> joinAdmin(@RequestBody @Valid SignUpDTO signUpDTO) {
        Long result = userService.joinAdmin(signUpDTO);

        return result != null ?
                ResponseEntity.ok().body("Signup Complete!, Please Enjoy Our Homepage!") :
                ResponseEntity.badRequest().build();
    }

    @ApiOperation(value = "로그아웃", notes = "로그아웃시 백에서는 Redis에 존재하는 해당 유저 Refresh Token을 삭제함과 동시에 " +
            "해당유저의 Access Token을 Redis에 블랙리스트로 등록해서 접근을 제한한다.")
    @DeleteMapping("/signout") //react server에서 logout mapping을 인식을 하지 못해서 mapping은 signout으로 변경
    public ResponseEntity<?> logout(HttpServletRequest request) {

        return userService.logout(request);
    }

}