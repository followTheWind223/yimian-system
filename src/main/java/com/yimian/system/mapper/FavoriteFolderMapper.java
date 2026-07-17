package com.yimian.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yimian.system.entity.FavoriteFolder;
import org.apache.ibatis.annotations.Param;

public interface FavoriteFolderMapper extends BaseMapper<FavoriteFolder> {

    int incrementViewCount(@Param("id") Long id);

    int updateItemCount(@Param("id") Long id, @Param("delta") Integer delta);
}
