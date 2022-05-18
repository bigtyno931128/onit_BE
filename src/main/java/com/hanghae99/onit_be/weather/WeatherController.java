package com.hanghae99.onit_be.weather;


import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.hanghae99.onit_be.entity.Participant;
import com.hanghae99.onit_be.entity.Plan;
import com.hanghae99.onit_be.mypage.ParticipantRepository;
import com.hanghae99.onit_be.plan.PlanRepository;
import com.hanghae99.onit_be.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

@RestController
@Slf4j
@RequiredArgsConstructor
public class WeatherController {

    private final PlanRepository planRepository;
    private final ParticipantRepository participantRepository;

    public static Map<String, Object> jsonToMap(String str) {
        Map<String, Object> map = new Gson().fromJson(str, new
                TypeToken<HashMap<String, Object>>() {
                }.getType());
        return map;
    }

    // plan.loation lat,lng -> cityName   (현재는 plan 에서 받아온 데이터로 가져오고 있지만 , plan 을 등록하는 시점에서 기상테이블에
    // 데이터를 저장하게 된다면 비동기를 이용하고 Dto 를 통해 들어온 좌표로 저장해야 할 것 같다 .
    @GetMapping("/api/weather")
    public String getWeather(@AuthenticationPrincipal UserDetailsImpl userDetails) {

        List<Participant> participantList = participantRepository.findAllByUserOrderByPlanDate(userDetails.getUser());
        List<Plan> planList = new ArrayList<>();

        String API_KEY = "384994b16fb4098a5312b226bd2d76e5";
        String LANG = "kr";

        for (Participant participant : participantList) {
            planList.add(participant.getPlan());
        }

        for (Plan plan : planList) {

            String urlString = "https://api.openweathermap.org/data/2.5/weather?lat=" + plan.getLocation().getLat()
                    + "&lon=" + plan.getLocation().getLng() + "&lang=" + LANG
                    + "&appid=" + API_KEY;
            String apiResult = "";
            String cityName = "";

            log.info("위도 ={}", plan.getLocation().getLat());
            log.info("경도 ={}", plan.getLocation().getLng());

            try {
                StringBuilder result = new StringBuilder();
                URL url = new URL(urlString);
                URLConnection conn = url.openConnection();
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }
                rd.close();
                Map<String, Object> respMap = jsonToMap(result.toString());

                apiResult = result.toString();
                cityName = (String) respMap.get("name");

                log.info("위도,경도로 호출했을때 위치에 대한 현재 날씨  ={}", apiResult);
                log.info("위도,경도로 호출했을때 위치에 대한 도시 이름  ={}", cityName);

            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
            return cityName;
        }
        return "ok";
    }


    @GetMapping("/api/weathers")
    public String getWeathers(@AuthenticationPrincipal UserDetailsImpl userDetails) {

        List<Participant> participantList = participantRepository.findAllByUserOrderByPlanDate(userDetails.getUser());
        List<Plan> planList = new ArrayList<>();

        String API_KEY = "384994b16fb4098a5312b226bd2d76e5";
        String PART = "current,minutely,hourly,alerts";
        String LANG = "kr";

        for (Participant participant : participantList) {
            planList.add(participant.getPlan());
        }

        for (Plan plan : planList) {
            String urlString = "https://api.openweathermap.org/data/2.5/onecall?lat="
                    + plan.getLocation().getLat() +
                    "&lon=" + plan.getLocation().getLng() +
                    "&exclude=" + PART +
                    "&lang=" + LANG +
                    "&appid=" + API_KEY;

            String apiResult = "";

            log.info("위도 ={}", plan.getLocation().getLat());
            log.info("경도 ={}", plan.getLocation().getLng());

            try {
                URL url = new URL(urlString);
                HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String line;
                StringBuilder content = new StringBuilder();
                while ((line = in.readLine()) != null) {
                    content.append(line);
                }
                in.close();
                //con.disconnect();
                log.info(content.toString());
                Map<String, Object> respMap = jsonToMap(content.toString());
                Map<String, Object> weatherMap = jsonToMap(respMap.get("weather").toString());
                log.info("날씨 예측 ={}",(String) weatherMap.get("description"));
                Collection<Object> values = respMap.values();
                log.info(String.valueOf(values));
                log.info(respMap.get("temp").toString());
                apiResult = content.toString();
                log.info(apiResult);
                log.info("위도,경도로 호출했을때 위치에 대한 기상예보  ={}", respMap);

            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
            return apiResult;
        }
        return "ok";
    }

}

