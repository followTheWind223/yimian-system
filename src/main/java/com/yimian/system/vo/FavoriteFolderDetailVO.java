package com.yimian.system.vo;

import com.github.pagehelper.PageInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FavoriteFolderDetailVO extends FavoriteFolderVO {

    private PageInfo<FavoriteItemVO> items;
}
