package com.moyu.boot.plugin.ThirdPartyApp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.plugin.ThirdPartyApp.model.entity.ThirdPartyApp;
import com.moyu.boot.plugin.ThirdPartyApp.model.param.ThirdPartyAppParam;
import com.moyu.boot.plugin.ThirdPartyApp.model.vo.ThirdPartyAppVO;

import java.util.List;

/**
 * 三方应用服务类Service
 *
 * @author moyusisi
 * @since 2026-08-17
 */
public interface ThirdPartyAppService extends IService<ThirdPartyApp> {

    /**
     * 获取记录列表(不分页，通过条件自行控制数量)
     */
    List<ThirdPartyAppVO> list(ThirdPartyAppParam param);

    /**
     * 分页获取记录列表
     */
    PageData<ThirdPartyAppVO> pageList(ThirdPartyAppParam param);

    /**
     * 获取记录详情(通过主键或唯一键)
     */
     ThirdPartyAppVO detail(ThirdPartyAppParam param);

    /**
     * 添加记录
     */
    void add(ThirdPartyAppParam param);

    /**
     * 修改记录(通过主键id更新)
     */
    void update(ThirdPartyAppParam param);

    /**
     * 通过ids删除记录
     */
    void deleteByIds(ThirdPartyAppParam param);
}
