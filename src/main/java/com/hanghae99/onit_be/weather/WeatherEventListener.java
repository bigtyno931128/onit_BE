package com.hanghae99.onit_be.weather;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hanghae99.onit_be.entity.Plan;

import com.hanghae99.onit_be.mypage.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.json.JSONObject;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.hanghae99.onit_be.common.utils.Date.compareDay;

@Async
@Slf4j
@Transactional
@Component
@RequiredArgsConstructor
public class WeatherEventListener {

    private final WeatherRepository weatherRepository;

    //일정을 처음 등록할때 위도 경도로 날씨 api 호출 -> 기상 예측 정보를 데이터로 받아옴. (8 일치 )
    @EventListener
    public void handleWeatherCreateEvent(WeatherCreateEvent weatherCreateEvent) {

        Plan plan = weatherCreateEvent.getPlan();

        log.info("위도 ={}", plan.getLocation().getLng());
        log.info("경도 ={}", plan.getLocation().getLat());

        String address = plan.getLocation().getAddress();
        LocalDateTime dayDate1 = plan.getPlanDate().truncatedTo(ChronoUnit.DAYS);

        String API_KEY = "384994b16fb4098a5312b226bd2d76e5";
        String PART = "current,minutely,hourly,alerts";
        String LANG = "kr";

        String urlString = "https://api.openweathermap.org/data/2.5/onecall?lat="
                + plan.getLocation().getLat() +
                "&lon=" + plan.getLocation().getLng() +
                "&exclude=" + PART +
                "&lang=" + LANG +
                "&appid=" + API_KEY;

        try {

            URL url = new URL(urlString);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line;
            String result = "";
            StringBuilder content = new StringBuilder();

            while ((line = in.readLine()) != null) {
                result = result.concat(line);
            }
            in.close();
            JSONObject json =new JSONObject();

            Gson gson = new Gson();
            Map<String, Object> jsonObject = gson.fromJson(result, new TypeToken<Map<String, Object>>() {
            }.getType());

            List<Map<String, Object>> jsonList = (List) jsonObject.get("daily");
            //log.info(jsonList.toString());
            for (Map<String, Object> test : jsonList) {
                log.info(test.toString());

                String main1 = (test.get("weather").toString().split(",")[1]);
                String icon1 = (test.get("weather").toString().split(",")[3]);
                String icon2 = icon1.split("=")[1];
                String icon = icon2.split("}")[0];

                log.info(icon);
                String id = (test.get("weather").toString().split(",")[0]);
                String main = main1.split("=")[1];
                String id1 = id.split("=")[1];
                String id2 = id1.split("\\.")[0];

                if (id2.equals("800")) {
                    id2 = "맑음";
                }
                if (id2.equals("200") || id2.equals("201") || id2.equals("202") || id2.equals("210") || id2.equals("211")
                        || id2.equals("212") || id2.equals("221") || id2.equals("230") || id2.equals("231") || id2.equals("232")
                ) {
                    id2 = "천둥";
                }
                if (id2.equals("300") || id2.equals("301") || id2.equals("302") || id2.equals("310") || id2.equals("311") || id2.equals("312")
                        || id2.equals("313") || id2.equals("314") || id2.equals("321")
                ) {
                    id2 ="소나기";
                }
                if (id2.equals("500") || id2.equals("501") || id2.equals("502") || id2.equals("503") || id2.equals("504") || id2.equals("511")
                        || id2.equals("520") || id2.equals("521") || id2.equals("522") || id2.equals("530")
                ) {
                    id2 ="비";
                }
                if (id2.equals("600") || id2.equals("601") || id2.equals("602") || id2.equals("611") || id2.equals("612") || id2.equals("613")
                        || id2.equals("615") || id2.equals("616") || id2.equals("620") || id2.equals("621") || id2.equals("622")
                ) {
                    id2 = "눈";
                }
                if (id2.equals("701") || id2.equals("711") || id2.equals("721") || id2.equals("731") || id2.equals("741") || id2.equals("751") ||
                        id2.equals("761") || id2.equals("762")
                ) {
                    id2 = "안개";
                }
                if (id2.equals("771")|| id2.equals("781")
                ) {
                    id2 = "태풍";
                }
                if (id2.equals("801") || id2.equals("802") || id2.equals("803") || id2.equals("804")
                ) {
                    id2 = "구름";
                }

                log.info(id2);

                double time = Double.parseDouble(test.get("dt").toString());
                Map<String, Object> jsonObject2 = gson.fromJson(test.get("temp").toString(), new TypeToken<Map<String, Object>>() {
                }.getType());

                double ktemp = (double) jsonObject2.get("day");
                int temp = (int) (ktemp - 273.15);

                log.info("오늘의 평균 온도={}", temp);

                int realTime= (int) time;
                String krTime = getTimestampToDate(String.valueOf(realTime));
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime weatherTime = LocalDateTime.parse(krTime , formatter);

                log.info("가져오는 날짜  ={}", String.valueOf(weatherTime));
                log.info("생성당시에 약속잡기로 한 날짜 ={}", String.valueOf(dayDate1));

                Long planId= plan.getId();
                LocalDateTime planDate = plan.getPlanDate();
                Weather weather = new Weather(address,main,id2,temp,planDate,weatherTime,planId,icon);
                weatherRepository.save(weather);

            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    // unix timestamp to date String
    private static String getTimestampToDate(String timestampStr) {
        long timestamp = Long.parseLong(timestampStr);
        Date date = new Date(timestamp * 1000L);
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+9"));
        String formattedDate = sdf.format(date);
        return formattedDate;
    }

    //갱신 어떻게 할까 ,, ?


    // 매일 새벽 5시에 날씨 테이블을 조회 후 , 지난 날짜에 대한  정보들은 삭제 .
    @Scheduled(cron="0 0 05 * * ?")
    public void deleteWeather(){
        List<Weather> weatherList = weatherRepository.findAll();
        for (Weather weather : weatherList) {
            int comResult = compareDay( weather.getWeatherDate(), LocalDateTime.now(ZoneId.of("Asia/Seoul")));
            log.info(String.valueOf(comResult));
            if (comResult == -1){
                weatherRepository.deleteById(weather.getId());
            }
        }
    }
}



