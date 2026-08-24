package com.yimian.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yimian.system.common.exception.BusinessException;
import com.yimian.system.common.result.ResultCode;
import com.yimian.system.dto.CommentCreateDto;
import com.yimian.system.dto.CommentQueryDto;
import com.yimian.system.entity.Blog;
import com.yimian.system.entity.Comment;
import com.yimian.system.entity.CommentLike;
import com.yimian.system.entity.Knowledge;
import com.yimian.system.entity.User;
import com.yimian.system.mapper.BlogMapper;
import com.yimian.system.mapper.CommentLikeMapper;
import com.yimian.system.mapper.CommentMapper;
import com.yimian.system.mapper.KnowledgeMapper;
import com.yimian.system.mapper.UserMapper;
import com.yimian.system.service.CommentService;
import com.yimian.system.service.HotDataService;
import com.yimian.system.service.MentionService;
import com.yimian.system.service.NotificationService;
import com.yimian.system.vo.CommentLikeVO;
import com.yimian.system.vo.CommentVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private static final String TARGET_KNOWLEDGE = "knowledge";
    private static final String TARGET_BLOG = "blog";
    private static final String SORT_NEWEST = "newest";
    private static final int STATUS_NORMAL = 1;

    private final CommentMapper commentMapper;
    private final CommentLikeMapper commentLikeMapper;
    private final UserMapper userMapper;
    private final KnowledgeMapper knowledgeMapper;
    private final BlogMapper blogMapper;
    private final HotDataService hotDataService;
    private final NotificationService notificationService;
    private final MentionService mentionService;

    @Override
    public PageInfo<CommentVO> list(CommentQueryDto query, Long currentUserId) {
        String targetType = normalizeTargetType(query.getTargetType());
        Long targetId = query.getTargetId();
        if (targetId == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "targetId is required");
        }
        validateTarget(targetType, targetId);

        int pageNum = query.getPage() != null && query.getPage() > 0 ? query.getPage() : 1;
        int pageSize = query.getSize() != null && query.getSize() > 0 ? Math.min(query.getSize(), 50) : 20;
        String sort = SORT_NEWEST.equals(query.getSort()) ? SORT_NEWEST : "hot";

        PageHelper.startPage(pageNum, pageSize);
        List<Comment> roots = commentMapper.selectRootComments(targetType, targetId, sort);
        PageInfo<Comment> sourcePage = new PageInfo<>(roots);

        List<Comment> replies = roots.isEmpty()
                ? Collections.emptyList()
                : commentMapper.selectRepliesByParentIds(roots.stream().map(Comment::getId).collect(Collectors.toList()));

        Map<Long, List<Comment>> repliesByParent = replies.stream()
                .collect(Collectors.groupingBy(Comment::getParentId));
        Map<Long, User> users = loadUsers(roots, replies);
        Set<Long> likedIds = loadLikedCommentIds(currentUserId, roots, replies);

        List<CommentVO> list = roots.stream().map(root -> {
            CommentVO vo = toVO(root, users, likedIds);
            List<CommentVO> replyVOs = repliesByParent.getOrDefault(root.getId(), Collections.emptyList())
                    .stream()
                    .map(reply -> toVO(reply, users, likedIds))
                    .collect(Collectors.toList());
            vo.setReplies(replyVOs);
            return vo;
        }).collect(Collectors.toList());

        PageInfo<CommentVO> targetPage = new PageInfo<>();
        targetPage.setList(list);
        targetPage.setTotal(commentMapper.countByTarget(targetType, targetId));
        targetPage.setPageNum(sourcePage.getPageNum());
        targetPage.setPageSize(sourcePage.getPageSize());
        targetPage.setPages(sourcePage.getPages());
        targetPage.setHasNextPage(sourcePage.isHasNextPage());
        targetPage.setHasPreviousPage(sourcePage.isHasPreviousPage());
        return targetPage;
    }

    @Override
    @Transactional
    public CommentVO create(CommentCreateDto dto, Long userId) {
        String targetType = normalizeTargetType(dto.getTargetType());
        Long targetId = dto.getTargetId();
        validateTarget(targetType, targetId);

        String content = dto.getContent() == null ? "" : dto.getContent().trim();
        if (content.isEmpty()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "content is required");
        }

        Long parentId = null;
        Long replyToCommentId = dto.getReplyToCommentId();
        Long replyToUserId = dto.getReplyToUserId();
        if (dto.getParentId() != null || replyToCommentId != null) {
            Comment parent = commentMapper.selectById(dto.getParentId() != null ? dto.getParentId() : replyToCommentId);
            if (parent == null || Integer.valueOf(1).equals(parent.getDeleted())
                    || !targetType.equals(parent.getTargetType()) || !targetId.equals(parent.getTargetId())) {
                throw new BusinessException(ResultCode.COMMENT_NOT_FOUND);
            }
            parentId = parent.getParentId() != null ? parent.getParentId() : parent.getId();

            if (replyToCommentId == null) {
                replyToCommentId = parent.getId();
            }
            Comment replyTo = commentMapper.selectById(replyToCommentId);
            if (replyTo == null || Integer.valueOf(1).equals(replyTo.getDeleted())
                    || !targetType.equals(replyTo.getTargetType()) || !targetId.equals(replyTo.getTargetId())) {
                throw new BusinessException(ResultCode.COMMENT_NOT_FOUND);
            }
            if (replyToUserId == null) {
                replyToUserId = replyTo.getAuthorId();
            }
        }

        Comment comment = new Comment();
        comment.setTargetType(targetType);
        comment.setTargetId(targetId);
        comment.setParentId(parentId);
        comment.setReplyToCommentId(replyToCommentId);
        comment.setReplyToUserId(replyToUserId);
        comment.setAuthorId(userId);
        comment.setContent(content);
        comment.setLikeCount(0);
        comment.setReplyCount(0);
        comment.setStatus(STATUS_NORMAL);
        comment.setDeleted(0);
        commentMapper.insert(comment);

        if (parentId != null) {
            commentMapper.updateReplyCount(parentId, 1);
        }
        updateTargetCommentCount(targetType, targetId, 1);

        User author = userMapper.selectById(userId);
        Map<Long, User> users = new HashMap<>();
        if (author != null) {
            users.put(author.getId(), author);
        }
        if (replyToUserId != null) {
            User replyUser = userMapper.selectById(replyToUserId);
            if (replyUser != null) {
                users.put(replyUser.getId(), replyUser);
            }
        }

        log.info("Comment created: id={}, targetType={}, targetId={}, userId={}",
                comment.getId(), targetType, targetId, userId);

        // Send notification for reply
        if (replyToUserId != null && !replyToUserId.equals(userId)) {
            String targetLabel = TARGET_KNOWLEDGE.equals(targetType) ? ":题目" : ":博客";
            String displayName = author != null ? displayName(author) : String.valueOf(userId);
            String title = displayName + " 回复了你在" + targetLabel + "中的评论";
            String cnt = content.length() > 100 ? content.substring(0, 100) + "..." : content;
            notificationService.create(
                    "comment_reply", userId, replyToUserId,
                    targetType, targetId, title, cnt, null);
        }

        mentionService.notifyMentions(userId, content, targetType, targetId, "comment", comment.getId());

        return toVO(comment, users, Collections.emptySet());
    }

    @Override
    @Transactional
    public CommentLikeVO toggleLike(Long id, Long userId) {
        Comment comment = commentMapper.selectById(id);
        if (comment == null || Integer.valueOf(1).equals(comment.getDeleted())) {
            throw new BusinessException(ResultCode.COMMENT_NOT_FOUND);
        }

        boolean liked;
        CommentLike active = commentLikeMapper.selectActive(id, userId);
        if (active != null) {
            commentLikeMapper.softDelete(id, userId);
            commentMapper.updateLikeCount(id, -1);
            liked = false;
        } else {
            CommentLike any = commentLikeMapper.selectAny(id, userId);
            if (any != null) {
                commentLikeMapper.restore(any.getId());
            } else {
                CommentLike like = new CommentLike();
                like.setCommentId(id);
                like.setUserId(userId);
                like.setDeleted(0);
                commentLikeMapper.insert(like);
            }
            commentMapper.updateLikeCount(id, 1);
            liked = true;
        }

        Comment updated = commentMapper.selectById(id);
        CommentLikeVO vo = new CommentLikeVO();
        vo.setLikeCount(updated.getLikeCount() != null ? updated.getLikeCount() : 0);
        vo.setLiked(liked);

        // Send notification for comment like
        if (liked && !comment.getAuthorId().equals(userId)) {
            User liker = userMapper.selectById(userId);
            String displayName = liker != null ? displayName(liker) : String.valueOf(userId);
            String targetLabel = TARGET_KNOWLEDGE.equals(comment.getTargetType()) ? ":题目" : ":博客";
            String title = displayName + " 赞了你在" + targetLabel + "中的评论";
            String cnt = comment.getContent() != null
                    ? (comment.getContent().length() > 100 ? comment.getContent().substring(0, 100) + "..." : comment.getContent())
                    : null;
            notificationService.create(
                    "comment_like", userId, comment.getAuthorId(),
                    comment.getTargetType(), comment.getTargetId(), title, cnt, null);
        }

        return vo;
    }

    private String normalizeTargetType(String targetType) {
        if (targetType == null || targetType.isBlank()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "targetType is required");
        }
        String value = targetType.trim().toLowerCase();
        if ("collection".equals(value)) {
            value = "folder";
        }
        if (!TARGET_KNOWLEDGE.equals(value) && !TARGET_BLOG.equals(value)) {
            throw new BusinessException(ResultCode.COMMENT_TARGET_INVALID);
        }
        return value;
    }

    private void validateTarget(String targetType, Long targetId) {
        if (targetId == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "targetId is required");
        }
        if (TARGET_KNOWLEDGE.equals(targetType)) {
            Knowledge knowledge = knowledgeMapper.selectById(targetId);
            if (knowledge == null || Integer.valueOf(1).equals(knowledge.getDeleted())
                    || !Integer.valueOf(1).equals(knowledge.getStatus())) {
                throw new BusinessException(ResultCode.COMMENT_TARGET_INVALID);
            }
            return;
        }
        Blog blog = blogMapper.selectById(targetId);
        if (blog == null || Integer.valueOf(1).equals(blog.getDeleted())
                || !Integer.valueOf(1).equals(blog.getStatus())) {
            throw new BusinessException(ResultCode.COMMENT_TARGET_INVALID);
        }
    }

    private void updateTargetCommentCount(String targetType, Long targetId, int delta) {
        if (TARGET_KNOWLEDGE.equals(targetType)) {
            knowledgeMapper.updateCommentCount(targetId, delta);
            if (delta > 0) {
                hotDataService.incrComment(targetId);
            }
        } else if (TARGET_BLOG.equals(targetType)) {
            blogMapper.updateCommentCount(targetId, delta);
        }
    }

    private Map<Long, User> loadUsers(List<Comment> roots, List<Comment> replies) {
        Set<Long> userIds = new HashSet<>();
        collectUserIds(roots, userIds);
        collectUserIds(replies, userIds);
        if (userIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, user -> user, (a, b) -> a));
    }

    private void collectUserIds(List<Comment> comments, Set<Long> userIds) {
        for (Comment comment : comments) {
            if (comment.getAuthorId() != null) {
                userIds.add(comment.getAuthorId());
            }
            if (comment.getReplyToUserId() != null) {
                userIds.add(comment.getReplyToUserId());
            }
        }
    }

    private Set<Long> loadLikedCommentIds(Long currentUserId, List<Comment> roots, List<Comment> replies) {
        if (currentUserId == null) {
            return Collections.emptySet();
        }
        List<Long> ids = new ArrayList<>();
        roots.forEach(comment -> ids.add(comment.getId()));
        replies.forEach(comment -> ids.add(comment.getId()));
        if (ids.isEmpty()) {
            return Collections.emptySet();
        }
        return new HashSet<>(commentLikeMapper.selectLikedCommentIds(currentUserId, ids));
    }

    private CommentVO toVO(Comment comment, Map<Long, User> users, Set<Long> likedIds) {
        CommentVO vo = new CommentVO();
        vo.setId(comment.getId());
        vo.setTargetType(comment.getTargetType());
        vo.setTargetId(comment.getTargetId());
        vo.setParentId(comment.getParentId());
        vo.setReplyToCommentId(comment.getReplyToCommentId());
        vo.setReplyToUserId(comment.getReplyToUserId());
        vo.setAuthorId(comment.getAuthorId());
        vo.setContent(comment.getContent());
        vo.setLikeCount(comment.getLikeCount() != null ? comment.getLikeCount() : 0);
        vo.setReplyCount(comment.getReplyCount() != null ? comment.getReplyCount() : 0);
        vo.setStatus(comment.getStatus());
        vo.setLiked(likedIds.contains(comment.getId()));
        vo.setCreatedAt(comment.getCreatedAt());
        vo.setUpdatedAt(comment.getUpdatedAt());
        vo.setReplies(Collections.emptyList());

        User author = users.get(comment.getAuthorId());
        if (author != null) {
            vo.setAuthorNickname(displayName(author));
            vo.setAuthorAvatar(author.getAvatar());
        }
        User replyToUser = users.get(comment.getReplyToUserId());
        if (replyToUser != null) {
            vo.setReplyToNickname(displayName(replyToUser));
        }
        return vo;
    }

    private String displayName(User user) {
        if (user.getNickname() != null && !user.getNickname().isBlank()) {
            return user.getNickname();
        }
        return user.getUsername();
    }
}



