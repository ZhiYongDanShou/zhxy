package com.atguigu.zhxy.controller;

import com.atguigu.zhxy.pojo.Admin;
import com.atguigu.zhxy.pojo.LoginForm;
import com.atguigu.zhxy.pojo.Student;
import com.atguigu.zhxy.pojo.Teacher;
import com.atguigu.zhxy.service.AdminService;
import com.atguigu.zhxy.service.StudentService;
import com.atguigu.zhxy.service.TeacherService;
import com.atguigu.zhxy.util.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.ibatis.jdbc.Null;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 公共功能
 * 非数据库表中数据CRUD功能的 业务模块（获取验证码、校验验证码）
 *
 * @author ysh
 * @create 2022-04-14 16:42
 */
@Api(tags = "系统控制器")
@RestController
@RequestMapping("/sms/system")
public class SystemController {

    @Autowired
    AdminService adminService;

    @Autowired
    TeacherService teacherService;

    @Autowired
    StudentService studentService;

    //sms/system/updatePwd/123456/123456789
    @ApiOperation("修改密码")
    @PostMapping("/updatePwd/{oldPwd}/{newPwd}")
    public Result updatePwd(
            @RequestHeader("token") String token,
            @PathVariable("oldPwd") String oldPwd,
            @PathVariable("newPwd") String newPwd
    ) {

        boolean yon = JwtHelper.isExpiration(token);
        if (yon) {
            // token过期
            return Result.fail().message("token失效!");
        }

        // 通过token获取当前登录的用户的 id
        Long userId = JwtHelper.getUserId(token);
        // 通过token获取当前登录的用户的 userType
        Integer userType = JwtHelper.getUserType(token);

        // 将明文密码转换为密文
        oldPwd = MD5.encrypt(oldPwd);
        newPwd = MD5.encrypt(newPwd);

        // 判断用户类型
        if (userType == 1) {
            QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("id", userId.intValue()).eq("password", oldPwd);
            Admin admin = adminService.getOne(queryWrapper);
            if (admin != null) {
                admin.setPassword(newPwd);
                adminService.saveOrUpdate(admin);
            } else {
                return Result.fail().message("原密码输入有误！");
            }
        } else if (userType == 2) {
            QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("id", userId.intValue()).eq("password", oldPwd);
            Student student = studentService.getOne(queryWrapper);
            if (student != null) {
                student.setPassword(newPwd);
                studentService.saveOrUpdate(student);
            } else {
                return Result.fail().message("原密码输入有误！");
            }
        } else if (userType == 3) {
            QueryWrapper<Teacher> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("id", userId.intValue()).eq("password", oldPwd);
            Teacher teacher = teacherService.getOne(queryWrapper);
            if (teacher != null) {
                teacher.setPassword(newPwd);
                teacherService.saveOrUpdate(teacher);
            } else {
                return Result.fail().message("原密码输入有误！");
            }
        }
        return Result.ok();
    }


    //sms/system/headerImgUpload
    @ApiOperation("文件上传统一入口")
    @PostMapping("/headerImgUpload")
    public Result headerImgUpload(
            @ApiParam("文件二进制数据") @RequestPart("multipartFile") MultipartFile multipartFile
    ) {
        String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
        String originalFilename = multipartFile.getOriginalFilename();
        int i = originalFilename.indexOf(".");
        String newFileName = uuid.concat(originalFilename.substring(i));

        // 保存文件，实际开发中是将文件发送到第三方/独立的图片服务器上
        String portraitPath = "D:/workspace_idea/zhxy/target/classes/public/upload/".concat(newFileName);
        try {
            multipartFile.transferTo(new File(portraitPath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 响应图片路径
        String path = "upload/".concat(newFileName);
        return Result.ok(path);
    }

    @ApiOperation("通过Token获取用户信息")
    @GetMapping("/getInfo")
    public Result getInfoByToken(@RequestHeader("token") String token) {
        // 获取用户请求中的token
        // 检查 token 是否过期 24H
        boolean expiration = JwtHelper.isExpiration(token);
        if (expiration) {
            return Result.build(null, ResultCodeEnum.TOKEN_ERROR);
        }

        // 从 token中解析出  用户id 和用户的类型
        Long userId = JwtHelper.getUserId(token);
        Integer userType = JwtHelper.getUserType(token);

        Map<String, Object> map = new LinkedHashMap<>();
        switch (userType) {
            case 1:
                Admin admin = adminService.getAdminById(userId);
                map.put("userType", 1);
                map.put("user", admin);
                break;
            case 2:
                Student student = studentService.getStudentById(userId);
                map.put("userType", 2);
                map.put("user", student);
                break;
            case 3:
                Teacher teacher = teacherService.getTeacherById(userId);
                map.put("userType", 3);
                map.put("user", teacher);
                break;
        }
        // 根据用户名查询登录用户的信息
        return Result.ok(map);
    }


    @ApiOperation("登录请求验证")
    @PostMapping("/login")
    public Result login(@ApiParam("登录提交信息的form表单") @RequestBody LoginForm loginForm, HttpServletRequest request) {
        // 1、验证码效验
        HttpSession session = request.getSession();
        // ①获取session中的验证码 ②获取请求体中的验证码信息
        String sessionVerfiCode = (String) session.getAttribute("verifiCode");
        String loginFormVerifiCode = loginForm.getVerifiCode();
        // 判断用户输入的是否为空
        if ("".equals(sessionVerfiCode) || null == sessionVerfiCode) {
            return Result.fail().message("验证码失效！请重新输入");
        }
        if (!sessionVerfiCode.equalsIgnoreCase(loginFormVerifiCode)) {
            return Result.fail().message("验证码有误！请小心输入后重试");
        }

        // 2、从session域中移除现有验证码
        session.removeAttribute("verifiCode");

        // 3、分用户类型进行校验
        /**
         * 1 : tb_admin
         * 2 : tb_student
         * 3 : tb_teacher
         */
        // 准备一个map用于存放响应的数据
        Map<String, Object> map = new LinkedHashMap<>();
        switch (loginForm.getUserType()) {
            case 1:
                try {
                    Admin admin = adminService.login(loginForm);
                    if (null != admin) {

                        // 用户的类型和用户的id转换成一个密文，以token的名称向客户端反馈
//                        String token = JwtHelper.createToken(admin.getId().longValue(), 1);
//                        map.put("token",token);
                        map.put("token", JwtHelper.createToken(admin.getId().longValue(), 1));
                    } else {
                        throw new RuntimeException("用户名或密码有误");
                    }
                    return Result.ok(map);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    return Result.fail().message(e.getMessage());
                }
            case 2:
                try {
                    Student student = studentService.login(loginForm);
                    if (null != student) {

                        // 用户的类型和用户的id转换成一个密文，以token的名称向客户端反馈
                        String token = JwtHelper.createToken(student.getId().longValue(), 2);
                        map.put("token", token);
                    } else {
                        throw new RuntimeException("用户名或密码有误");
                    }
                    return Result.ok(map);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    return Result.fail().message(e.getMessage());
                }
            case 3:
                try {
                    Teacher teacher = teacherService.login(loginForm);
                    if (null != teacher) {

                        // 用户的类型和用户的id转换成一个密文，以token的名称向客户端反馈
                        String token = JwtHelper.createToken(teacher.getId().longValue(), 3);
                        map.put("token", token);
                    } else {
                        throw new RuntimeException("用户名或密码有误");
                    }
                    return Result.ok(map);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    return Result.fail().message(e.getMessage());
                }
        }

        return Result.fail().message("查无此人");
    }


    @ApiOperation("获取验证码图片")
    @GetMapping("/getVerifiCodeImage")
    public void getVerifiCodeImage(HttpServletRequest request, HttpServletResponse response) {
        // 获取图片
        BufferedImage verifiCodeImage = CreateVerifiCodeImage.getVerifiCodeImage();

        // 获取图片上的验证码，将 char[] 转换为字符串
        char[] verifiCode = CreateVerifiCodeImage.getVerifiCode();
        String code = String.valueOf(verifiCode);

        // 将验证码文本保存到Session域，为下一次验证做准备
        HttpSession session = request.getSession();
        session.setAttribute("verifiCode",code);

        // 将验证码图片响应给浏览器，图片要以流的方式传输
        try {
            ImageIO.write(verifiCodeImage,"JPEG",response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
