package com.school.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.school.entity.Test;
import com.school.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/test")
public class TestController {
    @Autowired private TestService testService;

    @GetMapping("/list")
    public List<Test> list() {
        return testService.list();
    }

    @GetMapping("/page")
    public Page<Test> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<Test> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Test::getTtitle, keyword).or().like(Test::getTcontent, keyword);
        }
        wrapper.orderByDesc(Test::getTid);
        return testService.page(new Page<>(pageNum, pageSize), wrapper);
    }

    @GetMapping("/search")
    public List<Test> search(@RequestParam String keyword) {
        return testService.lambdaQuery().like(Test::getTtitle, keyword).or().like(Test::getTcontent, keyword).list();
    }

    @PostMapping("/save") public boolean save(@RequestBody Test test) { return testService.saveOrUpdate(test); }
    @DeleteMapping("/delete/{id}") public boolean delete(@PathVariable Integer id) { return testService.removeById(id); }
}
