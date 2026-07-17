package com.yimian.system.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationVO {

    private Long id;
    private String type;
    private Long senderId;
    private String senderNickname;
    private String senderAvatar;
    private Long receiverId;
    private String targetType;
    private Long targetId;
    private String title;
    private String content;
    private String extra;
    private Boolean read;
    private LocalDateTime createdAt;
}
