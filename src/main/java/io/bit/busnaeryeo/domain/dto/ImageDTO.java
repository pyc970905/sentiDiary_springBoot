package io.bit.busnaeryeo.domain.dto;

import io.bit.busnaeryeo.domain.entity.Image;
import io.bit.busnaeryeo.domain.entity.Diary;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ImageDTO {
    @ApiModelProperty(example = "사진 고유 식별번호")
    private Long id;
    @ApiModelProperty(example = "사진 URL")
    private String imgUrl;
    @ApiModelProperty(example = "사진이 첨부된 일기 entity")
    private Diary diary;


    public Image toEntity(){
        Image image = Image.builder()
                .id(id)
                .imgUrl(imgUrl)
                .diary(diary)
                .build();
        return image;
    }
}
