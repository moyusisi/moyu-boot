package com.moyu.boot.plugin.thirdPartyApi.service;

import com.dtflys.forest.http.ForestResponse;
import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.plugin.thirdPartyApi.model.entity.ThirdPartyApi;
import com.moyu.boot.plugin.thirdPartyApi.model.param.ThirdPartyApiParam;
import com.moyu.boot.plugin.thirdPartyApi.model.vo.ThirdPartyApiVO;
import com.mybatisflex.core.service.IService;

import java.util.List;
import java.util.Map;

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
     * 调试接口
     */
    void debugApi(ThirdPartyApiParam param);

    /**
     * 调用接口
     *
     * @param apiCode 接口唯一标识
     * @param headers 请求头数据
     * @param params  请求数据
     * @return 返回结果(请求失败返回null)
     */
    String requestApi(String apiCode, Map<String, Object> headers, Map<String, Object> params);

    /**
     * 发送GET/POST请求
     */
    ForestResponse<?> request(String method, String url, Map<String, Object> headers, Map<String, Object> params);

    /**
     * 发送GET请求，只有query，无body
     */
    ForestResponse<?> get(String url, Map<String, Object> headers, Map<String, Object> params);

    /**
     * 发送POST请求，表单提交
     */
    ForestResponse<?> postForm(String url, Map<String, Object> headers, Map<String, Object> params);

    /**
     * 发送POST请求，body为json
     */
    ForestResponse<?> postJson(String url, Map<String, Object> headers, Map<String, Object> params);
}
