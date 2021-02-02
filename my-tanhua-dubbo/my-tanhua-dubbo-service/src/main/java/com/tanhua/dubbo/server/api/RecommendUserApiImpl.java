package com.tanhua.dubbo.server.api;

import com.alibaba.dubbo.config.annotation.Service;
import com.tanhua.dubbo.server.pojo.RecommendUser;
import com.tanhua.dubbo.server.vo.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@Service(version = "1.0.0")
public class RecommendUserApiImpl  implements RecommendUserApi{

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public RecommendUser queryMaxScore(Long userId) {
        //toUserId 是用户的id 用来用户查询和匹配度最高的 人
        Query query= Query.query(Criteria.where("toUserId").is(userId))
                .with(Sort.by(Sort.Order.asc("socre"))).limit(1);
        //todo
        return this.mongoTemplate.findOne(query,RecommendUser.class) ;

    }

    @Override
    public PageInfo<RecommendUser> queryPageInfo(Long userId, Integer pageNum, Integer pageSize) {

        //查询和 用户匹配 高的 人，分数高的排在上面
        PageRequest pageable=PageRequest.of(pageNum-1,pageSize,Sort.by(Sort.Order.desc("score"))) ;
        Query query = Query.query(Criteria.where("toUserId").is(userId)).with(pageable);
        List<RecommendUser> list=this.mongoTemplate.find(query,RecommendUser.class);
        return new PageInfo(0,pageNum,pageSize,list);
    }
}
