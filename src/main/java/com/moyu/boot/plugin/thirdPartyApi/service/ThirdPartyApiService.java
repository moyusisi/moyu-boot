package com.moyu.boot.plugin.thirdPartyApi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.plugin.thirdPartyApi.model.entity.ThirdPartyApi;
import com.moyu.boot.plugin.thirdPartyApi.model.param.ThirdPartyApiParam;
import com.moyu.boot.plugin.thirdPartyApi.model.vo.ThirdPartyApiVO;

import java.util.List;

/**
 * 三方集成接口服务类Service
 *
 * @author moyusisi
 * @since 2026-08-06
 */
public interface ThirdPartyApiService extends IService<ThirdPartyApi> {

    /**
     * 获取记录列表(不分页，通过条件自行控制数量)
     */
    List<ThirdPartyApiVO> list(ThirdPartyApiParam param);

    /**
     * 分页获取记录列表
     */
    PageData<ThirdPartyApiVO> pageList(ThirdPartyApiParam param);

    /**
     * 获取记录详情(通过主键或唯一键)
     */
    ThirdPartyApiVO detail(ThirdPartyApiParam param);

    /**
     * 添加记录
     */
    void add(ThirdPartyApiParam param);

    /**
     * 修改记录(通过主键id更新)
     */
    void update(ThirdPartyApiParam param);

    /**
     * 通过ids删除记录
     */
    void deleteByIds(ThirdPartyApiParam param);

    /**
     * 调用接口
     */
    ThirdPartyApiVO debugApi(ThirdPartyApiParam param);
}
