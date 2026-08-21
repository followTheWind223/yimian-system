package com.yimian.system.dto;

import lombok.Data;

@Data
public class SystemAnnouncementQueryDto {

    private Integer page = 1;

    private Integer size = 10;

    private String keyword;

    private Boolean important;
}
