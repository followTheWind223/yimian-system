package com.yimian.system.common.cache;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yimian.system.entity.LogModule;
import com.yimian.system.mapper.LogModuleMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 日志模块字典缓存（内存，避免每次翻译 query DB）
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LogModuleCache {

    private final LogModuleMapper logModuleMapper;

    private volatile Map<String, LogModule> cache = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        refresh();
    }

    public void refresh() {
        List<LogModule> all = logModuleMapper.selectList(
                new LambdaQueryWrapper<LogModule>()
                        .eq(LogModule::getEnabled, 1)
                        .orderByAsc(LogModule::getSort));
        Map<String, LogModule> map = new ConcurrentHashMap<>();
        for (LogModule m : all) {
            map.put(m.getCode(), m);
        }
        this.cache = map;
        log.info("日志模块字典缓存刷新完成，共 {} 条", all.size());
    }

    /** code → name 翻译，未命中返回 code 本身 */
    public String getModuleName(String code) {
        if (code == null) return null;
        LogModule m = cache.get(code);
        return m != null ? m.getName() : code;
    }

    /** 获取全部启用的模块（按 sort 排序） */
    public List<LogModule> getEnabledModules() {
        return cache.values().stream()
                .sorted((a, b) -> {
                    int s1 = a.getSort() != null ? a.getSort() : 0;
                    int s2 = b.getSort() != null ? b.getSort() : 0;
                    return Integer.compare(s1, s2);
                })
                .toList();
    }
}
