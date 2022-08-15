package com.atguigu.zhxy.controller;

import com.atguigu.zhxy.pojo.Grade;
import com.atguigu.zhxy.service.GradeService;
import com.atguigu.zhxy.util.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * @author ysh
 * @create 2022-04-14 16:36
 */
@Api(tags = "年级控制器")
@RestController
@RequestMapping("/sms/gradeController")
public class GradeController {

    @Autowired
    GradeService gradeService;

    //sms/gradeController/getGrades
    @ApiOperation("查询所有的年级")
    @GetMapping("/getGrades")
    public Result getGrades(){
        List<Grade> grades = gradeService.getGrades();
        return Result.ok(grades);
    }


    // /sms/gradeController/deleteGrade
    @ApiOperation("删除Grade信息")
    @DeleteMapping("/deleteGrade")
    public Result deleteGrade(
            @ApiParam("要删除的所有的grade的id的JSON集合") @RequestBody List<Integer> ids // 接收参数
    ){
        // 调用服务层，实现删除和批量删除
        gradeService.removeByIds(ids);
        return Result.ok();
    }

    // /sms/gradeController/saveOrUpdateGrade
    @ApiOperation("新增或修改Grade，有id属性是修改，没有id属性是增加")
    @PostMapping("/saveOrUpdateGrade")
    public Result saveOrUpdateGrade(
            @ApiParam("JSON格式的Grade对象")
            @RequestBody Grade grade    // 接收参数，因为请求体中是以json格式放过来的
    ){
        // 调用服务层实现 增加 或修改
        gradeService.saveOrUpdate(grade);
        return Result.ok();
    }


    // /sms/gradeController/getGrade/1/3?gradeName=%E4%B8%89
    @ApiOperation("根据年级名称模糊查询，带分页")
    @GetMapping("/getGrades/{pageNo}/{pageSize}")
    public Result getGradeByOpr(
            @ApiParam("分页查询的页码数") @PathVariable("pageNo") Integer pageNo,
            @ApiParam("分页查询的页大小") @PathVariable("pageSize") Integer pageSize,
            @ApiParam("分页查询，模糊匹配的名称") String gradeName
    ){

        // 分页设置
        Page<Grade> pageParam = new Page<>(pageNo,pageSize);
        // 调用service层，进行模糊查询
        IPage<Grade> pageRs  = gradeService.getGradeByOpr(pageParam,gradeName);

        // 封装Result对象，并返回
        return Result.ok(pageRs);
    }
}
