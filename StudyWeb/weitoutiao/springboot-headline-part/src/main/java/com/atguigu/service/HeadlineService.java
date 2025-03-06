package com.atguigu.service;

import com.atguigu.pojo.Headline;
import com.atguigu.pojo.vo.PortalVo;
import com.atguigu.utils.Result;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 50477
* @description 针对表【news_headline】的数据库操作Service
* @createDate 2025-03-05 20:11:55
*/
public interface HeadlineService extends IService<Headline> {

    Result findNewsPage(PortalVo portalVd);


    Result showHeadlineDetail(Integer hid);
}
