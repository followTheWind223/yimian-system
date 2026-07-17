package com.yimian.system.service;

import com.github.pagehelper.PageInfo;
import com.yimian.system.dto.KnowledgeCreateDto;
import com.yimian.system.dto.KnowledgeQueryDto;
import com.yimian.system.dto.KnowledgeUpdateDto;
import com.yimian.system.vo.KnowledgeVO;

/**
 * 知识题目服务
 */
public interface KnowledgeService {

    /** 直接上传（跳过审核，需要权限） */
    KnowledgeVO createDirect(KnowledgeCreateDto dto, Long userId);

    /** 提交审核（受 audit.enabled 开关控制） */
    KnowledgeVO submit(KnowledgeCreateDto dto, Long userId);

    /** 编辑题目 */
    KnowledgeVO update(Long id, KnowledgeUpdateDto dto, Long userId);

    /** 删除题目 */
    void delete(Long id, Long userId);

    /** 分页查询 */
    PageInfo<KnowledgeVO> list(KnowledgeQueryDto query);

    /** 详情查询 */
    KnowledgeVO getById(Long id);

    KnowledgeVO getById(Long id, Long currentUserId);

    int toggleLike(Long id, Long userId);

    /** 检查内容哈希是否重复 */
    boolean isContentDuplicate(String hash);

    /** 我的题目列表（分页） */
    PageInfo<KnowledgeVO> myList(Integer page, Integer size, Integer status, Long userId);

    PageInfo<KnowledgeVO> publicListByUser(Integer page, Integer size, Long userId);

    /** 获取审核开关状态 */
    boolean getAuditEnabled();

    /** 设置审核开关（管理员，运行时生效，重启后恢复为 application.yml 配置值） */
    void setAuditEnabled(boolean enabled);

    /** 审核通过 */
    KnowledgeVO approve(Long id, Long auditorId);

    /** 审核拒绝 */
    KnowledgeVO reject(Long id, String remark, Long auditorId);

    /** 批量审核 */
    int batchAudit(java.util.List<Long> ids, Boolean approve, String remark, Long auditorId);
}
