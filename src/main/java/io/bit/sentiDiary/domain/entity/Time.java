package io.bit.sentiDiary.domain.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@MappedSuperclass
@SuperBuilder
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public abstract class Time {
    @ApiModelProperty(example = "생성일자")
    @Column(name = "created_date", nullable = false, updatable = false)
    @CreatedDate
    private String createdDate;
    @ApiModelProperty(example = "수정일자")
    @Column(name = "modified_date")
    @LastModifiedDate
    private String modifiedDate;

    @PrePersist
    public void onPrePersist(){
    this.createdDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
    this.modifiedDate = this.createdDate;
}

    @PreUpdate
    public void onPreUpdate(){
    this.modifiedDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
}


}
