package com.school.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.school.entity.Teacher;
import com.school.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/teacher")
@CrossOrigin
public class TeacherController {
    @Autowired private TeacherService teacherService;

    @GetMapping("/list")
    public List<Teacher> list() {
        return teacherService.list();
    }

    @GetMapping("/page")
    public Page<Teacher> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<Teacher> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Teacher::getTname, keyword).or().like(Teacher::getTno, keyword);
        }
        wrapper.orderByDesc(Teacher::getTid);
        return teacherService.page(new Page<>(pageNum, pageSize), wrapper);
    }

    @PostMapping("/save") public boolean save(@RequestBody Teacher teacher) { return teacherService.saveOrUpdate(teacher); }
    @DeleteMapping("/delete/{id}") public boolean delete(@PathVariable Integer id) { return teacherService.removeById(id); }
}
