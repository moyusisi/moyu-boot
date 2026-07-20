package com.moyu.boot.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.system.model.entity.SysApi;
import com.moyu.boot.system.model.param.SysApiParam;
import com.moyu.boot.system.model.vo.SysApiVO;

import java.util.List;

/**
 * 接口信息服务类Service
 *
 * @author moyusisi
 * @since 2026-07-20
 */
public interface SysApiService extends IService<SysApi> {

    /**
     * 获取记录列表(不分页，通过条件自行控制数量)
     */
    List<SysApiVO> list(SysApiParam param);

    /**
     * 分页获取记录列表
     */
    PageData<SysApiVO> pageList(SysApiParam param);

    /**
     * 获取记录详情(通过主键或唯一键)
     */
     SysApiVO detail(SysApiParam param);

    /**
     * 添加记录
     */
    void add(SysApiParam param);

    /**
     * 修改记录(通过主键id更新)
     */
    void update(SysApiParam param);

    /**
     * 通过ids删除记录
     */
    void deleteByIds(SysApiParam param);
}
