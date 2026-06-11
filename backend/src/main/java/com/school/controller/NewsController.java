package com.school.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.school.entity.News;
import com.school.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/news")
public class NewsController {
    @Autowired private NewsService newsService;

    @GetMapping("/list")
    public List<News> list() {
        return newsService.list();
    }

    @GetMapping("/page")
    public Page<News> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        LambdaQueryWrapper<News> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(News::getNewsdate);
        return newsService.page(new Page<>(pageNum, pageSize), wrapper);
    }

    @PostMapping("/save") public boolean save(@RequestBody News news) {
        if (news.getNewsdate() == null) news.setNewsdate(LocalDateTime.now());
        return newsService.saveOrUpdate(news);
    }
    @DeleteMapping("/delete/{id}") public boolean delete(@PathVariable Integer id) { return newsService.removeById(id); }
}
