package io.bit.sentiDiary.service;

import io.bit.sentiDiary.domain.dto.LoginDTO;
import io.bit.sentiDiary.domain.dto.SignUpDTO;
import io.bit.sentiDiary.domain.entity.User;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface UserService {
    public Long join(SignUpDTO signUpDTO);
    public Long joinAdmin(SignUpDTO signUpDTO);
    public User findUser(LoginDTO loginDTO);
    public boolean checkPassword(User user, LoginDTO loginDTO);
    public ResponseEntity<?> logout(HttpServletRequest request);
    public ResponseEntity<?> login(LoginDTO loginDTO, HttpServletResponse response);
}
