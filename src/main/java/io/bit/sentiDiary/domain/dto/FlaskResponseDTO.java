package io.bit.sentiDiary.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FlaskResponseDTO {
    @ApiModelProperty(example = "flask 서버에서 돌아오는 반환값 == 문달별로 추출된 감정 라벨들")
    private List<String> sentimental;
}
