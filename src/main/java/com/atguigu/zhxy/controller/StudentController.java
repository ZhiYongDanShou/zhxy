package com.atguigu.zhxy.controller;

import com.atguigu.zhxy.pojo.Student;
import com.atguigu.zhxy.service.StudentService;
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
@Api(tags = "学生控制器")
@RestController
@RequestMapping("/sms/studentController")
public class StudentController {

    @Autowired
    StudentService studentService;

    //sms/studentController/delStudentById
    @ApiOperation("删除学生，一个id是单条删除，多个id数组是批量删除")
    @DeleteMapping("/delStudentById")
    public Result deleteStudent(
           @ApiParam("删除的条件id") @RequestBody List<Integer> ids
            ){
        studentService.removeByIds(ids);
        return Result.ok();
    }


    // http://localhost:9001/sms/studentController/addOrUpdateStudent
    @ApiOperation("添加或修改学生，不带id的是添加，带id的是修改")
    @PostMapping("/addOrUpdateStudent")
    public Result addOrUpdateStudent(
            @ApiParam("请求体携带的数据") @RequestBody Student student
    ){

        // 如果是新增学生，密码进行MD5加密
        if(student.getId() == null || student.getId() == 0){
            student.setPassword(MD5.encrypt(student.getPassword()));
        }

        studentService.saveOrUpdate(student);
        return Result.ok();
    }

    //sms/studentController/getStudentByOpr/1/3?name=&className=

    @ApiOperation("根据班级名称、学生姓名模糊查询，带分页")
    @GetMapping("/getStudentByOpr/{pageNo}/{pageSize}")
    public Result getStudent(
            @ApiParam("分页查询的页码数") @PathVariable("pageNo") Integer pageNo,
            @ApiParam("分页查询的页大小") @PathVariable("pageSize") Integer pageSize,
            @ApiParam("分页查询的模糊条件") Student student
    ){
        // 设置分页
        Page<Student> pageParam = new Page<>(pageNo,pageSize);
        // 调用服务层，进行模糊查询
        IPage<Student> page = studentService.getStudentByOpr(pageParam,student);
       return Result.ok(page);
    }
}
