package com.tanhua.sso.mytanhuasso;

import com.tanhua.sso.service.FaceEngineService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

@Data
@AllArgsConstructor
public class Stu  implements  Comparable<Stu>{

    private  int agee;
    private  int socre;
    private  String name;

    @Override
    public int compareTo(Stu o) {

        System.out.println("this:"+this.agee);
        System.out.println(o.agee);
        return this.agee-o.agee;
    }




}
