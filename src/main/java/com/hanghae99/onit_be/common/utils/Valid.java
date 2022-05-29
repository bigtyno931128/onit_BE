package com.hanghae99.onit_be.common.utils;

import com.hanghae99.onit_be.user.dto.SignupReqDto;
import com.hanghae99.onit_be.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Valid {

    private final UserRepository userRepository;

    @Autowired
    public Valid(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    //회원가입 유효성 검사
    public static void validUser(SignupReqDto requestDto) throws IllegalArgumentException {

        if(!requestDto.getUsername().matches("^[a-zA-Z0-9-_]{3,10}$")){
            throw new IllegalArgumentException("아이디는 영어 or 숫자로 3자리 이상 ~10자리 이하로 입력하셔야 합니다!");
        }
        if(!requestDto.getPassword().matches("^[a-zA-Z0-9-_]{4,10}$")){
            throw new IllegalArgumentException("비밀번호는 영어 or 숫자로 4자리 이상 ~12자리 이하로 입력하셔야 합니다!");
        }
        if(!requestDto.getNickname().matches("^[가-힣a-zA-Z0-9-_]{2,8}$")){
            throw new IllegalArgumentException("닉네임은 한글 or 영어 or 숫자로 2자리 이상 ~8자리 이하로 입력하셔야 합니다!");
        }
    }


    /**
     * 두 지점간의 거리 계산 * *
     *
     * @param lat1 지점 1 위도 *
     * @param lon1 지점 1 경도 *
     * @param lat2 지점 2 위도 *
     * @param lon2 지점 2 경도 *
     * @param unit 거리 표출단위 *
     * @return
     */
    
    // 거리 계산 좌표끼리의
    public static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));

        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        if (unit == "kilometer") {
            dist = dist * 1.609344;
        } else if (unit == "meter") {
            dist = dist * 1609.344;
        }
        return (dist);
    }

    // This function converts decimal degrees to radians
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    // This function converts radians to decimal degrees
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }
}
