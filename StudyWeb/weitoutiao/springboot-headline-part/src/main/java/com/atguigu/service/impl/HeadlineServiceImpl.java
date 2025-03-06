package com.atguigu.service.impl;

import com.atguigu.pojo.vo.PortalVo;
import com.atguigu.utils.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.pojo.Headline;
import com.atguigu.service.HeadlineService;
import com.atguigu.mapper.HeadlineMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @author 50477
* @description 针对表【news_headline】的数据库操作Service实现
* @createDate 2025-03-05 20:11:55
*/
@Service
public class HeadlineServiceImpl extends ServiceImpl<HeadlineMapper, Headline>
    implements HeadlineService{

    @Autowired
    private HeadlineMapper headlineMapper;

    /*
    * 1.进行分页数据查询
    * 2.分页数据，拼接到result即可
    *
    * */
    @Override
    public Result findNewsPage(PortalVo portalVo){

        IPage page=new Page<>(portalVo.getPageNum(),portalVo.getPageSize());
        headlineMapper.selectMyPage(page,portalVo);
        List<Map> records = page.getRecords();
        Map data = new HashMap();
        data.put("pageData",records);
        data.put("pageNum",page.getCurrent());
        data.put("pageSize",page.getSize());
        data.put("totalPage",page.getPages());
        data.put("totalSize",page.getTotal());

        Map pageInfo = new HashMap();
        pageInfo.put("pageInfo",data);
        return Result.ok(pageInfo);
    }

    /*
    *
    *    1、查询对应的数据即可 【多表，头条和用户表，方法需要自定义返回map即可】
    *       2、修改阅读量 +1  【乐观锁】
    * */
    @Override
    public Result showHeadlineDetail(Integer hid){
        Map data=headlineMapper.queryDetailMap(hid);

        Map headlineMap =new HashMap();
        headlineMap.put("headline",data);

        //修改阅读量 +1
        Headline headline =new Headline();
        headline.setHid((Integer)data.get("hid"));
        headline.setVersion((Integer)data.get("version"));
        //阅读量+1
        headline.setPageViews((Integer)data.get("pageViews")+1);
        headlineMapper.updateById(headline);

        return Result.ok(headlineMap);
    }
}




