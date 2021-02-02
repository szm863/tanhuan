package com.tanhua.dubbo.server.api;

import com.tanhua.dubbo.server.pojo.Publish;

public interface QuanZiApi {
    /**
     * 发布动态
     *
     * @param publish
     * @return
     */

    boolean  savePublish(Publish publish);
}
