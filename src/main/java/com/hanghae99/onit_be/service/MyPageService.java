package com.hanghae99.onit_be.service;

import com.hanghae99.onit_be.dto.response.PlanDetailResDto;
import com.hanghae99.onit_be.dto.response.PlanResDto;
import com.hanghae99.onit_be.dto.response.ProfileResDto;
import com.hanghae99.onit_be.dto.response.RecordResDto;
import com.hanghae99.onit_be.entity.Location;
import com.hanghae99.onit_be.entity.Plan;
import com.hanghae99.onit_be.entity.User;
import com.hanghae99.onit_be.repository.PlanRepository;
import com.hanghae99.onit_be.repository.UserRepository;
import com.hanghae99.onit_be.security.UserDetailsImpl;
import com.hanghae99.onit_be.utils.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final UserRepository userRepository;
    private final PlanRepository planRepository;
    private final S3Uploader s3Uploader;

    // 프로필 이미지 수정
    @Transactional
    public ProfileResDto updateProfile(MultipartFile multipartFile, UserDetailsImpl userDetails) {
        User user = userRepository.findById(userDetails.getUser().getId()).orElseThrow(
                () -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다.")
        );

        // user repository에서 기존 이미지(파일명) 불러오기
        String oldImgName = user.getProfileImg();

        // 새로운 이미지 > S3 업로드 > 이미지 Url 생성
        String fileName = createFileName(multipartFile.getOriginalFilename());
        String imageUrl = s3Uploader.updateFile(multipartFile, oldImgName, fileName);
        User profile = new User(user.getId(), imageUrl);

        // 새로운 이미지 Url을 파싱해서 파일명으로만 DB에 저장
        String[] newImgUrl = imageUrl.split("/");
        String imageKey = newImgUrl[newImgUrl.length - 1];
        user.update(user.getId(), imageKey);

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

    // 지난 일정 목록 조회
    public Page<RecordResDto> getPlanHistory(User user, int pageno) {
        List<Plan> planList = planRepository.findAllByUserOrderByPlanDateDesc(userRepository.findById(user.getId()).orElseThrow(IllegalArgumentException::new));
        if(planList.isEmpty()){
            throw new IllegalArgumentException("지난 일정이 없습니다.");
        }
        List<RecordResDto> recordResDtoList = new ArrayList<>();
        for (Plan plan : planList) {
            LocalDateTime planDate = plan.getPlanDate();

            // 과거의 약속만 담아주기 (현재 서울 날짜의 시간이 planDate보다 이후일 때)
            if (LocalDateTime.now(ZoneId.of("Asia/Seoul")).isAfter(planDate)) {
                Long planId = plan.getId();
                String planName = plan.getPlanName();
                String planDateCv = planDate.format(DateTimeFormatter.ofPattern("M월 d일 E요일 HH:mm").withLocale(Locale.forLanguageTag("ko")));
                String address = plan.getLocation().getAddress();
                String penalty = plan.getPenalty();

                RecordResDto recordResDto = new RecordResDto(planId, planName, planDateCv, address, penalty);
                recordResDtoList.add(recordResDto);
            }
        }

        Pageable pageable = getPageable(pageno);

        int start = pageno * 6;
        int end = Math.min((start + 6), recordResDtoList.size());

        Page<RecordResDto> page = new PageImpl<>(recordResDtoList.subList(start, end), pageable, recordResDtoList.size());
        return page;
    }


    private Pageable getPageable(int page) {
        Sort.Direction direction = Sort.Direction.DESC;
        Sort sort = Sort.by(direction, "id");
        return PageRequest.of(page, 6, sort);
    }


    // 링크 공유를 통한 약속 저장
    public void savePlanInvitation(String url, User user){
        // 사용자의 현재 일정 리스트 찾기
//        List<Plan> planList = planRepository.findAllPlanByUserId(userRepository.findById(user.getId().orElseThrow(IllegalArgumentException::new)));
        List<Plan> planList = planRepository.findAllByUserOrderByPlanDateAsc(userRepository.findById(user.getId()).orElseThrow(IllegalArgumentException::new));
        // 공유받은 플랜 정보 찾기
        Plan planNew = planRepository.findPlanByUrl(url).orElseThrow(IllegalArgumentException::new);

        LocalDateTime planDateNew = planNew.getPlanDate();
        for (Plan plans : planList) {
            // 2. 이중 약속에 대한 처리 (약속 날짜와 오늘 날짜 비교)
            int comResult = compareDay(plans.getPlanDate(), planDateNew);
            if (comResult == 0) {
                // 3. 약속 시간 기준 +-2에 해당하는 약속은 정할 수 없게 처리
                // ex) 6시에 일정이 있으면 > 4시부터 8시 사이에는 일정을 잡지 못함
                long remainHours = ChronoUnit.HOURS.between(plans.getPlanDate().toLocalTime(),planDateNew.toLocalTime());
                if (!(remainHours > 2 || remainHours < -2))
                    throw new IllegalArgumentException("오늘 일정은 이미 있습니다.");
            }
        }
        Plan plan = new Plan(planNew, user);
        planRepository.save(plan);
    }

    public static int compareDay (LocalDateTime date1, LocalDateTime date2) {
        LocalDateTime dayDate1 = date1.truncatedTo(ChronoUnit.DAYS);
        LocalDateTime dayDate2 = date2.truncatedTo(ChronoUnit.DAYS);
        return dayDate1.compareTo(dayDate2);
    }
}