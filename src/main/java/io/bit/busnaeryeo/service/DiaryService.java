package io.bit.busnaeryeo.service;


import io.bit.busnaeryeo.domain.dto.DiaryDTO;
import io.bit.busnaeryeo.domain.dto.DiaryModifyDTO;
import io.bit.busnaeryeo.domain.dto.FlaskResponseDTO;
import io.bit.busnaeryeo.domain.dto.ShowDiaryDTO;
import io.bit.busnaeryeo.domain.entity.Diary;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

public interface DiaryService {
    public List<Diary> findAllDiariesByUserId(Long id);
    public Diary findDiaryById(Long id);
    public ShowDiaryDTO findDiaryByIdAndDate(HttpServletRequest request, String day, String month);
    public Double caculateSentimental(Mono<ResponseEntity<FlaskResponseDTO>> response);
    public String saveDiary(DiaryDTO diaryDTO, HttpServletRequest request, MultipartFile file, String dirName) throws IOException;
    public String modifyDiary(Long id, DiaryModifyDTO diaryModifyDTO, HttpServletRequest request, MultipartFile file, String dirName) throws IOException;
    public boolean checkAuth(HttpServletRequest request, DiaryDTO diaryDTO);
    public boolean checkAuthById(HttpServletRequest request, Long id);
    public ResponseEntity<?> deleteDiaryById(Long id);
    public List<ShowDiaryDTO> showDiary(HttpServletRequest request);
    public Long checkUser(HttpServletRequest request);
    public List<Diary> showCurrentDiary(HttpServletRequest request);
    public Double cacculateUserAvgSentimental(HttpServletRequest request);
    public Double calculateUserCurrentAvgSentimental(HttpServletRequest request);
}
