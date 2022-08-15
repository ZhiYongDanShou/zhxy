package com.atguigu.zhxy.controller;

import com.atguigu.zhxy.pojo.Clazz;
import com.atguigu.zhxy.service.ClazzService;
import com.atguigu.zhxy.util.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author ysh
 * @create 2022-04-14 16:36
 */
@Api(tags = "班级控制器")
@RestController
@RequestMapping("/sms/clazzController")
public class ClazzController {

    @Autowired
    ClazzService clazzService;

    //sms/clazzController/getClazzs
    @ApiOperation("查询所有的班级")
    @GetMapping("/getClazzs")
    public Result getClazz(){
        List<Clazz> clazzes = clazzService.getClazzs();
        return Result.ok(clazzes);
    }


    //sms/clazzController/deleteClazz

    @ApiOperation("单条删除和批量删除班级，一个id的是单条删除，iid集合的是批量删除")
    @DeleteMapping("/deleteClazz")
    public Result deleteClazz(
            @ApiParam("要删除的id")
            List<Integer> ids){
        clazzService.removeByIds(ids);
        return Result.ok();
    }

    //sms/clazzController/saveOrUpdateClazz

    @ApiOperation("增加和修改班级，不带id参数的是添加，带id参数的是修改")
    @PostMapping("/saveOrUpdateClazz")
    public Result saveOrUpdateClazz(
            @ApiParam("请求体中JSON格式数据")
            @RequestBody  Clazz clazz) {
        clazzService.saveOrUpdate(clazz);
        return Result.ok();
    }


    //sms/clazzController/getClazzsByOpr/1/3?gradeName=%...%&name=%

    @ApiOperation("根据年级、班级名称模糊查询，带分页")
    @GetMapping("/getClazzsByOpr/{pageNo}/{pageSize}")
    public Result getClazzsByOpr(
            @ApiParam("分页查询的页码数") @PathVariable("pageNo") Integer pageNo,
            @ApiParam("分页查询的页大小") @PathVariable("pageSize") Integer pageSize,
            @ApiParam("模糊查询的查询条件") Clazz clazz
    ) {
        // 分页设置
        Page<Clazz> pageParam = new Page<>(pageNo, pageSize);
        // 调用service实现 模糊查询
        IPage<Clazz> clazzes = clazzService.getClazzByOpr(pageParam, clazz);

        return Result.ok(clazzes);
    }
}
