package io.bit.sentiDiary.domain.dto;

import io.bit.sentiDiary.domain.entity.User;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.ElementCollection;
import javax.persistence.FetchType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@Builder
public class UserDTO {

    @ApiModelProperty(example = "회원 고유 식별 번호")
    private Long id;
    @ApiModelProperty(example = "회원 이메일")
    private String username;
    @ApiModelProperty(example = "회원 비밀번호")
    private String password;
    @ApiModelProperty(example = "회원 실명")
    private String realName;
    @ApiModelProperty(example = "회원 성별")
    private String gender;
    @ApiModelProperty(example = "회원 주소")
    private String address;
    @ApiModelProperty(example = "회원 생년월일")
    private String birthDay;

    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<String> roles = new ArrayList<>();



    public User toEntity() {
        User user = User.builder()
                .id(id)
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

    public User toDriverEntity() {
        User user = User.builder()
                .id(id)
                .username(username)
                .password(password)
                .realName(realName)
                .gender(gender)
                .address(address)
                .birthDay(birthDay)
                .roles(Collections.singletonList("ROLE_DRIVER"))
                .build();
        return user;
    }
    public User toAdminEntity() {
        User user = User.builder()
                .id(id)
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