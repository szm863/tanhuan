package com.tanhua.server.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.tanhua.dubbo.server.api.QuanZiApi;
import com.tanhua.dubbo.server.pojo.Publish;
import com.tanhua.server.pojo.User;
import com.tanhua.server.vo.PicUploadResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class MovementsService {
    @Autowired
    private UserService userService;

    @Autowired
    private PicUploadService picUploadService;

    @Reference(version = "1.0.0")
    private QuanZiApi quanZiApi;

    public boolean savePublish(String textContent, String location, String longitude,
                               String latitude, MultipartFile[] multipartFile,
                               String token) {
        User user = userService.queryToken(token);
        if (user == null) {
            return false;
        }

        Publish publish = new Publish();
        publish.setUserId(user.getId());
        publish.setText(textContent);
        publish.setLocationName(location);
        publish.setLatitude(latitude);
        publish.setLongitude(longitude);
        publish.setSeeType(1);
        List<String> picUrls = new ArrayList<>();
        for (MultipartFile file : multipartFile) {
            PicUploadResult uplod = picUploadService.uplod(file);
            picUrls.add(uplod.getName());
        }
        publish.setMedias(picUrls);
        return  this.quanZiApi.savePublish(publish) ;
    }
}
