package com.school.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.school.entity.User;
import com.school.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired private UserService userService;

    @GetMapping("/list")
    public List<User> list() {
        return userService.list();
    }

    @GetMapping("/page")
    public Page<User> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String checkedok,
            @RequestParam(required = false) Integer classId) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(User::getUsername, keyword).or().like(User::getUserno, keyword);
        }
        if (checkedok != null && !checkedok.isEmpty()) {
            wrapper.eq(User::getCheckedok, checkedok);
        }
        if (classId != null) {
            wrapper.eq(User::getClassId, classId);
        }
        wrapper.orderByDesc(User::getUid);
        return userService.page(new Page<>(pageNum, pageSize), wrapper);
    }

    @PostMapping("/save") public boolean save(@RequestBody User user) { return userService.saveOrUpdate(user); }
    @DeleteMapping("/delete/{id}") public boolean delete(@PathVariable Integer id) { return userService.removeById(id); }
    
    @PostMapping("/approve/{id}")
    public boolean approve(@PathVariable Integer id) {
        User user = userService.getById(id);
        if (user != null) {
            user.setCheckedok("已通过");
            return userService.updateById(user);
        }
        return false;
    }

    @PostMapping("/reject/{id}")
    public boolean reject(@PathVariable Integer id) {
        User user = userService.getById(id);
        if (user != null) {
            user.setCheckedok("已拒绝");
            return userService.updateById(user);
        }
        return false;
    }
}
