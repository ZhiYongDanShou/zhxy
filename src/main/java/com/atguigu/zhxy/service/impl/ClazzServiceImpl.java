package com.atguigu.zhxy.service.impl;

import com.atguigu.zhxy.mapper.ClazzMapper;
import com.atguigu.zhxy.pojo.Clazz;
import com.atguigu.zhxy.service.ClazzService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author ysh
 * @create 2022-04-14 16:31
 */
@Service("clazzServiceImpl")    // 当前实现类在容器中的id
@Transactional                  // 事务控制
public class ClazzServiceImpl extends ServiceImpl<ClazzMapper,Clazz> implements ClazzService {

    @Override
    public IPage<Clazz> getClazzByOpr(Page<Clazz> pageParam, Clazz clazz) {

        QueryWrapper queryWrapper = new QueryWrapper();
        if(clazz != null){

            // 班级名称条件
            String name = clazz.getName();
            if(!StringUtils.isEmpty(name)){
                queryWrapper.like("name",name);
            }

            // 年级名称条件
            String gradeName = clazz.getGradeName();
            if(!StringUtils.isEmpty(gradeName)){
                queryWrapper.like("grade_name",gradeName);
            }

            queryWrapper.orderByDesc("id");
            queryWrapper.orderByAsc("name");
        }

        Page<Clazz> page = baseMapper.selectPage(pageParam, queryWrapper);
        return page;
    }

    @Override
    public List<Clazz> getClazzs() {
        QueryWrapper queryWrapper = new QueryWrapper();
        List<Clazz> clazzes = baseMapper.selectList(null);
        return clazzes;
    }
}
