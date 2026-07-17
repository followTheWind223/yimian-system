package com.yimian.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class BlogCreateDto {

    @NotBlank(message = "博客标题不能为空")
    @Size(max = 200, message = "博客标题不能超过200字")
    private String title;

    @NotBlank(message = "博客正文不能为空")
    private String content;

    @Size(max = 500, message = "摘要不能超过500字")
    private String summary;

    /** 博客图片URL列表，至多9张 */
    private List<String> images;

    /** 0=草稿，1=发布 */
    private Integer status;

    /** knowledge / folder / null */
    private String refType;

    private Long refId;

    /** 话题ID列表 */
    private List<Long> topicIds;
}
