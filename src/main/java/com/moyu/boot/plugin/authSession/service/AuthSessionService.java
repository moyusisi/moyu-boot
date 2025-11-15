package com.moyu.boot.plugin.authSession.service;


import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.plugin.authSession.model.param.AuthSessionParam;
import com.moyu.boot.plugin.authSession.model.vo.AuthSessionVO;
import com.moyu.boot.plugin.sysLog.model.param.SysLogParam;
import com.moyu.boot.plugin.sysLog.model.vo.SysLogVO;

/**
 * 会话管理服务类Service
 *
 * @author shisong
 * @since 2025-11-15
 */
public interface AuthSessionService {

    /**
     * 分页获取记录列表
     */
    PageData<AuthSessionVO> pageList(AuthSessionParam param);

}
