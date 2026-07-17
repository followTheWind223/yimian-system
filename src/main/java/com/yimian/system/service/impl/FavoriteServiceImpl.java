package com.yimian.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yimian.system.common.exception.BusinessException;
import com.yimian.system.common.result.ResultCode;
import com.yimian.system.dto.FavoriteFolderCreateDto;
import com.yimian.system.dto.FavoriteFolderUpdateDto;
import com.yimian.system.dto.FavoriteItemCreateDto;
import com.yimian.system.entity.Blog;
import com.yimian.system.entity.BlogCollect;
import com.yimian.system.entity.FavoriteFolder;
import com.yimian.system.entity.FavoriteItem;
import com.yimian.system.entity.Knowledge;
import com.yimian.system.entity.Tag;
import com.yimian.system.mapper.BlogCollectMapper;
import com.yimian.system.mapper.BlogMapper;
import com.yimian.system.mapper.FavoriteFolderMapper;
import com.yimian.system.mapper.FavoriteItemMapper;
import com.yimian.system.mapper.KnowledgeMapper;
import com.yimian.system.mapper.KnowledgeTagMapper;
import com.yimian.system.mapper.TagMapper;
import com.yimian.system.service.FavoriteService;
import com.yimian.system.service.HotDataService;
import com.yimian.system.vo.FavoriteCheckVO;
import com.yimian.system.vo.FavoriteFolderDetailVO;
import com.yimian.system.vo.FavoriteFolderVO;
import com.yimian.system.vo.FavoriteItemVO;
import com.yimian.system.vo.BlogVO;
import com.yimian.system.vo.KnowledgeVO;
import com.yimian.system.vo.KnowledgeVO.TagVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private static final String ITEM_TYPE_KNOWLEDGE = "knowledge";
    private static final String ITEM_TYPE_BLOG = "blog";

    private final FavoriteFolderMapper favoriteFolderMapper;
    private final FavoriteItemMapper favoriteItemMapper;
    private final BlogMapper blogMapper;
    private final BlogCollectMapper blogCollectMapper;
    private final KnowledgeMapper knowledgeMapper;
    private final KnowledgeTagMapper knowledgeTagMapper;
    private final TagMapper tagMapper;
    private final HotDataService hotDataService;

    @Override
    @Transactional
    public FavoriteFolderVO createFolder(FavoriteFolderCreateDto dto, Long userId) {
        FavoriteFolder folder = new FavoriteFolder();
        folder.setName(dto.getName());
        folder.setDescription(dto.getDescription());
        folder.setUserId(userId);
        folder.setIsPublic(normalizePublicFlag(dto.getIsPublic()));
        folder.setCoverImage(dto.getCoverImage());
        folder.setItemCount(0);
        folder.setViewCount(0);
        folder.setDeleted(0);
        favoriteFolderMapper.insert(folder);
        log.info("Favorite folder created: id={}, userId={}", folder.getId(), userId);
        return toFolderVO(folder);
    }

    @Override
    @Transactional
    public FavoriteFolderVO updateFolder(Long id, FavoriteFolderUpdateDto dto, Long userId) {
        FavoriteFolder folder = getOwnedFolder(id, userId);
        if (dto.getName() != null && !dto.getName().isBlank()) {
            folder.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            folder.setDescription(dto.getDescription());
        }
        if (dto.getIsPublic() != null) {
            folder.setIsPublic(normalizePublicFlag(dto.getIsPublic()));
        }
        if (dto.getCoverImage() != null) {
            folder.setCoverImage(dto.getCoverImage());
        }
        favoriteFolderMapper.updateById(folder);
        log.info("Favorite folder updated: id={}, userId={}", id, userId);
        return toFolderVO(folder);
    }

    @Override
    @Transactional
    public void deleteFolder(Long id, Long userId) {
        FavoriteFolder folder = getOwnedFolder(id, userId);
        List<FavoriteItem> items = favoriteItemMapper.selectList(new LambdaQueryWrapper<FavoriteItem>()
                .eq(FavoriteItem::getFolderId, id));
        for (FavoriteItem item : items) {
            if (ITEM_TYPE_KNOWLEDGE.equals(item.getItemType())) {
                knowledgeMapper.updateCollectCount(item.getTargetId(), -1);
            } else if (ITEM_TYPE_BLOG.equals(item.getItemType())) {
                removeBlogCollect(item, userId);
            }
            favoriteItemMapper.deleteById(item.getId());
        }
        favoriteFolderMapper.deleteById(folder.getId());
        log.info("Favorite folder deleted: id={}, userId={}, itemCount={}", id, userId, items.size());
    }

    @Override
    @Transactional
    public FavoriteItemVO addItem(Long folderId, FavoriteItemCreateDto dto, Long userId) {
        getOwnedFolder(folderId, userId);
        String itemType = resolveItemType(dto);
        Long targetId = resolveTargetId(dto, itemType);

        FavoriteItem existing = favoriteItemMapper.selectActiveByFolderAndTarget(folderId, itemType, targetId);
        if (existing != null) {
            throw new BusinessException(ResultCode.FAVORITE_ITEM_EXISTS);
        }

        FavoriteItem item = new FavoriteItem();
        item.setFolderId(folderId);
        item.setItemType(itemType);
        item.setTargetId(targetId);
        item.setSort(0);
        item.setDeleted(0);
        Knowledge knowledge = null;
        Blog blog = null;
        if (ITEM_TYPE_KNOWLEDGE.equals(itemType)) {
            knowledge = getCollectableKnowledge(targetId);
            item.setKnowledgeId(targetId);
        } else {
            blog = getCollectableBlog(targetId);
            item.setBlogId(targetId);
            addBlogCollect(folderId, targetId, userId);
        }
        favoriteItemMapper.insert(item);
        favoriteFolderMapper.updateItemCount(folderId, 1);
        if (ITEM_TYPE_KNOWLEDGE.equals(itemType)) {
            knowledgeMapper.updateCollectCount(targetId, 1);
            hotDataService.incrCollect(targetId);
        }

        log.info("Favorite item added: folderId={}, itemType={}, targetId={}, userId={}",
                folderId, itemType, targetId, userId);
        return toItemVO(item, knowledge, blog);
    }

    @Override
    @Transactional
    public void removeItem(Long folderId, Long itemId, Long userId) {
        getOwnedFolder(folderId, userId);
        FavoriteItem item = favoriteItemMapper.selectById(itemId);
        if (item == null || !folderId.equals(item.getFolderId())) {
            throw new BusinessException(ResultCode.FAVORITE_ITEM_NOT_FOUND);
        }
        favoriteItemMapper.deleteById(itemId);
        favoriteFolderMapper.updateItemCount(folderId, -1);
        if (ITEM_TYPE_KNOWLEDGE.equals(item.getItemType())) {
            knowledgeMapper.updateCollectCount(item.getTargetId(), -1);
        } else if (ITEM_TYPE_BLOG.equals(item.getItemType())) {
            removeBlogCollect(item, userId);
        }
        log.info("Favorite item removed: folderId={}, itemId={}, userId={}", folderId, itemId, userId);
    }

    @Override
    public List<FavoriteFolderVO> listPublicFolders(Long userId) {
        List<FavoriteFolder> folders = favoriteFolderMapper.selectList(new LambdaQueryWrapper<FavoriteFolder>()
                .eq(FavoriteFolder::getUserId, userId)
                .eq(FavoriteFolder::getIsPublic, 1)
                .orderByDesc(FavoriteFolder::getCreatedAt));
        return folders.stream().map(this::toFolderVO).collect(Collectors.toList());
    }

    @Override
    public List<FavoriteFolderVO> listMyFolders(Long userId) {
        List<FavoriteFolder> folders = favoriteFolderMapper.selectList(new LambdaQueryWrapper<FavoriteFolder>()
                .eq(FavoriteFolder::getUserId, userId)
                .orderByDesc(FavoriteFolder::getCreatedAt));
        return folders.stream().map(this::toFolderVO).collect(Collectors.toList());
    }

    @Override
    public FavoriteFolderDetailVO getFolderDetail(Long id, Integer page, Integer size, String keyword, Long currentUserId) {
        FavoriteFolder folder = getVisibleFolder(id, currentUserId);
        favoriteFolderMapper.incrementViewCount(id);

        PageInfo<FavoriteItemVO> itemPage = queryFolderItems(id, page, size, keyword);

        FavoriteFolderDetailVO detail = new FavoriteFolderDetailVO();
        copyFolderFields(folder, detail);
        detail.setViewCount((folder.getViewCount() != null ? folder.getViewCount() : 0) + 1);
        detail.setItems(itemPage);
        return detail;
    }

    @Override
    public PageInfo<FavoriteItemVO> listFolderItems(Long folderId, Integer page, Integer size, String keyword, Long currentUserId) {
        getVisibleFolder(folderId, currentUserId);
        return queryFolderItems(folderId, page, size, keyword);
    }

    private PageInfo<FavoriteItemVO> queryFolderItems(Long folderId, Integer page, Integer size, String keyword) {
        int pageNum = page != null && page > 0 ? page : 1;
        int pageSize = size != null && size > 0 ? size : 10;
        PageHelper.startPage(pageNum, pageSize);
        String searchKeyword = keyword != null && !keyword.isBlank() ? keyword.trim() : null;
        List<FavoriteItem> items = favoriteItemMapper.selectFolderItems(folderId, searchKeyword);
        PageInfo<FavoriteItem> sourcePage = new PageInfo<>(items);
        List<FavoriteItemVO> itemVOList = items.stream().map(item -> {
            if (ITEM_TYPE_BLOG.equals(item.getItemType())) {
                Blog blog = blogMapper.selectById(item.getTargetId());
                return toItemVO(item, null, blog);
            }
            Knowledge knowledge = knowledgeMapper.selectById(item.getTargetId());
            return toItemVO(item, knowledge, null);
        }).collect(Collectors.toList());
        return copyPage(sourcePage, itemVOList);
    }

    @Override
    public FavoriteCheckVO checkCollected(Long knowledgeId, Long userId) {
        List<FavoriteFolder> folders = favoriteItemMapper.selectFoldersByUserAndKnowledge(userId, knowledgeId);
        FavoriteCheckVO vo = new FavoriteCheckVO();
        vo.setCollected(!folders.isEmpty());
        vo.setFolders(folders.stream().map(this::toFolderVO).collect(Collectors.toList()));
        return vo;
    }

    private String resolveItemType(FavoriteItemCreateDto dto) {
        String itemType = dto.getItemType();
        if (itemType == null || itemType.isBlank()) {
            return dto.getBlogId() != null ? ITEM_TYPE_BLOG : ITEM_TYPE_KNOWLEDGE;
        }
        itemType = itemType.trim().toLowerCase();
        if (!ITEM_TYPE_KNOWLEDGE.equals(itemType) && !ITEM_TYPE_BLOG.equals(itemType)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "Unsupported favorite item type");
        }
        return itemType;
    }

    private Long resolveTargetId(FavoriteItemCreateDto dto, String itemType) {
        Long targetId = dto.getTargetId();
        if (targetId == null) {
            targetId = ITEM_TYPE_BLOG.equals(itemType) ? dto.getBlogId() : dto.getKnowledgeId();
        }
        if (targetId == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "Favorite target ID cannot be empty");
        }
        return targetId;
    }

    private FavoriteFolder getOwnedFolder(Long id, Long userId) {
        FavoriteFolder folder = favoriteFolderMapper.selectById(id);
        if (folder == null) {
            throw new BusinessException(ResultCode.FAVORITE_FOLDER_NOT_FOUND);
        }
        if (!userId.equals(folder.getUserId())) {
            throw new BusinessException(ResultCode.FAVORITE_FORBIDDEN);
        }
        return folder;
    }

    private FavoriteFolder getVisibleFolder(Long id, Long currentUserId) {
        FavoriteFolder folder = favoriteFolderMapper.selectById(id);
        if (folder == null) {
            throw new BusinessException(ResultCode.FAVORITE_FOLDER_NOT_FOUND);
        }
        boolean owner = currentUserId != null && currentUserId.equals(folder.getUserId());
        boolean visible = owner || Integer.valueOf(1).equals(folder.getIsPublic());
        if (!visible) {
            throw new BusinessException(ResultCode.FAVORITE_FOLDER_PRIVATE);
        }
        return folder;
    }

    private Knowledge getCollectableKnowledge(Long knowledgeId) {
        Knowledge knowledge = knowledgeMapper.selectById(knowledgeId);
        if (knowledge == null || Integer.valueOf(1).equals(knowledge.getDeleted())
                || !Integer.valueOf(1).equals(knowledge.getStatus())) {
            throw new BusinessException(ResultCode.KNOWLEDGE_NOT_FOUND);
        }
        return knowledge;
    }

    private Blog getCollectableBlog(Long blogId) {
        Blog blog = blogMapper.selectById(blogId);
        if (blog == null || Integer.valueOf(1).equals(blog.getDeleted())
                || !Integer.valueOf(1).equals(blog.getStatus())) {
            throw new BusinessException(ResultCode.BLOG_NOT_FOUND);
        }
        return blog;
    }

    private void addBlogCollect(Long folderId, Long blogId, Long userId) {
        BlogCollect existing = blogCollectMapper.selectActive(blogId, folderId, userId);
        if (existing != null) {
            return;
        }
        BlogCollect collect = new BlogCollect();
        collect.setBlogId(blogId);
        collect.setFolderId(folderId);
        collect.setUserId(userId);
        collect.setDeleted(0);
        blogCollectMapper.insert(collect);
    }

    private void removeBlogCollect(FavoriteItem item, Long userId) {
        Long blogId = item.getTargetId() != null ? item.getTargetId() : item.getBlogId();
        if (blogId == null) {
            return;
        }
        BlogCollect collect = blogCollectMapper.selectActive(blogId, item.getFolderId(), userId);
        if (collect != null) {
            blogCollectMapper.deleteById(collect.getId());
        }
    }

    private Integer normalizePublicFlag(Integer isPublic) {
        return Integer.valueOf(1).equals(isPublic) ? 1 : 0;
    }

    private FavoriteFolderVO toFolderVO(FavoriteFolder folder) {
        FavoriteFolderVO vo = new FavoriteFolderVO();
        copyFolderFields(folder, vo);
        return vo;
    }

    private void copyFolderFields(FavoriteFolder folder, FavoriteFolderVO vo) {
        vo.setId(folder.getId());
        vo.setName(folder.getName());
        vo.setDescription(folder.getDescription());
        vo.setUserId(folder.getUserId());
        vo.setIsPublic(folder.getIsPublic());
        vo.setCoverImage(folder.getCoverImage());
        vo.setItemCount(folder.getItemCount() != null ? folder.getItemCount() : 0);
        vo.setViewCount(folder.getViewCount() != null ? folder.getViewCount() : 0);
        vo.setCreatedAt(folder.getCreatedAt());
        vo.setUpdatedAt(folder.getUpdatedAt());
    }

    private FavoriteItemVO toItemVO(FavoriteItem item, Knowledge knowledge, Blog blog) {
        FavoriteItemVO vo = new FavoriteItemVO();
        vo.setId(item.getId());
        vo.setFolderId(item.getFolderId());
        vo.setItemType(item.getItemType());
        vo.setTargetId(item.getTargetId());
        vo.setKnowledgeId(item.getKnowledgeId());
        vo.setBlogId(item.getBlogId());
        vo.setSort(item.getSort());
        vo.setCreatedAt(item.getCreatedAt());
        vo.setUpdatedAt(item.getUpdatedAt());
        if (knowledge != null) {
            vo.setKnowledge(toKnowledgeVO(knowledge));
        }
        if (blog != null) {
            vo.setBlog(toBlogVO(blog));
        }
        return vo;
    }

    private BlogVO toBlogVO(Blog entity) {
        BlogVO vo = new BlogVO();
        vo.setId(entity.getId());
        vo.setTitle(entity.getTitle());
        vo.setContent(entity.getContent());
        vo.setSummary(entity.getSummary());
        vo.setAuthorId(entity.getAuthorId());
        vo.setStatus(entity.getStatus());
        vo.setIsPinned(entity.getIsPinned());
        vo.setViewCount(entity.getViewCount() != null ? entity.getViewCount() : 0);
        vo.setLikeCount(entity.getLikeCount() != null ? entity.getLikeCount() : 0);
        vo.setCommentCount(entity.getCommentCount() != null ? entity.getCommentCount() : 0);
        vo.setPublishedAt(entity.getPublishedAt());
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());
        return vo;
    }

    private KnowledgeVO toKnowledgeVO(Knowledge entity) {
        KnowledgeVO vo = new KnowledgeVO();
        vo.setId(entity.getId());
        vo.setTitle(entity.getTitle());
        vo.setContent(entity.getContent());
        vo.setDifficulty(entity.getDifficulty());
        vo.setStatus(entity.getStatus());
        vo.setAuditRemark(entity.getAuditRemark());
        vo.setAuditUserId(entity.getAuditUserId());
        vo.setAuditTime(entity.getAuditTime());
        vo.setSubmitUserId(entity.getSubmitUserId());
        vo.setViewCount(entity.getViewCount() != null ? entity.getViewCount() : 0);
        vo.setLikeCount(entity.getLikeCount() != null ? entity.getLikeCount() : 0);
        vo.setCollectCount(entity.getCollectCount() != null ? entity.getCollectCount() : 0);
        vo.setCommentCount(entity.getCommentCount() != null ? entity.getCommentCount() : 0);
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());

        List<Long> tagIds = knowledgeTagMapper.selectTagIdsByKnowledgeId(entity.getId());
        if (tagIds != null && !tagIds.isEmpty()) {
            List<Tag> tags = tagMapper.selectByIds(tagIds);
            vo.setTags(tags.stream().map(tag -> {
                TagVO tagVO = new TagVO();
                tagVO.setId(tag.getId());
                tagVO.setName(tag.getName());
                tagVO.setColor(tag.getColor());
                return tagVO;
            }).collect(Collectors.toList()));
        } else {
            vo.setTags(Collections.emptyList());
        }
        return vo;
    }

    private PageInfo<FavoriteItemVO> copyPage(PageInfo<FavoriteItem> source, List<FavoriteItemVO> list) {
        PageInfo<FavoriteItemVO> target = new PageInfo<>();
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
