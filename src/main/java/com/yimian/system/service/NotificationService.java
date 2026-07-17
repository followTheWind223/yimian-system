package com.yimian.system.service;

import com.github.pagehelper.PageInfo;
import com.yimian.system.dto.NotificationQueryDto;
import com.yimian.system.vo.NotificationVO;

public interface NotificationService {

    PageInfo<NotificationVO> list(NotificationQueryDto query, Long currentUserId);

    int countUnread(Long userId);

    void markAsRead(Long notificationId, Long userId);

    void markAllAsRead(Long userId);

    void delete(Long notificationId, Long userId);

    /**
     * 创建通知（内部调用）
     */
    void create(String type, Long senderId, Long receiverId,
                String targetType, Long targetId,
                String title, String content, String extra);
}
