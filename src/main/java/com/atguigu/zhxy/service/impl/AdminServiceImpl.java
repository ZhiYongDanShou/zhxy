package com.atguigu.zhxy.service.impl;

import com.atguigu.zhxy.mapper.AdminMapper;
import com.atguigu.zhxy.pojo.Admin;
import com.atguigu.zhxy.pojo.LoginForm;
import com.atguigu.zhxy.service.AdminService;
import com.atguigu.zhxy.util.MD5;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.sql.Wrapper;

/**
 * @author ysh
 * @create 2022-04-14 16:20
 */
@Service("adminServiceImpl")        // 当前实现类在容器中的ID
@Transactional                      // 事务控制
public class AdminServiceImpl extends ServiceImpl<AdminMapper,Admin> implements AdminService {
//
//    @Override
//    public Admin login(LoginForm loginForm) {
//        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("name",loginForm.getUsername());
//        queryWrapper.eq("password", MD5.encrypt(loginForm.getPassword()));
//        Admin admin = baseMapper.selectOne(queryWrapper);
//        return admin;
//    }

    @Override
    public Admin login(LoginForm loginForm) {
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", loginForm.getUsername());
        queryWrapper.eq("password", MD5.encrypt(loginForm.getPassword()));
        Admin admin = baseMapper.selectOne(queryWrapper);
        return admin;
    }

    @Override
    public Admin getAdminById(Long userId) {
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", userId);
        Admin admin = baseMapper.selectOne(queryWrapper);
        return admin;
    }

    @Override
    public IPage<Admin> getAllAdmin(Page<Admin> pageParam, String adminName) {
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(adminName)) {
            queryWrapper.like("name", adminName);
        }
        queryWrapper.orderByDesc("id");
        Page<Admin> adminPage = baseMapper.selectPage(pageParam, queryWrapper);
        return adminPage;
    }
}