package com.moyu.boot.system.service;

import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.system.model.entity.Scope;
import com.moyu.boot.system.model.param.ScopeParam;
import com.moyu.boot.system.model.vo.ScopeVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 分组信息服务类Service
 *
 * @author moyusisi
 * @since 2025-10-04
 */
public interface ScopeService extends IService<Scope> {

    /**
     * 获取记录列表(不分页，通过条件自行控制数量)
     */
    List<ScopeVO> list(ScopeParam roleParam);

    /**
     * 分页获取记录列表
     */
    PageData<ScopeVO> pageList(ScopeParam param);

    /**
     * 获取记录详情(通过主键或唯一键)
     */
     ScopeVO detail(ScopeParam param);

    /**
     * 添加记录
     */
    void add(ScopeParam param);

    /**
     * 修改记录(通过主键id更新)
     */
    void update(ScopeParam param);

    /**
     * 通过ids删除记录
     */
    void deleteByIds(ScopeParam param);
}
