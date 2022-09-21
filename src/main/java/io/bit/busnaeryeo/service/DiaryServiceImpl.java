package io.bit.busnaeryeo.service;


import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import com.amazonaws.services.s3.AmazonS3Client;
import io.bit.busnaeryeo.common.S3Uploader;
import io.bit.busnaeryeo.domain.dto.*;

import io.bit.busnaeryeo.domain.entity.Diary;
import io.bit.busnaeryeo.domain.entity.Image;
import io.bit.busnaeryeo.domain.entity.User;
import io.bit.busnaeryeo.jwt.JwtTokenProvider;
import io.bit.busnaeryeo.repository.ImageRepository;
import io.bit.busnaeryeo.repository.DiaryRepository;
import io.bit.busnaeryeo.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.multipart.MultipartFile;

import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;


import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

import static io.netty.util.internal.StringUtil.length;


@Service
@Transactional
@Log4j2
public class DiaryServiceImpl extends S3Uploader implements DiaryService {
    private DiaryRepository diaryRepository;
    private JwtTokenProvider jwtTokenProvider;
    private UserRepository userRepository;
    private ImageRepository imageRepository;

    public DiaryServiceImpl(AmazonS3Client amazonS3Client, DiaryRepository diaryRepository, JwtTokenProvider jwtTokenProvider
    , UserRepository userRepository, ImageRepository imageRepository) {
        super(amazonS3Client);
        this.diaryRepository = diaryRepository;
        this.jwtTokenProvider =jwtTokenProvider;
        this.userRepository =userRepository;
        this.imageRepository =imageRepository;
    }


    @Value("${jwt.secretkey}")
    private String secretKey;

    @Value("${prepix}")
    private String prepix;

    HttpClient client = HttpClient.create()
            .responseTimeout(Duration.ofSeconds(20));

    WebClient webClient = WebClient.builder()
            .baseUrl("http://54.180.94.150:5000")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .clientConnector(new ReactorClientHttpConnector(client))
            .build();

    @Override
    public List<Diary> findAllDiariesByUserId(Long id) {
        return diaryRepository.findAllByUserId(id);
    }
    @Override
    public Diary findDiaryById(Long id) {

        Diary diary = diaryRepository.findById(id).orElse(new Diary());

        return diary;
    }
    @Override
    public ShowDiaryDTO findDiaryByIdAndDate(HttpServletRequest request, String day, String month) {

        String username = jwtTokenProvider.getUsername(jwtTokenProvider.resolveAccessToken(request));

        User user = userRepository.findByUsername(username).get();
        Long userId = user.getId();
        String createDate = "2022." + month + "." + day;

        DiaryDTO result = diaryRepository.findByUserIdAndCreatedDate(userId, createDate).toDTO();

        ShowDiaryDTO showDiaryDTO = new ShowDiaryDTO();
        showDiaryDTO.setDiaryDTO(result);
        showDiaryDTO.setImgUrl(findDiaryById(result.getId()).getImages().get(0).getImgUrl());

        return showDiaryDTO;
    }
    @Override
    public Double caculateSentimental(Mono<ResponseEntity<FlaskResponseDTO>> response) {
        ResponseEntity<FlaskResponseDTO> flaskResult = response.share().block();
        List<String> flaskResult1 = flaskResult.getBody().getSentimental();


        List<String> result = new ArrayList();

        for(int i = 0; i <flaskResult1.size(); i++) {
            String label = flaskResult1.get(i);
            if (label.equals("슬픔")) {
                String a = "-1";
                result.add(a);
            } else if (label.equals("당황")) {
                String a = "-0.1";
                result.add(a);
            } else if (label.equals("분노")) {
                String a = "-0.7";
                result.add(a);
            } else if (label.equals("무기력")) {
                String a = "-0.3";
                result.add(a);
            } else if (label.equals("중립")) {
                String a = "0.1";
                result.add(a);
            } else if (label.equals("기쁨")) {
                String a = "1.0";
                result.add(a);
            } else if (label.equals("상처")) {
                String a = "-0.8";
                result.add(a);
            }
            log.info(result);
        }
        log.info(result);

        double resultNum = 0;

        for (int i = 0; i <result.size(); i++) {

            resultNum = resultNum + Double.parseDouble(result.get(i));
            log.info(resultNum);
        }

        double sentimental = resultNum / result.size();

        return  sentimental;
    }
    @Override
    public String saveDiary(DiaryDTO diaryDTO, HttpServletRequest request, MultipartFile file, String dirName) throws IOException {
        // jwt 토큰에서 username을 가져와서 작성자를 따로 입력하지 않아도 값이 들어가도록 함


        String username = jwtTokenProvider.getUsername(jwtTokenProvider.resolveAccessToken(request));

        User user = userRepository.findByUsername(username).get();

        String content = diaryDTO.getContent();
        FlaskRequestDTO flaskRequestDTO = new FlaskRequestDTO();
        flaskRequestDTO.setInput(content);

        Mono<ResponseEntity<FlaskResponseDTO>> response = webClient.post()
                .uri("/call")
                .bodyValue(flaskRequestDTO)
                .retrieve()
                .toEntity(FlaskResponseDTO.class);


        double sentimental = caculateSentimental(response);

        diaryDTO.setSentimental(sentimental);
        diaryDTO.setUser(user);
        diaryDTO.setWriter(user.getUsername());


        String uri = super.upload(file, dirName);

        ImageDTO imageDTO = ImageDTO.builder()
                .imgUrl(uri)
                .diary(diaryDTO.toEntity())
                .build();

        imageRepository.save(imageDTO.toEntity());


        String msg = "등록이 완료되었습니다";

        return msg;
    }
    @Override
    public String modifyDiary(Long id, DiaryModifyDTO diaryModifyDTO, HttpServletRequest request, MultipartFile file, String dirName) throws IOException {


        //기존파일 삭제
        Diary diary = diaryRepository.findById(id).get();
        DiaryDTO diaryDTO = diary.toDTO();
        Long imgId = diary.getImages().get(0).getId();
        imageRepository.deleteById(imgId);

        String fileName = diary.getImages().get(0).getImgUrl().substring(length(prepix));


        super.deleteS3(fileName);

        String content = diaryModifyDTO.getContent();
        diaryDTO.setContent(content);

        FlaskRequestDTO flaskRequestDTO = new FlaskRequestDTO();
        flaskRequestDTO.setInput(content);

        Mono<ResponseEntity<FlaskResponseDTO>> response = webClient.post()
                .uri("/call")
                .bodyValue(flaskRequestDTO)
                .retrieve()
                .toEntity(FlaskResponseDTO.class);


        double sentimental = caculateSentimental(response);

        diaryModifyDTO.setSentimental(sentimental);

        String uri = super.upload(file, dirName);

        ImageDTO image = ImageDTO.builder()
                .id(imgId)
                .imgUrl(uri)
                .diary(diaryDTO.toEntity()).build();


        imageRepository.save(image.toEntity());

        String msg = "등록이 완료되었습니다";

        return msg;
    }
    @Override
    public boolean checkAuth(HttpServletRequest request, DiaryDTO diaryDTO) {
        String accessToken= jwtTokenProvider.resolveAccessToken(request);
        Jws<Claims> claims = Jwts.parser().setSigningKey(Base64.getEncoder().encodeToString(secretKey.getBytes())).parseClaimsJws(accessToken);
        // 이줄을 고쳐야함 오류가 걸렸던 이유 signkey에 직접적으로 String을 값을 넣어줬는데 역파싱 하기 위해서는 secretkry를 byte로 파싱해준뒤 역파싱해야함.
        String username = claims.getBody().getSubject();

        return username.equals(diaryDTO.getWriter());
    }
    @Override
    public boolean checkAuthById(HttpServletRequest request, Long id) {
        String accessToken = jwtTokenProvider.resolveAccessToken(request);
        Jws<Claims> claims = Jwts.parser().setSigningKey(Base64.getEncoder().encodeToString(secretKey.getBytes())).parseClaimsJws(accessToken);
        // 이줄을 고쳐야함 오류가 걸렸던 이유 signkey에 직접적으로 String을 값을 넣어줬는데 역파싱 하기 위해서는 secretkry를 byte로 파싱해준뒤 역파싱해야함.
        String username = claims.getBody().getSubject();

        DiaryDTO diaryDTO = diaryRepository.findById(id).get().toDTO();

        return username.equals(diaryDTO.getWriter());
    }

    @Override
    public ResponseEntity<?> deleteDiaryById(Long id) {
        diaryRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
    @Override
    public Long checkUser(HttpServletRequest request) {
        String username = jwtTokenProvider.getUsername(jwtTokenProvider.resolveAccessToken(request));

        return userRepository.findByUsername(username).get().getId();
    }
    @Override
    public List<ShowDiaryDTO> showDiary(HttpServletRequest request) {

        Long id = checkUser(request);
        List<Diary> diaries = findAllDiariesByUserId(id);

        List<ShowDiaryDTO> showDiaryDTOList = new ArrayList<>();
        for (int i = 0; i < diaries.size() ; i ++) {
            ShowDiaryDTO a = new ShowDiaryDTO();
            a.setDiaryDTO(diaries.get(i).toDTO());
            a.setImgUrl(findDiaryById(diaries.get(i).getId()).getImages().get(0).getImgUrl());
            showDiaryDTOList.add(a);
        }

        return showDiaryDTOList;
    }
    @Override
    public List<Diary> showCurrentDiary(HttpServletRequest request) {

        Long id = checkUser(request);

        return diaryRepository.findCurrentTenDiariesByUserId(id);
    }

    @Override
    public Double cacculateUserAvgSentimental(HttpServletRequest request) {

        Long id = checkUser(request);

        return  diaryRepository.caculateAverageTotalSentimental(id).get(0);
    }

    @Override
    public Double calculateUserCurrentAvgSentimental(HttpServletRequest request){
        Long id = checkUser(request);

        return  diaryRepository.caculateAverageCurrentTenDiariesSentimental(id).get(0);
    }
}
