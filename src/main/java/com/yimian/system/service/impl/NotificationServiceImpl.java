package com.yimian.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yimian.system.common.exception.BusinessException;
import com.yimian.system.common.result.ResultCode;
import com.yimian.system.dto.NotificationQueryDto;
import com.yimian.system.entity.Notification;
import com.yimian.system.entity.User;
import com.yimian.system.mapper.NotificationMapper;
import com.yimian.system.mapper.UserMapper;
import com.yimian.system.service.NotificationService;
import com.yimian.system.vo.NotificationVO;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationMapper notificationMapper;
    private final UserMapper userMapper;

    @Override
    public PageInfo<NotificationVO> list(NotificationQueryDto query, Long currentUserId) {
        int pageNum = query.getPage() != null && query.getPage() > 0 ? query.getPage() : 1;
        int pageSize = query.getSize() != null && query.getSize() > 0 ? Math.min(query.getSize(), 50) : 20;

        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<Notification>()
                .eq(Notification::getReceiverId, currentUserId);

        if (query.getType() != null && !query.getType().isBlank()) {
            wrapper.eq(Notification::getType, query.getType().trim());
        }
        if (query.getRead() != null) {
            wrapper.eq(Notification::getRead, query.getRead());
        }

        wrapper.orderByDesc(Notification::getCreatedAt);

        PageHelper.startPage(pageNum, pageSize);
        List<Notification> list = notificationMapper.selectList(wrapper);
        PageInfo<Notification> pageInfo = new PageInfo<>(list);

        Map<Long, User> senders = loadSenders(list);

        List<NotificationVO> voList = list.stream().map(n -> toVO(n, senders)).collect(Collectors.toList());

        PageInfo<NotificationVO> result = new PageInfo<>();
        result.setList(voList);
        result.setTotal(pageInfo.getTotal());
        result.setPageNum(pageInfo.getPageNum());
        result.setPageSize(pageInfo.getPageSize());
        result.setPages(pageInfo.getPages());
        result.setHasNextPage(pageInfo.isHasNextPage());
        result.setHasPreviousPage(pageInfo.isHasPreviousPage());
        return result;
    }

    @Override
    public int countUnread(Long userId) {
        Long count = notificationMapper.selectCount(
                new LambdaQueryWrapper<Notification>()
                        .eq(Notification::getReceiverId, userId)
                        .eq(Notification::getRead, false));
        return count != null ? count.intValue() : 0;

    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationMapper.selectById(notificationId);
        if (notification == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "Notification not found");
        }
        if (!notification.getReceiverId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        Notification update = new Notification();
        update.setId(notificationId);
        update.setRead(true);
        notificationMapper.updateById(update);
    }

    @Override
    @Transactional
    public void markAllAsRead(Long userId) {
        Notification update = new Notification();
        update.setRead(true);
        notificationMapper.update(update,
                new LambdaQueryWrapper<Notification>()
                        .eq(Notification::getReceiverId, userId)
                        .eq(Notification::getRead, false));
    }

    @Override
    @Transactional
    public void delete(Long notificationId, Long userId) {
        Notification notification = notificationMapper.selectById(notificationId);
        if (notification == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "Notification not found");
        }
        if (!notification.getReceiverId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        notificationMapper.deleteById(notificationId);
    }

    @Override
    @Transactional
    public void create(String type, Long senderId, Long receiverId,
                       String targetType, Long targetId,
                       String title, String content, String extra) {
        if (receiverId == null || receiverId.equals(senderId)) {
            return;
        }
        Notification notification = new Notification();
        notification.setType(type);
        notification.setSenderId(senderId);
        notification.setReceiverId(receiverId);
        notification.setTargetType(targetType);
        notification.setTargetId(targetId);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setExtra(extra);
        notification.setRead(false);
        notificationMapper.insert(notification);
    }

    private Map<Long, User> loadSenders(List<Notification> list) {
        List<Long> ids = list.stream()
                .map(Notification::getSenderId)
                .filter(id -> id != null)
                .collect(Collectors.toList());
        if (ids.isEmpty()) {
            return Collections.emptyMap();
        }
        return userMapper.selectBatchIds(ids).stream()
                .collect(Collectors.toMap(User::getId, u -> u, (a, b) -> a));
    }

    private NotificationVO toVO(Notification n, Map<Long, User> senders) {
        NotificationVO vo = new NotificationVO();
        vo.setId(n.getId());
        vo.setType(n.getType());
        vo.setSenderId(n.getSenderId());
        vo.setReceiverId(n.getReceiverId());
        vo.setTargetType(n.getTargetType());
        vo.setTargetId(n.getTargetId());
        vo.setTitle(n.getTitle());
        vo.setContent(n.getContent());
        vo.setExtra(n.getExtra());
        vo.setRead(n.getRead());
        vo.setCreatedAt(n.getCreatedAt());

        User sender = senders.get(n.getSenderId());
        if (sender != null) {
            String displayName = sender.getNickname() != null && !sender.getNickname().isBlank()
                    ? sender.getNickname() : sender.getUsername();
            vo.setSenderNickname(displayName);
            vo.setSenderAvatar(sender.getAvatar());
        }
        return vo;
    }
}
