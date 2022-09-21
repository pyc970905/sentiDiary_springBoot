package io.bit.busnaeryeo.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.bit.busnaeryeo.domain.dto.DiaryDTO;
import io.bit.busnaeryeo.domain.dto.DiaryModifyDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@SuperBuilder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Diary extends Time{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(example = "게시글 고유 식별번호")
    private Long id;

    @ApiModelProperty(example = "게시글 작성자")
    @Column
    private  String writer;
    @ApiModelProperty(example = "게시글 내용")
    @Column
    private String content;

    @ApiModelProperty(example = "감정")
    @Column
    private Double sentimental;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder.Default
    @JsonIgnore
    @OneToMany(mappedBy = "diary", cascade = CascadeType.ALL)
    private List<Image> images = new ArrayList<>();


    public DiaryDTO toDTO() {

        DiaryDTO diaryDTO = DiaryDTO.builder()
                .id(id)
                .create_date(getCreatedDate())
                .content(content)
                .writer(writer)
                .user(user)
                .sentimental(sentimental)
                .build();

        return diaryDTO;
    }
    public DiaryModifyDTO toModifyDTO() {
        DiaryModifyDTO diaryModifyDTO = DiaryModifyDTO.builder()
                .id(id)
                .content(content).build();
        return diaryModifyDTO;
    }
}