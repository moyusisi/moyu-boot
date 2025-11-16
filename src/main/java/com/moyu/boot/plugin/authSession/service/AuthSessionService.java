package com.moyu.boot.plugin.authSession.service;


import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.plugin.authSession.model.param.AuthSessionParam;
import com.moyu.boot.plugin.authSession.model.vo.AuthSessionAnalysisVO;
import com.moyu.boot.plugin.authSession.model.vo.AuthSessionVO;

/**
 * 会话管理服务类Service
 *
 * @author shisong
 * @since 2025-11-15
 */
public interface AuthSessionService {

    /**
     * 统计分析
     */
    AuthSessionAnalysisVO analyse();

    /**
     * 分页获取记录列表
     */
    PageData<AuthSessionVO> pageList(AuthSessionParam param);

    /**
     * 移除session(强退所有)
     */
    void removeSession(AuthSessionParam param);

    /**
     * 移除token(强退指定token)
     */
    void removeToken(AuthSessionParam param);
}
