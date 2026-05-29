package com.moyu.boot.system.service;

import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.system.model.entity.SysConfig;
import com.moyu.boot.system.model.param.SysConfigParam;
import com.moyu.boot.system.model.vo.SysConfigVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 系统配置服务类Service
 *
 * @author moyusisi
 * @since 2026-05-28
 */
public interface SysConfigService extends IService<SysConfig> {

    /**
     * 获取记录列表(不分页，通过条件自行控制数量)
     */
    List<SysConfigVO> list(SysConfigParam param);

    /**
     * 分页获取记录列表
     */
    PageData<SysConfigVO> pageList(SysConfigParam param);

    /**
     * 获取记录详情(通过主键或唯一键)
     */
     SysConfigVO detail(SysConfigParam param);

    /**
     * 通过keyName获取对应keyValue(仅查询未删除且有效的)
     */
    String getValue(String keyName);

    /**
     * 添加记录
     */
    void add(SysConfigParam param);

    /**
     * 修改记录(通过主键id更新)
     */
    void update(SysConfigParam param);

    /**
     * 通过ids删除记录
     */
    void deleteByIds(SysConfigParam param);
}
