package com.yimian.system.vo;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class SystemAnnouncementVO {

    private Long id;

    private Long notificationId;

    private String title;

    private String content;

    private Boolean important;

    private Boolean enabled;

    private Long creatorId;

    private String creatorName;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
