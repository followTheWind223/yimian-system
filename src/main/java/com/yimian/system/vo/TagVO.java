package com.yimian.system.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 标签 VO
 */
@Data
public class TagVO {

    private Long id;
    private String name;
    private String color;
    private Integer sort;

    /** 关联知识数量（列表接口实时统计） */
    private Integer usageCount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /** 标签详情：该标签下的知识列表 */
    private List<KnowledgeVO> knowledges;
}
