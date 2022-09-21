package io.bit.sentiDiary.controller;


import io.bit.sentiDiary.domain.dto.DiaryDTO;
import io.bit.sentiDiary.domain.dto.DiaryModifyDTO;
import io.bit.sentiDiary.domain.dto.ShowDiaryDTO;
import io.bit.sentiDiary.service.DiaryServiceImpl;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

import lombok.extern.log4j.Log4j2;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;



@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/diary")
@Log4j2
public class DiaryController {
    private final DiaryServiceImpl diaryService;
    @Value("${dirName}")
    private String dirName;

    @ApiOperation(value = "일기 작성", notes = "DTO를 매개변수로 받아서 글을 DB에 작성한다.")
    @PostMapping(value = "/regist",produces = MediaType.APPLICATION_JSON_VALUE, consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    @ApiImplicitParams({
            @ApiImplicitParam(name= "diaryDTO", value = "작성한 일기"),
            @ApiImplicitParam(name= "file", value = "일기에 들어가는 사진")})
    public ResponseEntity<?> postDiary(HttpServletRequest request, @RequestPart(value= "diaryDTO") DiaryDTO diaryDTO, @RequestPart(value= "file",required = false) MultipartFile file) throws IOException {

        return new ResponseEntity<>(diaryService.saveDiary(diaryDTO, request, file,dirName), HttpStatus.CREATED);
    }
    @ApiOperation(value = "일기 전체 조회" ,notes = "일기 전체 조회, Header에 실린 토큰의 주인인 유저가 작성한 일기를 다 불러온다.")
    @GetMapping(value = "/show")
    public ResponseEntity<?> getAllDiaries(HttpServletRequest request) {

        List<ShowDiaryDTO> showDiaryDTOList = diaryService.showDiary(request);

        return  ResponseEntity.ok().body(showDiaryDTOList);
    }
    @GetMapping(value = "/show/{month}/{day}")
    @ApiOperation(value = "특정 날짜 일기 조회", notes = "해당 날짜의 글을 보여준다.")
    @ApiImplicitParams({
    @ApiImplicitParam(name= "month", value = "요청한 사용자의 일기 작성 월"),
    @ApiImplicitParam(name= "day", value = "요청한 사용자의 일기 작성 일")})
    public ResponseEntity<?> getDiary(HttpServletRequest request,@PathVariable("day") String day,@PathVariable("month") String month) {
        //게시판 공지 수정 본인확인 메소드 추가
        ShowDiaryDTO showDiaryDTO = diaryService.findDiaryByIdAndDate(request,day,month);

        return ResponseEntity.ok().body(showDiaryDTO);
    }
    //공지 수정
    @PatchMapping(value = "/modify/{id}",produces = MediaType.APPLICATION_JSON_VALUE,  consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    @ApiOperation(value = "일기 수정", notes = "새롭게 입력한 DiaryDTO를 매개변수로 받아서 해당 id를 가지고 있는 일기에 덮어 씌운다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name= "id", value = "수정하고싶은 게시판 글 고유 식별 번호"),
            @ApiImplicitParam(name= "diaryModifyDTO", value = "수정할 일기"),
            @ApiImplicitParam(name= "file", value = "수정할 일기에 들어가는 사진")})
    public ResponseEntity<?> modifyDiary(HttpServletRequest request, @PathVariable("id") Long id, @RequestPart DiaryModifyDTO diaryModifyDTO, @RequestPart(value= "file",required = false) MultipartFile file) throws IOException {
        //게시판 공지 수정 본인확인 메소드 추가
        DiaryDTO modifyNotice = diaryService.findDiaryById(id).toDTO();

        return diaryService.checkAuth(request, modifyNotice) ?
                ResponseEntity.ok().body(diaryService.modifyDiary(id,diaryModifyDTO,request,file,dirName)) :
                ResponseEntity.badRequest().body("Not your Article, Please Check This Article's Writer");
    }
    //swagger
    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "일기 삭제", notes = "해당 id를 가진 Diary를 DB에서 삭제한다.")
    @ApiImplicitParam(name= "id", value = "삭제하고싶은 일기 고유 식별 번호")
    public ResponseEntity<?> deleteDiary(@PathVariable("id") Long id,HttpServletRequest request) {

        return diaryService.checkAuthById(request, id) ?
                diaryService.deleteDiaryById(id) :
                ResponseEntity.badRequest().body("Not your Article, Please Check This Article's Writer");
    }

//    @GetMapping("/test1")//유저의 최근 10개 감정 반환, 프로젝트에 사용되지 않음
//    public ResponseEntity<?> test1(HttpServletRequest request){
//
//        return ResponseEntity.ok().body(diaryService.calculateUserCurrentAvgSentimental(request));
//    }
//
//    @GetMapping("/test2")//유저의 전체글 감정 반환, 프로젝트에 사용되지 않음
//    public ResponseEntity<?> test2(HttpServletRequest request){
//
//        return ResponseEntity.ok().body(diaryService.cacculateUserAvgSentimental(request));
//
//    }

    @GetMapping("/show7diaries")//유저의 7개 글
    public ResponseEntity<?> test3(HttpServletRequest request){

        return ResponseEntity.ok().body(diaryService.showCurrentDiary(request));
    }



}
