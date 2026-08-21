package com.yimian.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yimian.system.common.exception.BusinessException;
import com.yimian.system.common.result.ResultCode;
import com.yimian.system.dto.SystemAnnouncementCreateDto;
import com.yimian.system.dto.SystemAnnouncementQueryDto;
import com.yimian.system.entity.Notification;
import com.yimian.system.entity.SystemAnnouncement;
import com.yimian.system.entity.User;
import com.yimian.system.mapper.NotificationMapper;
import com.yimian.system.mapper.SystemAnnouncementMapper;
import com.yimian.system.mapper.UserMapper;
import com.yimian.system.service.SystemAnnouncementService;
import com.yimian.system.vo.SystemAnnouncementVO;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SystemAnnouncementServiceImpl implements SystemAnnouncementService {

    private static final String NOTIFICATION_TYPE = "system_announcement";
    private static final String TARGET_TYPE = "system_announcement";

    private final SystemAnnouncementMapper announcementMapper;
    private final NotificationMapper notificationMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public SystemAnnouncementVO publish(SystemAnnouncementCreateDto dto, Long creatorId) {
        User creator = userMapper.selectById(creatorId);
        if (creator == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        SystemAnnouncement announcement = new SystemAnnouncement();
        announcement.setTitle(trimRequired(dto.getTitle()));
        announcement.setContent(trimRequired(dto.getContent()));
        announcement.setImportant(Boolean.TRUE.equals(dto.getImportant()));
        announcement.setEnabled(true);
        announcement.setCreatorId(creatorId);
        announcement.setDeleted(0);
        announcementMapper.insert(announcement);

        List<User> users = userMapper.selectList(new LambdaQueryWrapper<User>()
                .eq(User::getStatus, 1));
        for (User user : users) {
            Notification notification = new Notification();
            notification.setType(NOTIFICATION_TYPE);
            notification.setSenderId(creatorId);
            notification.setReceiverId(user.getId());
            notification.setTargetType(TARGET_TYPE);
            notification.setTargetId(announcement.getId());
            notification.setTitle(announcement.getTitle());
            notification.setContent(announcement.getContent());
            notification.setExtra("{\"important\":" + announcement.getImportant() + "}");
            notification.setRead(false);
            notification.setDeleted(0);
            notificationMapper.insert(notification);
        }

        return toVO(announcement, creator, null);
    }

    @Override
    public PageInfo<SystemAnnouncementVO> list(SystemAnnouncementQueryDto query) {
        int page = query.getPage() == null || query.getPage() < 1 ? 1 : query.getPage();
        int size = query.getSize() == null || query.getSize() < 1 ? 10 : Math.min(query.getSize(), 50);

        LambdaQueryWrapper<SystemAnnouncement> wrapper = new LambdaQueryWrapper<>();
        String keyword = trim(query.getKeyword());
        if (keyword != null) {
            wrapper.and(w -> w.like(SystemAnnouncement::getTitle, keyword)
                    .or()
                    .like(SystemAnnouncement::getContent, keyword));
        }
        if (query.getImportant() != null) {
            wrapper.eq(SystemAnnouncement::getImportant, query.getImportant());
        }
        wrapper.orderByDesc(SystemAnnouncement::getCreatedAt);

        PageHelper.startPage(page, size);
        List<SystemAnnouncement> records = announcementMapper.selectList(wrapper);
        PageInfo<SystemAnnouncement> source = new PageInfo<>(records);
        Map<Long, User> creators = loadUsers(records.stream()
                .map(SystemAnnouncement::getCreatorId)
                .collect(Collectors.toList()));

        PageInfo<SystemAnnouncementVO> result = new PageInfo<>();
        result.setList(records.stream()
                .map(item -> toVO(item, creators.get(item.getCreatorId()), null))
                .collect(Collectors.toList()));
        result.setTotal(source.getTotal());
        result.setPageNum(source.getPageNum());
        result.setPageSize(source.getPageSize());
        result.setPages(source.getPages());
        result.setHasNextPage(source.isHasNextPage());
        result.setHasPreviousPage(source.isHasPreviousPage());
        return result;
    }

    @Override
    public List<SystemAnnouncementVO> listUnreadImportant(Long userId) {
        List<Notification> notifications = notificationMapper.selectList(new LambdaQueryWrapper<Notification>()
                .eq(Notification::getReceiverId, userId)
                .eq(Notification::getType, NOTIFICATION_TYPE)
                .eq(Notification::getTargetType, TARGET_TYPE)
                .eq(Notification::getRead, false)
                .orderByDesc(Notification::getCreatedAt));
        if (notifications.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> announcementIds = notifications.stream()
                .map(Notification::getTargetId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());
        if (announcementIds.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, SystemAnnouncement> announcements = announcementMapper.selectBatchIds(announcementIds)
                .stream()
                .filter(item -> Boolean.TRUE.equals(item.getEnabled()))
                .filter(item -> Boolean.TRUE.equals(item.getImportant()))
                .collect(Collectors.toMap(SystemAnnouncement::getId, Function.identity(), (a, b) -> a));

        return notifications.stream()
                .filter(n -> announcements.containsKey(n.getTargetId()))
                .map(n -> toVO(announcements.get(n.getTargetId()), null, n.getId()))
                .sorted(Comparator.comparing(SystemAnnouncementVO::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void confirm(Long announcementId, Long userId) {
        Notification notification = notificationMapper.selectOne(new LambdaQueryWrapper<Notification>()
                .eq(Notification::getReceiverId, userId)
                .eq(Notification::getType, NOTIFICATION_TYPE)
                .eq(Notification::getTargetType, TARGET_TYPE)
                .eq(Notification::getTargetId, announcementId)
                .last("LIMIT 1"));
        if (notification == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "公告通知不存在");
        }
        Notification update = new Notification();
        update.setId(notification.getId());
        update.setRead(true);
        notificationMapper.updateById(update);
    }

    private SystemAnnouncementVO toVO(SystemAnnouncement announcement, User creator, Long notificationId) {
        SystemAnnouncementVO vo = new SystemAnnouncementVO();
        vo.setId(announcement.getId());
        vo.setNotificationId(notificationId);
        vo.setTitle(announcement.getTitle());
        vo.setContent(announcement.getContent());
        vo.setImportant(announcement.getImportant());
        vo.setEnabled(announcement.getEnabled());
        vo.setCreatorId(announcement.getCreatorId());
        vo.setCreatorName(displayName(creator));
        vo.setCreatedAt(announcement.getCreatedAt());
        vo.setUpdatedAt(announcement.getUpdatedAt());
        return vo;
    }

    private Map<Long, User> loadUsers(List<Long> ids) {
        List<Long> cleanIds = ids.stream()
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());
        if (cleanIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return userMapper.selectBatchIds(cleanIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity(), (a, b) -> a));
    }

    private String displayName(User user) {
        if (user == null) {
            return null;
        }
        if (user.getNickname() != null && !user.getNickname().isBlank()) {
            return user.getNickname();
        }
        return user.getUsername();
    }

    private String trimRequired(String value) {
        String trimmed = trim(value);
        if (trimmed == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "公告标题和内容不能为空");
        }
        return trimmed;
    }

    private String trim(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
