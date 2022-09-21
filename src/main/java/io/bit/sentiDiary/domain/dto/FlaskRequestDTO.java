package io.bit.sentiDiary.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlaskRequestDTO {
    @ApiModelProperty(example = "flask 서버로 전송되는 dto == 일기 내용")
    private String input;
}
