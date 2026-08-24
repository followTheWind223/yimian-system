package com.yimian.system.service.impl;

import com.yimian.system.entity.User;
import com.yimian.system.mapper.UserFollowMapper;
import com.yimian.system.mapper.UserMapper;
import com.yimian.system.service.MentionService;
import com.yimian.system.service.NotificationService;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MentionServiceImpl implements MentionService {

    private static final String TYPE_MENTION = "mention";
    private static final int MAX_MUTUAL_FRIENDS_SCAN = 200;
    private static final int SNIPPET_MAX_LENGTH = 120;
    private static final Pattern MENTION_PATTERN =
            Pattern.compile("(?<![\\p{L}\\p{N}_])@([\\p{L}\\p{N}_-]{1,64})");

    private final UserFollowMapper userFollowMapper;
    private final UserMapper userMapper;
    private final NotificationService notificationService;

    @Override
    public void notifyMentions(Long senderId,
                               String content,
                               String targetType,
                               Long targetId,
                               String sourceType,
                               Long sourceId) {
        if (senderId == null || content == null || content.isBlank()) {
            return;
        }

        Set<String> names = extractMentionNames(content);
        if (names.isEmpty()) {
            return;
        }

        List<User> friends = userFollowMapper.selectMutualFollowUsers(senderId, null, MAX_MUTUAL_FRIENDS_SCAN);
        if (friends == null || friends.isEmpty()) {
            return;
        }

        Map<String, User> mentionableByName = new LinkedHashMap<>();
        for (User friend : friends) {
            putName(mentionableByName, friend.getUsername(), friend);
            putName(mentionableByName, friend.getNickname(), friend);
        }

        Set<Long> receiverIds = new LinkedHashSet<>();
        for (String name : names) {
            User friend = mentionableByName.get(normalizeName(name));
            if (friend != null && friend.getId() != null && !friend.getId().equals(senderId)) {
                receiverIds.add(friend.getId());
            }
        }
        if (receiverIds.isEmpty()) {
            return;
        }

        User sender = userMapper.selectById(senderId);
        String senderName = displayName(sender, senderId);
        String sourceLabel = sourceLabel(sourceType);
        String title = senderName + " 在" + sourceLabel + "中 @了你";
        String snippet = buildSnippet(content);
        String extra = buildExtra(sourceType, sourceId, targetType, targetId);

        for (Long receiverId : receiverIds) {
            notificationService.create(TYPE_MENTION, senderId, receiverId,
                    targetType, targetId, title, snippet, extra);
        }
        log.info("Mention notifications created: senderId={}, receivers={}, sourceType={}, sourceId={}",
                senderId, receiverIds.size(), sourceType, sourceId);
    }

    private Set<String> extractMentionNames(String content) {
        Set<String> names = new LinkedHashSet<>();
        Matcher matcher = MENTION_PATTERN.matcher(content);
        while (matcher.find()) {
            String name = matcher.group(1);
            if (name != null && !name.isBlank()) {
                names.add(name.trim());
            }
        }
        return names;
    }

    private void putName(Map<String, User> map, String name, User user) {
        if (name == null || name.isBlank()) {
            return;
        }
        map.putIfAbsent(normalizeName(name), user);
    }

    private String normalizeName(String name) {
        return name.trim().toLowerCase(Locale.ROOT);
    }

    private String displayName(User user, Long userId) {
        if (user == null) {
            return "用户#" + userId;
        }
        if (user.getNickname() != null && !user.getNickname().isBlank()) {
            return user.getNickname();
        }
        if (user.getUsername() != null && !user.getUsername().isBlank()) {
            return user.getUsername();
        }
        return "用户#" + userId;
    }

    private String sourceLabel(String sourceType) {
        if ("comment".equals(sourceType)) {
            return "评论";
        }
        if ("blog".equals(sourceType)) {
            return "博客";
        }
        if ("knowledge".equals(sourceType)) {
            return "题目";
        }
        return "内容";
    }

    private String buildSnippet(String content) {
        String normalized = content.replaceAll("\\s+", " ").trim();
        return normalized.length() > SNIPPET_MAX_LENGTH
                ? normalized.substring(0, SNIPPET_MAX_LENGTH) + "..."
                : normalized;
    }

    private String buildExtra(String sourceType, Long sourceId, String targetType, Long targetId) {
        return "{"
                + "\"sourceType\":\"" + escapeJson(sourceType) + "\","
                + "\"sourceId\":" + jsonLong(sourceId) + ","
                + "\"targetType\":\"" + escapeJson(targetType) + "\","
                + "\"targetId\":" + jsonLong(targetId)
                + "}";
    }

    private String jsonLong(Long value) {
        return value == null ? "null" : "\"" + value + "\"";
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder(value.length() + 8);
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            switch (ch) {
                case '"' -> builder.append("\\\"");
                case '\\' -> builder.append("\\\\");
                case '\b' -> builder.append("\\b");
                case '\f' -> builder.append("\\f");
                case '\n' -> builder.append("\\n");
                case '\r' -> builder.append("\\r");
                case '\t' -> builder.append("\\t");
                default -> builder.append(ch);
            }
        }
        return builder.toString();
    }
}
