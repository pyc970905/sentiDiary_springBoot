package io.bit.sentiDiary.domain.entity;

import io.bit.sentiDiary.domain.dto.ImageDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Image {
    @ApiModelProperty(example = "사진 고유 식별번호")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ApiModelProperty(example = "사진 URL")
    @Column
    private String imgUrl;
    @ApiModelProperty(example = "사진이 첨부된 일기 entity")
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name= "diary_id")
    private Diary diary;

    public ImageDTO toDTO() {
        ImageDTO imageDTO = ImageDTO.builder()
                .id(id)
                .imgUrl(imgUrl)
                .diary(diary)
                .build();
        return imageDTO;
    }
}
