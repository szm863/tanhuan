package com.tanhua.sso.mytanhuasso;

import com.tanhua.sso.service.FaceEngineService;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class Test {


    @Autowired
    private   FaceEngineService faceEngineService;

    @org.junit.Test
    public void aa(){


        File file = new File("F:\\A.jpg");
        boolean checkIsPortrait = faceEngineService.checkIsPortrait(file);
        System.out.println(checkIsPortrait); // true|false
    }

}
