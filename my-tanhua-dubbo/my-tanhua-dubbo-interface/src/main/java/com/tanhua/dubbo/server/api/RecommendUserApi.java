package com.tanhua.dubbo.server.api;

import com.tanhua.dubbo.server.pojo.RecommendUser;
import com.tanhua.dubbo.server.vo.PageInfo;

public interface RecommendUserApi {
    /**
     * 查询最高配备分数
     */
    RecommendUser queryMaxScore(Long userId);

    /**
     * 按照得分倒序
     */
    PageInfo<RecommendUser> queryPageInfo(Long userId, Integer pageNum, Integer pageSize);


}
