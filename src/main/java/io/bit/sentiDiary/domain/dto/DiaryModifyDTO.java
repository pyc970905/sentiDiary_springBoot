package io.bit.sentiDiary.domain.dto;

import io.bit.sentiDiary.domain.entity.Diary;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiaryModifyDTO {

    @ApiModelProperty(example = "게시글 고유 식별번호")
    private Long id;

    @ApiModelProperty(example = "내용")
    private String content;

    @ApiModelProperty(example = "감정 수치")
    private Double sentimental;
    public Diary toModifiedEntity() {
        Diary diary = Diary.builder()
                .id(id)
                .content(content)
                .sentimental(sentimental)
                .build();
        return diary;
    }
}
