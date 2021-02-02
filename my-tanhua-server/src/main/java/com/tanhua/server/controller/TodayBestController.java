package com.tanhua.server.controller;

import com.tanhua.server.service.TodayBestService;
import com.tanhua.server.utils.Cache;
import com.tanhua.server.vo.PageResult;
import com.tanhua.server.vo.RecommendUserQueryParam;
import com.tanhua.server.vo.TodayBest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("tanhua")
@Slf4j
public class TodayBestController {

    @Autowired
    private TodayBestService todayBestService;


    @GetMapping("todayBest")
    public ResponseEntity<TodayBest> todayBest(@RequestHeader("Authorization") String token) {

        try {
            TodayBest todayBest = todayBestService.todayBest(token);
            return ResponseEntity.ok(todayBest);
        } catch (Exception e) {
            log.error("token 异常" + token, e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

    /**
     *
     *条件查询推荐列表
     */
    @GetMapping("recommendation")
    @Cache(time = "35")
    public ResponseEntity<PageResult> queryRecommendation(@RequestHeader("Authorization") String token,
                                                          @RequestBody RecommendUserQueryParam queryParam) {

        try {
            PageResult result=   todayBestService.queryRecommendation(token ,queryParam);
            if (result!=null){
                return ResponseEntity.ok(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);

    }
}
