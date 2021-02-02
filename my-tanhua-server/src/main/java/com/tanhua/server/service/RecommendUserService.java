package com.tanhua.server.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.tanhua.dubbo.server.api.RecommendUserApi;
import com.tanhua.dubbo.server.pojo.RecommendUser;
import com.tanhua.dubbo.server.vo.PageInfo;
import com.tanhua.server.vo.TodayBest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RecommendUserService {

    @Reference(version = "1.0.0")
    private RecommendUserApi recommendUserApi;

    @Autowired
    private UserService userService;



    public TodayBest findTodayBest(Long id) {

        RecommendUser recommendUser =this.recommendUserApi.queryMaxScore(id);
        if (recommendUser == null) {
            return null;
        }
        TodayBest todayBest = new TodayBest();
        //被推荐出的人的id
        todayBest.setId(recommendUser.getUserId());
        //取整
        if (recommendUser.getScore() != null) {
            double score = Math.floor(recommendUser.getScore());
            todayBest.setFateValue(Double.valueOf(score).longValue());
        }
        return todayBest;
    }


    public PageInfo<RecommendUser> queryRecommendUserList(Long id, Integer page, Integer pagesize) {
       return this.recommendUserApi.queryPageInfo(id,page,pagesize);
    }
}
