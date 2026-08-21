package com.yimian.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yimian.system.common.exception.BusinessException;
import com.yimian.system.common.result.ResultCode;
import com.yimian.system.dto.BlogCreateDto;
import com.yimian.system.dto.BlogQueryDto;
import com.yimian.system.dto.BlogUpdateDto;
import com.yimian.system.entity.*;
import com.yimian.system.mapper.*;
import com.yimian.system.service.BlogService;
import com.yimian.system.service.HotDataService;
import com.yimian.system.vo.BlogVO;
import com.yimian.system.vo.BlogVO.RefVO;
import com.yimian.system.vo.BlogVO.TopicSimple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlogServiceImpl implements BlogService {

    private static final int STATUS_DRAFT = 0;
    private static final int STATUS_PUBLISHED = 1;
    private static final int MAX_IMAGES = 9;
    private static final String ITEM_TYPE_BLOG = "blog";

    private final BlogMapper blogMapper;
    private final UserMapper userMapper;
    private final KnowledgeMapper knowledgeMapper;
    private final FavoriteFolderMapper favoriteFolderMapper;
    private final BlogImageMapper blogImageMapper;
    private final BlogTopicMapper blogTopicMapper;
    private final TopicMapper topicMapper;
    private final BlogLikeMapper blogLikeMapper;
    private final BlogCollectMapper blogCollectMapper;
    private final FavoriteItemMapper favoriteItemMapper;
    private final HotDataService hotDataService;

    @Override
    @Transactional
    public BlogVO create(BlogCreateDto dto, Long userId) {
        String refType = normalizeRefType(dto.getRefType());
        validateRef(refType, dto.getRefId(), userId);

        Blog blog = new Blog();
        blog.setTitle(requireText(dto.getTitle(), "博客标题不能为空"));
        blog.setContent(requireText(dto.getContent(), "博客正文不能为空"));
        blog.setSummary(resolveSummary(dto.getSummary(), dto.getContent()));
        blog.setAuthorId(userId);
        blog.setStatus(normalizeStatus(dto.getStatus()));
        blog.setIsPinned(0);
        blog.setViewCount(0);
        blog.setLikeCount(0);
        blog.setCommentCount(0);
        blog.setRefType(refType);
        blog.setRefId(refType == null ? null : dto.getRefId());
        blog.setTopicIds(joinTopicIds(dto.getTopicIds()));
        blog.setDeleted(0);
        if (Integer.valueOf(STATUS_PUBLISHED).equals(blog.getStatus())) {
            blog.setPublishedAt(LocalDateTime.now());
        }
        blogMapper.insert(blog);

        // 保存图片
        saveImages(blog.getId(), dto.getImages());

        // 保存话题关联；只有已发布博客计入话题热度。
        List<Long> topicIds = normalizeTopicIds(dto.getTopicIds());
        saveTopicRelations(blog.getId(), topicIds);
        syncTopicUsage(Collections.emptyList(), topicIds, false, isPublished(blog));

        log.info("Blog created: id={}, userId={}, status={}", blog.getId(), userId, blog.getStatus());
        return toVO(blog);
    }

    @Override
    @Transactional
    public BlogVO update(Long id, BlogUpdateDto dto, Long userId) {
        Blog blog = getOwnedBlog(id, userId);
        boolean wasPublished = isPublished(blog);
        List<Long> previousTopicIds = normalizeTopicIds(blogTopicMapper.selectTopicIdsByBlogId(id));
        List<Long> nextTopicIds = previousTopicIds;

        if (dto.getTitle() != null) {
            blog.setTitle(requireText(dto.getTitle(), "博客标题不能为空"));
        }
        if (dto.getContent() != null) {
            blog.setContent(requireText(dto.getContent(), "博客正文不能为空"));
            if (dto.getSummary() == null) {
                blog.setSummary(resolveSummary(blog.getSummary(), dto.getContent()));
            }
        }
        if (dto.getSummary() != null) {
            blog.setSummary(resolveSummary(dto.getSummary(), blog.getContent()));
        }
        if (dto.getImages() != null) {
            // 全量替换图片
            blogImageMapper.deleteByBlogId(id);
            saveImages(id, dto.getImages());
        }
        if (dto.getStatus() != null) {
            Integer oldStatus = blog.getStatus();
            blog.setStatus(normalizeStatus(dto.getStatus()));
            if (!Integer.valueOf(STATUS_PUBLISHED).equals(oldStatus)
                    && Integer.valueOf(STATUS_PUBLISHED).equals(blog.getStatus())) {
                blog.setPublishedAt(LocalDateTime.now());
            }
        }
        if (dto.getRefType() != null || dto.getRefId() != null) {
            String refType = normalizeRefType(dto.getRefType());
            validateRef(refType, dto.getRefId(), userId);
            blog.setRefType(refType);
            blog.setRefId(refType == null ? null : dto.getRefId());
        }
        if (dto.getTopicIds() != null) {
            nextTopicIds = normalizeTopicIds(dto.getTopicIds());
            blog.setTopicIds(joinTopicIds(nextTopicIds));
            blogTopicMapper.deleteByBlogId(id);
            saveTopicRelations(id, nextTopicIds);
        }

        blogMapper.updateById(blog);
        syncTopicUsage(previousTopicIds, nextTopicIds, wasPublished, isPublished(blog));
        log.info("Blog updated: id={}, userId={}", id, userId);
        return toVO(blog);
    }

    @Override
    @Transactional
    public void delete(Long id, Long userId) {
        Blog blog = getOwnedBlog(id, userId);
        blogImageMapper.deleteByBlogId(blog.getId());
        blogTopicMapper.deleteByBlogId(blog.getId());
        // 更新话题的 blog_count
        if (isPublished(blog)) {
            for (Long topicId : splitTopicIds(blog.getTopicIds())) {
                applyTopicDelta(topicId, -1);
            }
        }
        blogMapper.deleteById(blog.getId());
        log.info("Blog deleted: id={}, userId={}", id, userId);
    }

    @Override
    public PageInfo<BlogVO> list(BlogQueryDto query) {
        PageHelper.startPage(query.getPage(), query.getSize());
        List<Blog> list = blogMapper.selectPublishedPage(
                trimToNull(query.getKeyword()),
                normalizeRefType(query.getRefType()),
                normalizeSort(query.getSort()));
        PageInfo<Blog> source = new PageInfo<>(list);
        return copyPage(source, list.stream().map(this::toVO).collect(Collectors.toList()));
    }

    @Override
    public PageInfo<BlogVO> adminList(Integer page, Integer size, String keyword, Integer status) {
        int pageNum = page != null && page > 0 ? page : 1;
        int pageSize = size != null && size > 0 ? Math.min(size, 50) : 10;
        PageHelper.startPage(pageNum, pageSize);
        List<Blog> list = blogMapper.selectAdminPage(trimToNull(keyword), status);
        PageInfo<Blog> source = new PageInfo<>(list);
        return copyPage(source, list.stream().map(this::toVO).collect(Collectors.toList()));
    }

    @Override
    public PageInfo<BlogVO> myList(Integer page, Integer size, Integer status, Long userId) {
        PageHelper.startPage(page != null && page > 0 ? page : 1, size != null && size > 0 ? size : 10);
        List<Blog> list = blogMapper.selectMyPage(userId, status);
        PageInfo<Blog> source = new PageInfo<>(list);
        return copyPage(source, list.stream().map(this::toVO).collect(Collectors.toList()));
    }

    @Override
    public PageInfo<BlogVO> publicListByUser(Integer page, Integer size, Long userId) {
        PageHelper.startPage(page != null && page > 0 ? page : 1, size != null && size > 0 ? size : 10);
        List<Blog> list = blogMapper.selectMyPage(userId, STATUS_PUBLISHED);
        PageInfo<Blog> source = new PageInfo<>(list);
        return copyPage(source, list.stream().map(this::toVO).collect(Collectors.toList()));
    }

    @Override
    @Transactional
    public BlogVO getDetail(Long id, Long currentUserId) {
        Blog blog = getExistingBlog(id);
        boolean owner = currentUserId != null && currentUserId.equals(blog.getAuthorId());
        if (!owner && !Integer.valueOf(STATUS_PUBLISHED).equals(blog.getStatus())) {
            throw new BusinessException(ResultCode.BLOG_FORBIDDEN);
        }
        if (Integer.valueOf(STATUS_PUBLISHED).equals(blog.getStatus())) {
            blogMapper.incrementViewCount(id);
            blog.setViewCount((blog.getViewCount() != null ? blog.getViewCount() : 0) + 1);
        }
        BlogVO vo = toVO(blog);
        // 当前用户是否已点赞
        if (currentUserId != null) {
            BlogLike liked = blogLikeMapper.selectActive(id, currentUserId);
            vo.setLiked(liked != null);
        } else {
            vo.setLiked(false);
        }
        // 收藏数
        long collectCount = blogCollectMapper.selectCount(
                new LambdaQueryWrapper<BlogCollect>()
                        .eq(BlogCollect::getBlogId, id));
        vo.setCollectCount((int) collectCount);
        return vo;
    }

    @Override
    @Transactional
    public int toggleLike(Long id, Long userId) {
        Blog blog = getExistingBlog(id);
        BlogLike existing = blogLikeMapper.selectActive(id, userId);
        if (existing != null) {
            blogLikeMapper.softDelete(id, userId);
            blogMapper.updateLikeCount(id, -1);
            return Math.max(0, (blog.getLikeCount() != null ? blog.getLikeCount() : 1) - 1);
        }
        BlogLike any = blogLikeMapper.selectAny(id, userId);
        if (any != null) {
            blogLikeMapper.restore(any.getId());
        } else {
            BlogLike like = new BlogLike();
            like.setBlogId(id);
            like.setUserId(userId);
            like.setDeleted(0);
            blogLikeMapper.insert(like);
        }
        blogMapper.updateLikeCount(id, 1);
        return (blog.getLikeCount() != null ? blog.getLikeCount() : 0) + 1;
    }

    @Override
    @Transactional
    public int collect(Long id, Long folderId, Long userId) {
        getExistingBlog(id);
        FavoriteFolder folder = favoriteFolderMapper.selectById(folderId);
        if (folder == null || Integer.valueOf(1).equals(folder.getDeleted())
                || !userId.equals(folder.getUserId())) {
            throw new BusinessException(ResultCode.FAVORITE_FOLDER_NOT_FOUND);
        }
        FavoriteItem existingItem = favoriteItemMapper.selectActiveByFolderAndTarget(folderId, ITEM_TYPE_BLOG, id);
        if (existingItem != null) {
            throw new BusinessException(ResultCode.FAVORITE_ITEM_EXISTS);
        }
        BlogCollect existing = blogCollectMapper.selectActive(id, folderId, userId);
        if (existing == null) {
            BlogCollect collect = new BlogCollect();
            collect.setBlogId(id);
            collect.setFolderId(folderId);
            collect.setUserId(userId);
            collect.setDeleted(0);
            blogCollectMapper.insert(collect);
        }
        FavoriteItem item = new FavoriteItem();
        item.setFolderId(folderId);
        item.setItemType(ITEM_TYPE_BLOG);
        item.setTargetId(id);
        item.setBlogId(id);
        item.setSort(0);
        item.setDeleted(0);
        favoriteItemMapper.insert(item);
        // 收藏夹条目数 +1
        favoriteFolderMapper.updateItemCount(folderId, 1);
        return (folder.getItemCount() != null ? folder.getItemCount() : 0) + 1;
    }

    @Override
    @Transactional
    public BlogVO adminUpdateStatus(Long id, Integer status) {
        Blog blog = getExistingBlog(id);
        boolean wasPublished = isPublished(blog);
        Integer nextStatus = normalizeAdminStatus(status);
        blogMapper.updateStatus(id, nextStatus);
        blog.setStatus(nextStatus);
        if (Integer.valueOf(STATUS_PUBLISHED).equals(nextStatus) && blog.getPublishedAt() == null) {
            blog.setPublishedAt(LocalDateTime.now());
        }
        List<Long> topicIds = splitTopicIds(blog.getTopicIds());
        syncTopicUsage(topicIds, topicIds, wasPublished, isPublished(blog));
        return toVO(blog);
    }

    @Override
    @Transactional
    public void adminDelete(Long id) {
        Blog blog = getExistingBlog(id);
        blogImageMapper.deleteByBlogId(blog.getId());
        blogTopicMapper.deleteByBlogId(blog.getId());
        if (isPublished(blog)) {
            for (Long topicId : splitTopicIds(blog.getTopicIds())) {
                applyTopicDelta(topicId, -1);
            }
        }
        blogMapper.deleteById(blog.getId());
        log.info("Blog admin deleted: id={}", id);
    }

    // ==================== 图片 ====================

    private void saveImages(Long blogId, List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            return;
        }
        List<String> valid = imageUrls.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(u -> !u.isEmpty())
                .distinct()
                .limit(MAX_IMAGES)
                .collect(Collectors.toList());
        for (int i = 0; i < valid.size(); i++) {
            BlogImage img = new BlogImage();
            img.setBlogId(blogId);
            img.setUrl(valid.get(i));
            img.setSort(i + 1);
            img.setDeleted(0);
            blogImageMapper.insert(img);
        }
    }

    // ==================== 话题 ====================

    private List<Long> normalizeTopicIds(List<Long> topicIds) {
        if (topicIds == null || topicIds.isEmpty()) {
            return Collections.emptyList();
        }
        return topicIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .limit(8)
                .collect(Collectors.toList());
    }

    private void saveTopicRelations(Long blogId, List<Long> topicIds) {
        if (topicIds == null || topicIds.isEmpty()) {
            return;
        }
        List<Long> valid = topicIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .limit(8)
                .collect(Collectors.toList());
        if (!valid.isEmpty()) {
            blogTopicMapper.insertBatch(blogId, valid);
        }
    }

    private void syncTopicUsage(List<Long> previousTopicIds, List<Long> nextTopicIds,
                                boolean wasPublished, boolean nowPublished) {
        Set<Long> previous = wasPublished ? new HashSet<>(normalizeTopicIds(previousTopicIds)) : Collections.emptySet();
        Set<Long> next = nowPublished ? new HashSet<>(normalizeTopicIds(nextTopicIds)) : Collections.emptySet();

        for (Long topicId : previous) {
            if (!next.contains(topicId)) {
                applyTopicDelta(topicId, -1);
            }
        }
        for (Long topicId : next) {
            if (!previous.contains(topicId)) {
                applyTopicDelta(topicId, 1);
            }
        }
    }

    private void applyTopicDelta(Long topicId, int delta) {
        topicMapper.incrementBlogCount(topicId, delta);
        hotDataService.incrTopic(topicId, delta);
    }

    private String joinTopicIds(List<Long> topicIds) {
        if (topicIds == null || topicIds.isEmpty()) {
            return null;
        }
        return topicIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .limit(8)
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    private List<Long> splitTopicIds(String topicIds) {
        if (topicIds == null || topicIds.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.stream(topicIds.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }

    // ==================== 校验 ====================

    private boolean isPublished(Blog blog) {
        return blog != null && Integer.valueOf(STATUS_PUBLISHED).equals(blog.getStatus());
    }

    private Blog getExistingBlog(Long id) {
        Blog blog = blogMapper.selectById(id);
        if (blog == null || Integer.valueOf(1).equals(blog.getDeleted())) {
            throw new BusinessException(ResultCode.BLOG_NOT_FOUND);
        }
        return blog;
    }

    private Blog getOwnedBlog(Long id, Long userId) {
        Blog blog = getExistingBlog(id);
        if (!userId.equals(blog.getAuthorId())) {
            throw new BusinessException(ResultCode.BLOG_FORBIDDEN);
        }
        return blog;
    }

    private void validateRef(String refType, Long refId, Long userId) {
        if (refType == null) {
            return;
        }
        if (refId == null) {
            throw new BusinessException(ResultCode.BLOG_REF_INVALID);
        }
        if ("knowledge".equals(refType)) {
            Knowledge knowledge = knowledgeMapper.selectById(refId);
            if (knowledge == null || Integer.valueOf(1).equals(knowledge.getDeleted())
                    || !Integer.valueOf(1).equals(knowledge.getStatus())) {
                throw new BusinessException(ResultCode.BLOG_REF_INVALID);
            }
            return;
        }
        // folder 类型：只允许关联自己公开的收藏夹
        FavoriteFolder folder = favoriteFolderMapper.selectById(refId);
        boolean visible = folder != null && !Integer.valueOf(1).equals(folder.getDeleted())
                && Integer.valueOf(1).equals(folder.getIsPublic())
                && userId.equals(folder.getUserId());
        if (!visible) {
            throw new BusinessException(ResultCode.BLOG_REF_INVALID);
        }
    }

    // ==================== VO ====================

    private BlogVO toVO(Blog blog) {
        BlogVO vo = new BlogVO();
        vo.setId(blog.getId());
        vo.setTitle(blog.getTitle());
        vo.setContent(blog.getContent());
        vo.setSummary(blog.getSummary());
        vo.setAuthorId(blog.getAuthorId());
        vo.setStatus(blog.getStatus());
        vo.setIsPinned(blog.getIsPinned());
        vo.setViewCount(blog.getViewCount() != null ? blog.getViewCount() : 0);
        vo.setLikeCount(blog.getLikeCount() != null ? blog.getLikeCount() : 0);
        vo.setCommentCount(blog.getCommentCount() != null ? blog.getCommentCount() : 0);
        vo.setRefType(blog.getRefType());
        vo.setRefId(blog.getRefId());
        vo.setPublishedAt(blog.getPublishedAt());
        vo.setCreatedAt(blog.getCreatedAt());
        vo.setUpdatedAt(blog.getUpdatedAt());

        // 图片
        List<BlogImage> images = blogImageMapper.selectByBlogId(blog.getId());
        vo.setImages(images.stream().map(BlogImage::getUrl).collect(Collectors.toList()));

        // 话题
        List<Long> topicIds = blogTopicMapper.selectTopicIdsByBlogId(blog.getId());
        if (topicIds != null && !topicIds.isEmpty()) {
            List<Topic> topics = topicMapper.selectByIds(topicIds);
            vo.setTopics(topics.stream().map(t -> {
                TopicSimple ts = new TopicSimple();
                ts.setId(t.getId());
                ts.setName(t.getName());
                ts.setColor(t.getColor());
                return ts;
            }).collect(Collectors.toList()));
        } else {
            vo.setTopics(Collections.emptyList());
        }

        // 作者
        User author = userMapper.selectById(blog.getAuthorId());
        if (author != null) {
            vo.setAuthorName(author.getNickname() != null && !author.getNickname().isBlank()
                    ? author.getNickname() : author.getUsername());
            vo.setAuthorAvatar(author.getAvatar());
        }
        vo.setRef(buildRef(blog));
        return vo;
    }

    private RefVO buildRef(Blog blog) {
        if (blog.getRefType() == null || blog.getRefId() == null) {
            return null;
        }
        RefVO ref = new RefVO();
        ref.setType(blog.getRefType());
        ref.setId(blog.getRefId());
        if ("knowledge".equals(blog.getRefType())) {
            Knowledge knowledge = knowledgeMapper.selectById(blog.getRefId());
            if (knowledge == null) {
                return null;
            }
            ref.setTitle(knowledge.getTitle());
            ref.setDescription(knowledge.getContent());
        } else if ("folder".equals(blog.getRefType())) {
            FavoriteFolder folder = favoriteFolderMapper.selectById(blog.getRefId());
            if (folder == null) {
                return null;
            }
            ref.setTitle(folder.getName());
            ref.setDescription(folder.getDescription());
            ref.setCoverImage(folder.getCoverImage());
        }
        return ref;
    }

    // ==================== 工具 ====================

    private String normalizeRefType(String refType) {
        String value = trimToNull(refType);
        if (value == null) {
            return null;
        }
        value = value.toLowerCase();
        if ("collection".equals(value)) {
            return "folder";
        }
        if (!"knowledge".equals(value) && !"folder".equals(value)) {
            throw new BusinessException(ResultCode.BLOG_REF_INVALID);
        }
        return value;
    }

    private String normalizeSort(String sort) {
        return "hot".equals(sort) ? "hot" : "newest";
    }

    private Integer normalizeStatus(Integer status) {
        return Integer.valueOf(STATUS_DRAFT).equals(status) ? STATUS_DRAFT : STATUS_PUBLISHED;
    }

    private Integer normalizeAdminStatus(Integer status) {
        if (status == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "status is required");
        }
        if (status < 0 || status > 3) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "status must be 0, 1, 2 or 3");
        }
        return status;
    }

    private String resolveSummary(String summary, String content) {
        String value = trimToNull(summary);
        if (value != null) {
            return value.length() > 500 ? value.substring(0, 500) : value;
        }
        String text = content == null ? "" : content
                .replaceAll("```[\\s\\S]*?```", " ")
                .replaceAll("[#>*_`\\-\\[\\]()]", " ")
                .replaceAll("\\s+", " ")
                .trim();
        if (text.isEmpty()) {
            return null;
        }
        return text.length() > 200 ? text.substring(0, 200) : text;
    }

    private String requireText(String text, String message) {
        String value = trimToNull(text);
        if (value == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, message);
        }
        return value;
    }

    private String trimToNull(String text) {
        if (text == null) {
            return null;
        }
        String value = text.trim();
        return value.isEmpty() ? null : value;
    }

    private PageInfo<BlogVO> copyPage(PageInfo<Blog> source, List<BlogVO> list) {
        PageInfo<BlogVO> target = new PageInfo<>();
        target.setList(list);
        target.setTotal(source.getTotal());
        target.setPageNum(source.getPageNum());
        target.setPageSize(source.getPageSize());
        target.setSize(source.getSize());
        target.setStartRow(source.getStartRow());
        target.setEndRow(source.getEndRow());
        target.setPages(source.getPages());
        target.setPrePage(source.getPrePage());
        target.setNextPage(source.getNextPage());
        target.setIsFirstPage(source.isIsFirstPage());
        target.setIsLastPage(source.isIsLastPage());
        target.setHasPreviousPage(source.isHasPreviousPage());
        target.setHasNextPage(source.isHasNextPage());
        target.setNavigatePages(source.getNavigatePages());
        target.setNavigatepageNums(source.getNavigatepageNums());
        target.setNavigateFirstPage(source.getNavigateFirstPage());
        target.setNavigateLastPage(source.getNavigateLastPage());
        return target;
    }
}
