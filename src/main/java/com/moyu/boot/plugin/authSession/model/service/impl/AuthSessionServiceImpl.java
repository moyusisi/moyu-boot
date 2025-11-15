package com.moyu.boot.plugin.authSession.model.service.impl;


import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.plugin.authSession.model.param.AuthSessionParam;
import com.moyu.boot.plugin.authSession.model.service.AuthSessionService;
import com.moyu.boot.plugin.authSession.model.vo.AuthSessionVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 会话管理服务实现类
 *
 * @author shisong
 * @since 2025-11-15
 */
@Slf4j
@Service
public class AuthSessionServiceImpl implements AuthSessionService {

    @Override
    public PageData<AuthSessionVO> pageList(AuthSessionParam param) {
        String keyword = param.getSearchKey();
        int total = StpUtil.searchSessionId(keyword, 0, -1, true).size();
        List<AuthSessionVO> voList = new ArrayList<>();
        if (total <= 0) {
            return new PageData<>(0L, voList);
        }
        List<String> loginIdList = StpUtil.searchSessionId(keyword, (param.getPageNum() - 1) * param.getPageSize(), param.getPageSize(), true).stream()
                // 格式为 Authorization:login:session:loginId 取出loginId
                .map(sessionId -> StrUtil.split(sessionId, StrUtil.COLON).get(3))
                .collect(Collectors.toList());
        loginIdList.forEach(loginId -> {
            SaSession saSession = StpUtil.getSessionByLoginId(loginId, false);
            AuthSessionVO vo = new AuthSessionVO();
            vo.setAccount(loginId);
            // sessionId为 Authorization:login:session:loginId
            vo.setSessionId(saSession.getId());
            vo.setSessionCreateTime(new Date(saSession.getCreateTime()));
            long sessionTimeOut = saSession.timeout();
            if (sessionTimeOut == -1) {
                vo.setSessionTimeout("永久");
            } else {
                vo.setSessionTimeout(sessionTimeOut + "秒");
            }
        });
        return null;
    }
}
