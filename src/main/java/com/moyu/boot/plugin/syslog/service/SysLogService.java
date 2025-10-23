package com.moyu.boot.plugin.syslog.service;

import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.plugin.syslog.model.entity.SysLog;
import com.moyu.boot.plugin.syslog.model.param.SysLogParam;
import com.moyu.boot.plugin.syslog.model.vo.SysLogVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 系统日志服务类Service
 *
 * @author moyusisi
 * @since 2025-10-22
 */
public interface SysLogService extends IService<SysLog> {

    /**
     * 获取记录列表(不分页，通过条件自行控制数量)
     */
    List<SysLogVO> list(SysLogParam param);

    /**
     * 分页获取记录列表
     */
    PageData<SysLogVO> pageList(SysLogParam param);

    /**
     * 获取记录详情(通过主键或唯一键)
     */
     SysLogVO detail(SysLogParam param);

    /**
     * 添加记录
     */
    void add(SysLogParam param);

    /**
     * 修改记录(通过主键id更新)
     */
    void update(SysLogParam param);

    /**
     * 通过ids删除记录
     */
    void deleteByIds(SysLogParam param);
}
