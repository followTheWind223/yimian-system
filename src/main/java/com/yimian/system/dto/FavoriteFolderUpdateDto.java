package com.yimian.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Update favorite folder request")
public class FavoriteFolderUpdateDto {

    @Size(max = 100, message = "收藏夹名称不能超过100个字符")
    @Schema(description = "收藏夹名称")
    private String name;

    @Size(max = 500, message = "收藏夹描述不能超过500个字符")
    @Schema(description = "收藏夹描述")
    private String description;

    @Schema(description = "是否公开: 0=私有, 1=公开")
    private Integer isPublic;

    @Size(max = 500, message = "封面图不能超过500个字符")
    @Schema(description = "封面图 URL")
    private String coverImage;
}
