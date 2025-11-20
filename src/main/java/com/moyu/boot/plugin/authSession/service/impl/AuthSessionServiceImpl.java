package com.moyu.boot.plugin.authSession.service.impl;


import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.session.SaTerminalInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.plugin.authSession.model.param.AuthSessionParam;
import com.moyu.boot.plugin.authSession.model.vo.AuthSessionAnalysisVO;
import com.moyu.boot.plugin.authSession.model.vo.AuthSessionVO;
import com.moyu.boot.plugin.authSession.service.AuthSessionService;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
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
    public AuthSessionAnalysisVO analyse() {
        AuthSessionAnalysisVO vo = new AuthSessionAnalysisVO();
        vo.setMaxTokenCount(0);
        List<String> sessionList = StpUtil.searchSessionId("", 0, -1, true);
        List<String> tokenList = StpUtil.searchTokenValue("", 0, -1, true);
        vo.setSessionTotalCount(sessionList.size());
        vo.setTokenTotalCount(tokenList.size());
        int todayTokenCount = 0;
        for (String sessionId : sessionList) {
            List<SaTerminalInfo> terminalList = StpUtil.getSessionBySessionId(sessionId).getTerminalList();
            int tokenCount = terminalList.size();
            if (tokenCount > vo.getMaxTokenCount()) {
                vo.setMaxTokenCount(tokenCount);
            }
            for (SaTerminalInfo terminal : terminalList) {
                DateTime yesterdayEnd = DateTime.now().minusDays(1).millisOfDay().withMaximumValue();
                if (yesterdayEnd.isBefore(terminal.getCreateTime())) {
                    todayTokenCount++;
                }
            }
        }
        vo.setTodayTokenCount(todayTokenCount);
        return vo;
    }

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
            vo.setName(saSession.get("name", ""));
            // sessionId为 Authorization:login:session:loginId
            vo.setSessionId(saSession.getId());
            vo.setSessionCreateTime(new Date(saSession.getCreateTime()));
            vo.setSessionTimeout(saSession.timeout());
            vo.setDeadline(DateTime.now().plusSeconds(Convert.toInt(saSession.timeout())).toDate());
            // 并发登录数 受到 isConcurrent 和 maxLoginCount 影响，超限将会主动注销第一个登录的会话（先进先出）
            List<SaTerminalInfo> terminalList = saSession.getTerminalList();
            List<AuthSessionVO.SignTokenInfo> tokenInfoList = terminalList.stream()
                    .filter(terminalInfo -> {
                        // 获取指定 token 剩余有效时间（单位: 秒，返回 -1 代表永久有效，-2 代表没有这个值）
                        long tokenTimeout = StpUtil.getTokenTimeout(terminalInfo.getTokenValue());
                        // 过滤掉不存在的
                        return tokenTimeout != -2;
                    })
                    .map(terminalInfo -> {
                        AuthSessionVO.SignTokenInfo tokenInfo = new AuthSessionVO.SignTokenInfo();
                        tokenInfo.setTokenValue(terminalInfo.getTokenValue());
                        tokenInfo.setTokenDevice(terminalInfo.getDeviceType());
                        tokenInfo.setCreateTime(new Date(terminalInfo.getCreateTime()));
                        long tokenTimeoutConfig = SaManager.getConfig().getTimeout();
                        long tokenTimeout = StpUtil.getTokenTimeout(terminalInfo.getTokenValue());
                        tokenInfo.setTokenTimeout(tokenTimeout);
                        if (tokenTimeout == -1) {
                            tokenInfo.setDeadline(DateTime.now().plusDays(100).toDate());
                            tokenInfo.setTokenTimeoutPercent(100d);
                        } else {
                            tokenInfo.setDeadline(DateTime.now().plusSeconds(Convert.toInt(tokenTimeout)).toDate());
                            if (tokenTimeoutConfig == -1) {
                                tokenInfo.setTokenTimeoutPercent(1d);
                            } else {
                                tokenInfo.setTokenTimeoutPercent(NumberUtil.div(tokenTimeout, tokenTimeoutConfig));
                            }
                        }
                        return tokenInfo;
                    }).collect(Collectors.toList());

            vo.setTokenCount(tokenInfoList.size());
            vo.setTokenList(tokenInfoList);
            vo.setLatestLoginTime(tokenInfoList.get(vo.getTokenCount() - 1).getCreateTime());
            voList.add(vo);
        });
        return new PageData<>(Convert.toLong(voList.size()), voList);
    }

    @Override
    public void removeSession(AuthSessionParam param) {
        param.getCodes().forEach(StpUtil::logout);
    }

    @Override
    public void removeToken(AuthSessionParam param) {
        param.getCodes().forEach(StpUtil::logoutByTokenValue);
    }
}
