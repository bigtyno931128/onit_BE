package com.hanghae99.onit_be.map;

import com.hanghae99.onit_be.common.ResultDto;
import com.hanghae99.onit_be.map.dto.MapResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MapController {

    private final MapService mapService;

    @GetMapping("/map/{randomUrl}")
    public ResponseEntity<ResultDto<MapResDto>> getMap (@PathVariable ("randomUrl") String url) {
        MapResDto mapResDto = mapService.getPlanInfo(url);
        return ResponseEntity.ok().body(new ResultDto<>("링크 접속 성공!", mapResDto));
    }
}
