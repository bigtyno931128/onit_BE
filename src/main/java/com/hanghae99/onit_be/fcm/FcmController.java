//package com.hanghae99.onit_be.fcm;
//
//import com.hanghae99.onit_be.dto.response.ResultDto;
//import com.hanghae99.onit_be.security.UserDetailsImpl;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.io.IOException;
//
//@Slf4j
//@RestController
//@RequiredArgsConstructor
//public class FcmController {
//
//    private final FirebaseCloudMessageService firebaseCloudMessageService;
//
//
//    @PostMapping("/api/fcm")
//    public ResponseEntity<ResultDto> pushMessage(@RequestBody FcmReqDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
//        FcmResDto responseDto = firebaseCloudMessageService.manualPush(requestDto.getPlanId(), userDetails.getUser().getId());
//        firebaseCloudMessageService.sendMessageTo(
//                responseDto.getToken(),
//                responseDto.getTitle(),
//                responseDto.getBody(),
//                responseDto.getUrl());
//
//        return ResponseEntity.ok().body(new ResultDto<>("push message 전송 완료"));
//    }
//}
//
////    @PostMapping("/api/fcm")
////    public ResponseEntity pushMessage(@RequestBody RequestDTO requestDTO) throws IOException {
////        log.info(requestDTO.getTargetToken());
////
////        System.out.println(requestDTO.getTargetToken() + " "
////                +requestDTO.getTitle() + " " + requestDTO.getBody());
////
////        firebaseCloudMessageService.sendMessageTo(
////                requestDTO.getTargetToken(),
////                requestDTO.getTitle(),
////                requestDTO.getBody(),
////                requestDTO.getUrl();
////        return ResponseEntity.ok().build();
////    }
////}