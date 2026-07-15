package com.moyu.boot.plugin.authSession.service;

import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.plugin.authSession.model.param.AuthSessionParam;
import com.moyu.boot.plugin.authSession.model.vo.AuthSessionAnalysisVO;
import com.moyu.boot.plugin.authSession.model.vo.AuthSessionVO;
import com.moyu.boot.plugin.authSession.model.vo.SignTokenVO;

import java.util.List;

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
     * 获取指定用户loginId的令牌列表
     */
    List<SignTokenVO> tokenList(String loginId);

    /**
     * 移除token(强退指定token)
     */
    void removeToken(AuthSessionParam param);

    /**
     * 续签指定Token：(将 [最后操作时间] 更新为当前时间)
     */
    void renewActive(String tokenValue);
}
