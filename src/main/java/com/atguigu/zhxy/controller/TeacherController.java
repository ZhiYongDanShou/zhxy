package com.atguigu.zhxy.controller;

import com.atguigu.zhxy.pojo.Teacher;
import com.atguigu.zhxy.service.TeacherService;
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
 * @create 2022-04-14 16:37
 */
@Api(tags = "教师控制器")
@RestController
@RequestMapping("/sms/teacherController")
public class TeacherController {

    @Autowired
    TeacherService teacherService;

    //sms/teacherController/deleteTeacher
    @ApiOperation("删除教师，一个id是单条删除，多个id数组是批量删除")
    @DeleteMapping("/deleteTeacher")
    public Result deleteTeacher(
            @ApiParam("删除的条件id") @RequestBody List<Integer> ids
    ){
        teacherService.removeByIds(ids);
        return Result.ok();
    }

    //sms/teacherController/saveOrUpdateTeacher
    @ApiOperation("添加或修改老师，不带id的是添加，带id的是修改")
    @PostMapping("/saveOrUpdateTeacher")
    public Result saveOrUpdateTeacher(
            @ApiParam("请求体中的参数") @RequestBody Teacher teacher
    ){
        // 如果是新增，要对密码进行加密
        if(teacher.getId()== null || teacher.getId() == 0){
            teacher.setPassword(MD5.encrypt(teacher.getPassword()));
        }

        teacherService.saveOrUpdate(teacher);
        return Result.ok();
    }

    //sms/teacherController/getTeachers/1/3?name=&clazzName=
    @ApiOperation("根据教师名称、班级名称进行模糊查询，带分页")
    @GetMapping("/getTeachers/{pageNo}/{pageSize}")
//    public Result getTeachers(
//            @ApiParam("分页查询的页码数") @PathVariable("pageNo") Integer pageNo,
//            @ApiParam("分页查询的页大小") @PathVariable("pageSize") Integer pageSize,
//            @ApiParam("模糊查询的条件")Teacher teacher
//            ){
//        Page<Teacher> pageParam = new Page<>(pageNo,pageSize);
//        IPage<Teacher> teacherIpage = teacherService.getTeachers(pageParam,teacher);
//        return Result.ok(teacherIpage);
//    }
    public Result getTeachers(
            @PathVariable("pageNo") Integer pageNo,
            @ApiParam("分页查询的大小")@PathVariable("pageSize") Integer pageSize,
            Teacher teacher
    ){
        Page<Teacher> teacherPage = new Page<Teacher>(pageNo,pageSize);
        IPage<Teacher> teachers = teacherService.getTeachers(teacherPage, teacher);
        return Result.ok(teachers);


    }
}
