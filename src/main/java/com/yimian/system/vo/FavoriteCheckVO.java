package com.yimian.system.vo;

import lombok.Data;

import java.util.List;

@Data
public class FavoriteCheckVO {

    private Boolean collected;

    private List<FavoriteFolderVO> folders;
}
