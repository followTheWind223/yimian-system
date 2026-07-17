package com.yimian.system.service;

import com.github.pagehelper.PageInfo;
import com.yimian.system.dto.FavoriteFolderCreateDto;
import com.yimian.system.dto.FavoriteFolderUpdateDto;
import com.yimian.system.dto.FavoriteItemCreateDto;
import com.yimian.system.vo.FavoriteCheckVO;
import com.yimian.system.vo.FavoriteFolderDetailVO;
import com.yimian.system.vo.FavoriteFolderVO;
import com.yimian.system.vo.FavoriteItemVO;

import java.util.List;

public interface FavoriteService {

    FavoriteFolderVO createFolder(FavoriteFolderCreateDto dto, Long userId);

    FavoriteFolderVO updateFolder(Long id, FavoriteFolderUpdateDto dto, Long userId);

    void deleteFolder(Long id, Long userId);

    FavoriteItemVO addItem(Long folderId, FavoriteItemCreateDto dto, Long userId);

    void removeItem(Long folderId, Long itemId, Long userId);

    List<FavoriteFolderVO> listPublicFolders(Long userId);

    List<FavoriteFolderVO> listMyFolders(Long userId);

    FavoriteFolderDetailVO getFolderDetail(Long id, Integer page, Integer size, String keyword, Long currentUserId);

    PageInfo<FavoriteItemVO> listFolderItems(Long folderId, Integer page, Integer size, String keyword, Long currentUserId);

    FavoriteCheckVO checkCollected(Long knowledgeId, Long userId);
}
