package com.tanhua.sso.mytanhuasso;

import com.tanhua.sso.service.FaceEngineService;
import org.junit.Test;

import java.io.File;

//@SpringBootTest
//@RunWith(SpringRunner.class)
class MyTanhuaSsoApplicationTests {

    public void contextLoads() {

        System.out.println(00);

    }


    @Test
    public void main(String[] args) {
        FaceEngineService faceEngineService = new FaceEngineService();

        File file = new File("F:\\A.jpg");
        boolean checkIsPortrait = faceEngineService.checkIsPortrait(file);
        System.out.println(checkIsPortrait); // true|false

    }

}
