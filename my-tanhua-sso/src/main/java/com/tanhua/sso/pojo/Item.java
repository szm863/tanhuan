package com.tanhua.sso.pojo;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@Builder

public class Item {

    private Long id;
    private String title;
    private Date created;



}
