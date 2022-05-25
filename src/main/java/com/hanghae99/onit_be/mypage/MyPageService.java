package com.hanghae99.onit_be.mypage;

import com.hanghae99.onit_be.mypage.dto.RecordResDto;
import com.hanghae99.onit_be.noti.event.PlanDeleteEvent;
import com.hanghae99.onit_be.plan.dto.PlanDetailResDto;
import com.hanghae99.onit_be.mypage.dto.ProfileResDto;
import com.hanghae99.onit_be.entity.Participant;
import com.hanghae99.onit_be.entity.Plan;
import com.hanghae99.onit_be.entity.User;
import com.hanghae99.onit_be.noti.event.NotificationEvent;
import com.hanghae99.onit_be.plan.PlanRepository;
import com.hanghae99.onit_be.plan.dto.PlanReqDto;
import com.hanghae99.onit_be.user.UserRepository;
import com.hanghae99.onit_be.security.UserDetailsImpl;
import com.hanghae99.onit_be.common.utils.S3Uploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.hanghae99.onit_be.common.utils.Date.*;
import static com.hanghae99.onit_be.common.utils.Page.getPageable;

@Slf4j
@Service
@RequiredArgsConstructor
public class MyPageService {

    private final UserRepository userRepository;
    private final S3Uploader s3Uploader;
    private final PlanRepository planRepository;
    private final ParticipantRepository participantRepository;
    private final ApplicationEventPublisher eventPublisher;

    // 프로필 이미지 수정
    @Transactional
    public void updateProfile(MultipartFile multipartFile, UserDetailsImpl userDetails) {
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

    // 링크 공유를 통한 약속 저장 (중복으로 참여는 x 이미 참여하고 있는 일정중에 조건 해결 못함)
    @Transactional
    public void savePlanInvitation(String url, User user) {


        User user1 = userRepository.findById(user.getId()).orElseThrow(IllegalArgumentException::new);

        Plan planNew = planRepository.findPlanByUrl(url).orElseThrow(IllegalArgumentException::new);

        List<Participant> participantList = participantRepository.findAllByPlan(planNew);
        if (!participantList.isEmpty()) {
            for (Participant participant : participantList) {
                if (Objects.equals(participant.getUser().getId(), user.getId())) {
                    log.info("접속한 사람의 id ==={}", user.getId());
                    log.info("이 사람의 id ==={}", user.getId());
                    throw new IllegalArgumentException("이미 일정에 참여 중입니다");
                }
            }
        }
        Participant participant = new Participant(planNew, user1);
        participantRepository.save(participant);

        // 알림
        //eventPublisher.publishEvent(new NotificationEvent(participant));
    }

    // 내가 참여한 일정 상세 가져오는 메서드 ( 현재 사용 x)
    public PlanDetailResDto getPlanInvitation(String url, User user) {
        Participant participant = participantRepository.findByUserAndPlan(
                userRepository.findById(user.getId()).orElseThrow(IllegalArgumentException::new),
                planRepository.findPlanByUrl(url).orElseThrow(IllegalArgumentException::new));
        return new PlanDetailResDto(participant);
    }


    // 지난 일정 목록 조회
    public Page<RecordResDto> getPlanHistory(User user, int pageno) {
        List<Plan> planList = planRepository.findAllByUserOrderByPlanDateDesc(userRepository.findById(user.getId()).orElseThrow(IllegalArgumentException::new));
        if (planList.isEmpty()) {
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

    // 참가한 목록에서 지우기 .
    @Transactional
    public void deleteInvitationPlan(String url, User user) {
        Plan plan = planRepository.findByUrl(url);
        Participant participant = participantRepository.findByUserAndPlan(user,plan);
        // 작성자만 삭제 가능
        if (Objects.equals(participant.getUser().getId(), user.getId())) {
            participantRepository.deleteByUserAndPlan(user, plan);
        }
    }
}