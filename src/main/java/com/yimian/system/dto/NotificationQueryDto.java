package com.yimian.system.dto;

import lombok.Data;

@Data
public class NotificationQueryDto {

    private String type;
    private Boolean read;
    private Integer page = 1;
    private Integer size = 20;
}
