package com.school.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.school.entity.Interaction;
import com.school.service.InteractionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/interaction")
@CrossOrigin
public class InteractionController {
    @Autowired private InteractionService interactionService;

    @GetMapping("/list")
    public List<Interaction> list() {
        return interactionService.list();
    }

    @GetMapping("/page")
    public Page<Interaction> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String status) {
        LambdaQueryWrapper<Interaction> wrapper = new LambdaQueryWrapper<>();
        if (name != null && !name.isEmpty()) {
            wrapper.eq(Interaction::getName, name);
        }
        if ("replied".equals(status)) {
            wrapper.isNotNull(Interaction::getComrepl);
        } else if ("unreplied".equals(status)) {
            wrapper.isNull(Interaction::getComrepl);
        }
        wrapper.orderByDesc(Interaction::getAsktime);
        return interactionService.page(new Page<>(pageNum, pageSize), wrapper);
    }

    @PostMapping("/ask") public boolean ask(@RequestBody Interaction interaction) {
        interaction.setAsktime(LocalDateTime.now());
        return interactionService.save(interaction);
    }
    @PostMapping("/reply") public boolean reply(@RequestBody Interaction interaction) {
        Interaction existing = interactionService.getById(interaction.getId());
        if (existing != null) {
            existing.setReplname(interaction.getReplname());
            existing.setComrepl(interaction.getComrepl());
            existing.setRepltime(LocalDateTime.now());
            return interactionService.updateById(existing);
        }
        return false;
    }
    @DeleteMapping("/delete/{id}") public boolean delete(@PathVariable Integer id) { return interactionService.removeById(id); }
}
