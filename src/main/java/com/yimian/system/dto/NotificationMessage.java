package com.yimian.system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessage implements Serializable {

    private String type;
    private Long senderId;
    private Long receiverId;
    private String targetType;
    private Long targetId;
    private String title;
    private String content;
    private String extra;
}
