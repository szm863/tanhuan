package com.tanhua.sso.service;

import com.aliyun.oss.OSSClient;
import com.tanhua.sso.config.AliyunConfig;
import com.tanhua.sso.vo.PicUploadResult;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;

@Service
public class PicUploadService {
    // 允许上传的格式
    private static final String[] IMAGE_TYPE = new String[]{".bmp", ".jpg",
            ".jpeg", ".gif", ".png"};



    @Autowired
    private OSSClient ossClient;

    @Autowired
    private AliyunConfig aliyunConfig;

    public PicUploadResult uplod(MultipartFile multipartFile) {
        PicUploadResult picUploadResult=new PicUploadResult();
        if (multipartFile==null) {
            picUploadResult.setStatus("error");
            return picUploadResult;
        }
        String originalFilename = multipartFile.getOriginalFilename();

        //判断图片类型
        boolean isPicture=false;
        for (String file : IMAGE_TYPE) {
            if (StringUtils.endsWithIgnoreCase(originalFilename,file)){
                isPicture=true;
                break;
            }
        }
        if (!isPicture){
            picUploadResult.setStatus("error");
            return picUploadResult;
        }

        String filePath = getFilePath(originalFilename);
        // 上传到阿里云
        try {
            // 目录结构：images/2018/12/29/xxxx.jpg
            ossClient.putObject(aliyunConfig.getBucketName(), filePath, new
                    ByteArrayInputStream(multipartFile.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
            //上传失败
            picUploadResult.setStatus("error");
            return picUploadResult;
        }

        // 上传成功
        picUploadResult.setName(this.aliyunConfig.getUrlPrefix()+filePath);
        picUploadResult.setStatus("done");
        picUploadResult.setUid(String.valueOf(System.currentTimeMillis()));
        return picUploadResult;
    }




    private String getFilePath(String sourceFileName) {
        DateTime dateTime = new DateTime();
        //images/yyyy/MM/dd/xxxxxxx.jpg
        return "images/" + dateTime.toString("yyyy")
                + "/" + dateTime.toString("MM") + "/"
                + dateTime.toString("dd") + "/" + System.currentTimeMillis() +
                RandomUtils.nextInt(100, 9999) + "." +
                StringUtils.substringAfterLast(sourceFileName, ".");
    }
}
