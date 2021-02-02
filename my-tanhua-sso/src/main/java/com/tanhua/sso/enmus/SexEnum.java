package  com.tanhua.sso.enmus;


import com.baomidou.mybatisplus.core.enums.IEnum;
import com.tanhua.sso.dto.MessageDto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;


public enum SexEnum implements IEnum<Integer> {

    MAN(1,"男"),
    WOMAN(2,"女"),
    UNKNOWN(3,"未知");

    private int value;
    private String desc;

    SexEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return this.desc;
    }

    public static void main(String[] args) {

       List<MessageDto> objects = new ArrayList<>();

        Stream<MessageDto> limit = objects.stream().limit(2).limit(2);


    }
}
