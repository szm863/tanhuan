package com.tanhua.server.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tanhua.dubbo.server.pojo.RecommendUser;
import com.tanhua.dubbo.server.vo.PageInfo;
import com.tanhua.server.pojo.User;
import com.tanhua.server.pojo.UserInfo;
import com.tanhua.server.vo.PageResult;
import com.tanhua.server.vo.RecommendUserQueryParam;
import com.tanhua.server.vo.TodayBest;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TodayBestService {

    @Autowired
    private UserService userService;

    @Autowired
    private UserInforService userInforService;

    @Value("${tanhua.sso.default.user}")
    private Long defaultUser;

    @Autowired
    private RecommendUserService recommendUserService;

    public TodayBest todayBest(String token) {
        // 1 调用token解析
        User user = userService.queryToken(token);

        if (user == null) {
            return null;
        }
        // 2 获取id并查询最高匹配的人
        TodayBest todayBest = recommendUserService.findTodayBest(user.getId());

        // 3 获取最高匹配的人的信息封装返回
        if (todayBest == null) {
            //如果没有配配到，就默认给他一个
            todayBest = new TodayBest();
            todayBest.setId(defaultUser);
            todayBest.setFateValue(80L);
        }
        UserInfo userInfo= userInforService.queryUserInfoByUserId(todayBest.getId());
        todayBest.setAvatar(userInfo.getLogo());
        todayBest.setNickname(userInfo.getNickName());
        todayBest.setTags(StringUtils.split(userInfo.getTags(), ','));
        todayBest.setGender(userInfo.getSex().getValue()==1?"man" : "woman");
        todayBest.setAge(userInfo.getAge());

        return todayBest;
    }



    public PageResult queryRecommendation(String token, RecommendUserQueryParam queryParam) {
        PageResult pageResult=null;
        User user = userService.queryToken(token);
        if (user==null){
            return pageResult;
        }
        //获取所有匹配该用户的 佳人的ids
        PageInfo<RecommendUser> recommendUserPageInfo = this.recommendUserService.queryRecommendUserList(user.getId(), queryParam.getPage(), queryParam.getPagesize());

        if (recommendUserPageInfo==null|| CollectionUtils.isEmpty(recommendUserPageInfo.getRecords())){
            return pageResult;
        }
        //获取 ids的所有按条件匹配的人
        Set<Long> ids = recommendUserPageInfo.getRecords().stream().map(RecommendUser::getUserId).collect(Collectors.toSet());

        LambdaQueryWrapper<UserInfo> wapper= new LambdaQueryWrapper<>();
//        wapper.in(UserInfo::getUserId,ids);
//        wapper.like(UserInfo::getCity,queryParam.getCity());
//        //小于等于
//        wapper.le(UserInfo::getAge,queryParam.getAge());
//        wapper.eq(UserInfo::getEdu,queryParam.getEducation());
        wapper.eq(UserInfo::getSex, StringUtils.equals(queryParam.getGender(), "man") ? 1 : 2);

        //条件封装
        List<UserInfo> list =userService.queryRecommendation( wapper);
        //处理 返回结果
        List<RecommendUser> records = recommendUserPageInfo.getRecords();
        List<TodayBest> todayBestList=new ArrayList<>();
        for (UserInfo userInfo : list) {
            TodayBest todayBest = new TodayBest();
            todayBest.setId(userInfo.getUserId());
            todayBest.setAvatar(userInfo.getLogo());
            todayBest.setNickname(userInfo.getNickName());
            todayBest.setTags(StringUtils.split(userInfo.getTags(), ','));
            todayBest.setGender(userInfo.getSex().getValue() == 1 ? "man" : "woman");
            todayBest.setAge(userInfo.getAge());

            //缘分值
            for (RecommendUser record : records) {
                if(record.getUserId().longValue() == userInfo.getUserId().longValue()){
                    double score = Math.floor(record.getScore());//取整,98.2 -> 98
                    todayBest.setFateValue(Double.valueOf(score).longValue());
                    break;
                }
            }
            todayBestList.add(todayBest);
        }
        pageResult=new PageResult();
        pageResult.setItems(todayBestList);
        return pageResult;
    }
}
