package io.bit.sentiDiary.domain.dto;

import io.bit.sentiDiary.domain.entity.User;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

import javax.validation.constraints.Pattern;
import java.util.Collections;

@Getter
@Setter
public class SignUpDTO {
    @ApiModelProperty(example = "회원가입할 사용자의 이메일")
    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,6}$", message = "이메일 형식에 맞지 않습니다.")
    private String username;
    @ApiModelProperty(example = "회원가입할 사용자의 비밀번호")
    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]{8,16}$", message = "비밀번호는 8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
    private String password;
    @ApiModelProperty(example = "회원가입할 사용자의 실명")
    @NotBlank(message = "필수입력 값입니다.")
    private String realName;
    @ApiModelProperty(example = "회원가입할 사용자의 성별")
    @NotBlank(message = "필수입력 값입니다.")
    private String gender;
    @ApiModelProperty(example = "회원가입할 사용자의 주소")
    @NotBlank(message = "필수입력 값입니다.")
    private String address;
    @ApiModelProperty(example = "회원가입할 사용자의 생년월일")
    @NotBlank(message = "필수입력 값입니다.")
    private String birthDay;


    public User toEntity(){
        User user = User.builder()
                .username(username)
                .password(password)
                .realName(realName)
                .gender(gender)
                .address(address)
                .birthDay(birthDay)
                .roles(Collections.singletonList("ROLE_USER"))
                .build();
        return user;
    }


    public User toAdminEntity() {
        User user = User.builder()
                .username(username)
                .password(password)
                .realName(realName)
                .gender(gender)
                .address(address)
                .birthDay(birthDay)
                .roles(Collections.singletonList("ROLE_ADMIN"))
                .build();
        return user;
    }
}


