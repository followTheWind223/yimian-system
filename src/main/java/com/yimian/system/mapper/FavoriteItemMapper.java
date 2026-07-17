package com.yimian.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yimian.system.entity.FavoriteFolder;
import com.yimian.system.entity.FavoriteItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FavoriteItemMapper extends BaseMapper<FavoriteItem> {

    FavoriteItem selectActiveByFolderAndKnowledge(@Param("folderId") Long folderId,
                                                  @Param("knowledgeId") Long knowledgeId);

    FavoriteItem selectActiveByFolderAndTarget(@Param("folderId") Long folderId,
                                               @Param("itemType") String itemType,
                                               @Param("targetId") Long targetId);

    List<FavoriteItem> selectFolderItems(@Param("folderId") Long folderId,
                                         @Param("keyword") String keyword);

    List<FavoriteFolder> selectFoldersByUserAndKnowledge(@Param("userId") Long userId,
                                                         @Param("knowledgeId") Long knowledgeId);

    List<FavoriteFolder> selectFoldersByUserAndTarget(@Param("userId") Long userId,
                                                      @Param("itemType") String itemType,
                                                      @Param("targetId") Long targetId);
}
