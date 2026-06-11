package com.school.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.school.entity.Experiment;
import com.school.service.ExperimentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/experiment")
public class ExperimentController {
    @Autowired private ExperimentService experimentService;

    @GetMapping("/list")
    public List<Experiment> list() {
        return experimentService.list();
    }

    @GetMapping("/page")
    public Page<Experiment> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<Experiment> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Experiment::getEtitle, keyword).or().like(Experiment::getEcontent, keyword);
        }
        wrapper.orderByDesc(Experiment::getEid);
        return experimentService.page(new Page<>(pageNum, pageSize), wrapper);
    }

    @PostMapping("/save") public boolean save(@RequestBody Experiment experiment) { return experimentService.saveOrUpdate(experiment); }
    @DeleteMapping("/delete/{id}") public boolean delete(@PathVariable Integer id) { return experimentService.removeById(id); }
}
