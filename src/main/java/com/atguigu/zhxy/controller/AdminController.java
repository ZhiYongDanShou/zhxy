package com.atguigu.zhxy.controller;

import com.atguigu.zhxy.pojo.Admin;
import com.atguigu.zhxy.service.AdminService;
import com.atguigu.zhxy.util.MD5;
import com.atguigu.zhxy.util.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author ysh
 * @create 2022-04-14 16:36
 */
@Api(tags = "管理员控制器")
@RestController     // 返回 JOSN 数据
@RequestMapping("/sms/adminController")     // 业务接口要和前端提供的一一对应
public class AdminController {

    @Autowired
    AdminService adminService;

    //sms/adminController/deleteAdmin
    @ApiOperation("删除管理员，一个id的是单条删除，多个id是批量删除")
    @DeleteMapping("/deleteAdmin")
    public Result deleteAdmin(
            @ApiParam("删除的条件") @RequestBody List<Integer> ids
    ){
        adminService.removeByIds(ids);
        return Result.ok();
    }

    //sms/adminController/saveOrUpdateAdmin
    @ApiOperation("添加或修改管理员，不带id的是添加，带id的是修改")
    @PostMapping("/saveOrUpdateAdmin")
    public Result saveOrUpdateAdmin(
            @ApiParam("请求体中的数据") @RequestBody Admin admin
    ){
        // 如果是新增管理，密码要进行MD5加密
        Integer id = admin.getId();
        if(id == null || id == 0){
            admin.setPassword(MD5.encrypt(admin.getPassword()));
        }
        adminService.saveOrUpdate(admin);
        return Result.ok();
    }

    //sms/adminController/getAllAdmin/1/3?adminName=
    @ApiOperation("根据姓名模糊查询，带分页")
    @GetMapping("/getAllAdmin/{pageNo}/{pageSize}")
    public Result getAllAdmin(
            @ApiParam("分页查询的页码数") @PathVariable("pageNo") Integer pageNo,
            @ApiParam("分页查询的页大小") @PathVariable("pageSize") Integer pageSize,
            @ApiParam("模糊查询条件") String adminName
    ){
        Page<Admin> pageParam = new Page<>(pageNo,pageSize);

        IPage<Admin> adminIPage = adminService.getAllAdmin(pageParam,adminName);

        return Result.ok(adminIPage);
    }
}
