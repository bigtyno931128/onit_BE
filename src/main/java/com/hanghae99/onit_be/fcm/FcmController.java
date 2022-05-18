//package com.hanghae99.onit_be.fcm;
//
//
//import com.hanghae99.onit_be.security.UserDetailsImpl;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.io.IOException;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api")
//public class FcmController {
//    // 배포 시 삭제할 controller 입니다(테스트용)
//    private final FirebaseCloudMessageService firebaseCloudMessageService;
//
//    @PostMapping("/fcm")
//    public ResponseEntity<Success<Object>> pushMessage(@RequestBody FcmRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
//        // Push 와 message 보내는 것을 같이 처리 .
//        FcmResponseDto responseDto = firebaseCloudMessageService.manualPush(requestDto.getPlanId(), userDetails.getUser().getId());
//
//        firebaseCloudMessageService.sendMessageTo(
//                responseDto.getToken(),
//                responseDto.getTitle(),
//                responseDto.getBody(),
//                responseDto.getUrl());
//
//        return ResponseEntity.ok().body(new Success<>("push message 전송 완료"));
//    }
//}