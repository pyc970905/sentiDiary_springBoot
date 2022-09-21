package io.bit.busnaeryeo.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShowDiaryDTO {
    @ApiModelProperty(example = "일기가 가지고 있는 내용들")
    private DiaryDTO diaryDTO;
    @ApiModelProperty(example = "일기에 첨부된 사진 url")
    private String imgUrl;
}
