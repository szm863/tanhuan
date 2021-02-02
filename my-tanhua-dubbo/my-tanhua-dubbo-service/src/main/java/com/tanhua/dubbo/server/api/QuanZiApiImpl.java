package com.tanhua.dubbo.server.api;

import com.alibaba.dubbo.config.annotation.Service;
import com.tanhua.dubbo.server.pojo.Album;
import com.tanhua.dubbo.server.pojo.Publish;
import com.tanhua.dubbo.server.pojo.TimeLine;
import com.tanhua.dubbo.server.pojo.Users;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@Service(version = "1.0.0")
public class QuanZiApiImpl implements QuanZiApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public boolean savePublish(Publish publish) {
        //校验id
        if (publish.getUserId()==null) {
            return false;
        }
        try {
            // 发布动态，只要一张动态发布表 存所有用户的
            publish.setCreated(System.currentTimeMillis());
            //发一条动态 给一个唯一id
            publish.setId(ObjectId.get());
            mongoTemplate.save(publish);

            //构建相册
            Album album = new Album();
            album.setPublishId(publish.getId());
            album.setCreated(System.currentTimeMillis());
            album.setId(ObjectId.get());
            this.mongoTemplate.save(album,"quanzi_album_"+publish.getUserId());


            //写入到好友时间线
            Query query=Query.query(Criteria.where("userId").is(publish.getUserId()));
            List<Users> friends = this.mongoTemplate.find(query, Users.class);

            for (Users user : friends) {
                TimeLine timeline=new TimeLine();
                //存的是用户id  当前用户的
                timeline.setUserId(user.getUserId());
                timeline.setDate(System.currentTimeMillis());
                timeline.setId(ObjectId.get());
                timeline.setPublishId(publish.getId());
                this.mongoTemplate.save(timeline, "quanzi_time_line_"+user.getFriendId());
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }


        return false;
    }
}
