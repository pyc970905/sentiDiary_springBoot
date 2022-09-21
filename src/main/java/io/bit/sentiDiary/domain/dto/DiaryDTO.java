package io.bit.sentiDiary.domain.dto;

import io.bit.sentiDiary.domain.entity.Diary;
import io.bit.sentiDiary.domain.entity.User;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DiaryDTO {
    @ApiModelProperty(example = "게시글 고유 식별번호")
    private Long id;

    @ApiModelProperty(example = "게시글 생성일자 및 제목")
    private String create_date;
    @ApiModelProperty(example = "내용")
    private String content;
    @ApiModelProperty(example = "Access Token에서 가져온 이메일을 가진사람의 이름")
    private String writer;
    @ApiModelProperty(example = "작성시 헤더에 제출한 토큰 안에 있는 이메일의 주인")
    private User user;
    @ApiModelProperty(example = "게시글의 감정분석 결과")
    private Double sentimental;


    public Diary toEntity() {

         Diary diary = Diary.builder()
                 .id(id)
                 .content(content)
                 .writer(writer)
                 .user(user)
                 .sentimental(sentimental)
                 .build();

        return diary;
    }
}
