package com.school.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.school.entity.Course;
import com.school.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/course")
public class CourseController {
    @Autowired private CourseService courseService;

    @GetMapping("/list")
    public List<Course> list() {
        return courseService.list();
    }

    @GetMapping("/page")
    public Page<Course> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Course::getCtitle, keyword).or().like(Course::getCcontent, keyword);
        }
        wrapper.orderByDesc(Course::getCid);
        return courseService.page(new Page<>(pageNum, pageSize), wrapper);
    }

    @PostMapping("/save") public boolean save(@RequestBody Course course) { return courseService.saveOrUpdate(course); }
    @DeleteMapping("/delete/{id}") public boolean delete(@PathVariable Integer id) { return courseService.removeById(id); }
}
