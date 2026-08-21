package com.yimian.system.service;

import com.github.pagehelper.PageInfo;
import com.yimian.system.dto.SystemAnnouncementCreateDto;
import com.yimian.system.dto.SystemAnnouncementQueryDto;
import com.yimian.system.vo.SystemAnnouncementVO;
import java.util.List;

public interface SystemAnnouncementService {

    SystemAnnouncementVO publish(SystemAnnouncementCreateDto dto, Long creatorId);

    PageInfo<SystemAnnouncementVO> list(SystemAnnouncementQueryDto query);

    List<SystemAnnouncementVO> listUnreadImportant(Long userId);

    void confirm(Long announcementId, Long userId);
}
