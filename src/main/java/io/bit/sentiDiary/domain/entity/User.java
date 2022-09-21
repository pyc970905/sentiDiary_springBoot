package io.bit.sentiDiary.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.bit.sentiDiary.domain.dto.UserDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(example = "회원 고유 식별번호")
    private Long id;
    @ApiModelProperty(example = "회원 이메일")
    @Column(nullable = false, unique = true)
    private String username;
    @ApiModelProperty(example = "회원 비밀번호")
    @Column(nullable = false)
    private String password;
    @ApiModelProperty(example = "회원 실명")
    @Column
    private String realName;
    @ApiModelProperty(example = "회원 성별")
    @Column
    private String gender;
    @ApiModelProperty(example = "회원 주소")
    @Column
    private String address;
    @ApiModelProperty(example = "회원 생년월일")
    @Column
    private String birthDay;

    @ApiModelProperty(example = "회원 감정수치")
    @Column
    private Double sentimental;
    @Builder.Default
    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Diary> diaries = new ArrayList<>();
    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<String> roles = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    public UserDTO toDTO() {
        UserDTO userDTO = UserDTO.builder()
                .id(id)
                .username(username)
                .password(password)
                .realName(realName)
                .gender(gender)
                .address(address)
                .birthDay(birthDay)
                .build();
        return userDTO;
    }
}
