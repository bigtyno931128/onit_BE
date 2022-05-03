package com.hanghae99.onit_be.service;

import com.hanghae99.onit_be.dto.ProfileResDto;
import com.hanghae99.onit_be.entity.User;
import com.hanghae99.onit_be.repository.UserRepository;
import com.hanghae99.onit_be.security.UserDetailsImpl;
import com.hanghae99.onit_be.utils.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final UserRepository userRepository;
    private final S3Uploader s3Uploader;

    // 프로필 이미지 수정
    @Transactional
    public ProfileResDto updateProfile(MultipartFile multipartFile, UserDetailsImpl userDetails){
        User user = userRepository.findById(userDetails.getUser().getId()).orElseThrow(
                () -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다.")
        );

        // user repository에서 기존 이미지(파일명) 불러오기
        String oldImgName = user.getProfileImg();

        // 새로운 이미지 > S3 업로드 > 이미지 Url 생성
        String fileName = createFileName(multipartFile.getOriginalFilename());
        String imageUrl = s3Uploader.updateFile(multipartFile,oldImgName, fileName);
        User profile = new User(user.getId(),imageUrl);

        // 새로운 이미지 Url을 파싱해서 파일명으로만 DB에 저장
        String [] newImgUrl = imageUrl.split("/");
        String imageKey = newImgUrl[newImgUrl.length-1];
        user.update(user.getId(),imageKey);

        return new ProfileResDto(profile);
    }

    // 이미지 파일명 변환 관련 메소드
    public String createFileName(String fileName) {
        // 먼저 파일 업로드 시, 파일명을 난수화하기 위해 random으로 돌립니다.
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

    private String getFileExtension(String fileName) {
        // file 형식이 잘못된 경우를 확인하기 위해 만들어진 로직이며,
        // 파일 타입과 상관없이 업로드할 수 있게 하기 위해 .의 존재 유무만 판단
        try {
            return fileName.substring(fileName.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 형식의 파일(" + fileName + ") 입니다.");
        }

    }
}