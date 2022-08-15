package com.atguigu.zhxy.service;

import com.atguigu.zhxy.pojo.Grade;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author ysh
 * @create 2022-04-14 16:29
 */
public interface GradeService extends IService<Grade> {
    IPage<Grade> getGradeByOpr(Page<Grade> pageParam, String gradeName);

    List<Grade> getGrades();
}
