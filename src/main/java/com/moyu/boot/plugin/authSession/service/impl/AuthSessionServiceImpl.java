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
import com.moyu.boot.plugin.authSession.model.vo.SignTokenVO;
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
            AuthSessionVO sessionVO = new AuthSessionVO();
            sessionVO.setLoginId(loginId);
            sessionVO.setName(saSession.get("name", ""));
            // sessionId为 Authorization:login:session:loginId
            sessionVO.setSessionId(saSession.getId());
            sessionVO.setSessionCreateTime(new Date(saSession.getCreateTime()));
            sessionVO.setSessionTimeout(saSession.timeout());
            // 配置的过期时长
            long configTimeout = SaManager.getConfig().getTimeout();
            long sessionTimeout = sessionVO.getSessionTimeout();
            // 剩余时间百分比
            if (sessionTimeout == -1) {
                sessionVO.setDeadline(DateTime.now().plusDays(100).toDate());
                sessionVO.setSessionTimeoutPercent(1d);
            } else {
                sessionVO.setDeadline(DateTime.now().plusSeconds(Convert.toInt(sessionTimeout)).toDate());
                if (configTimeout == -1) {
                    sessionVO.setSessionTimeoutPercent(1d);
                } else {
                    sessionVO.setSessionTimeoutPercent(NumberUtil.div(sessionTimeout, configTimeout));
                }
            }
            // 并发登录数 受到 isConcurrent 和 maxLoginCount 影响，超限将会主动注销第一个登录的会话（先进先出）
            List<SaTerminalInfo> terminalList = saSession.getTerminalList().stream()
                    .filter(terminalInfo -> {
                        // 获取指定 token 剩余有效时间（单位: 秒，返回 -1 代表永久有效，-2 代表没有这个值）
                        long tokenTimeout = StpUtil.getTokenTimeout(terminalInfo.getTokenValue());
                        // 过滤掉不存在的
                        return tokenTimeout != -2;
                    }).collect(Collectors.toList());
            sessionVO.setTokenCount(terminalList.size());
            long createTime = terminalList.get(sessionVO.getTokenCount() - 1).getCreateTime();
            sessionVO.setLastLoginTime(new Date(createTime));
            voList.add(sessionVO);
        });
        return new PageData<>(Convert.toLong(voList.size()), voList);
    }

    @Override
    public void removeSession(AuthSessionParam param) {
        param.getCodes().forEach(StpUtil::logout);
    }

    @Override
    public List<SignTokenVO> tokenList(String loginId) {
        // 并发登录数 受到 isConcurrent 和 maxLoginCount 影响，超限将会主动注销第一个登录的会话（先进先出）
        List<SaTerminalInfo> terminalList = StpUtil.getTerminalListByLoginId(loginId);
        List<SignTokenVO> tokenList = terminalList.stream()
                .filter(terminalInfo -> {
                    // 获取指定 token 剩余有效时间（单位: 秒，返回 -1 代表永久有效，-2 代表没有这个值）
                    long tokenTimeout = StpUtil.getTokenTimeout(terminalInfo.getTokenValue());
                    // 过滤掉不存在的
                    return tokenTimeout != -2;
                })
                .map(terminalInfo -> {
                    String tokenValue = terminalInfo.getTokenValue();
                    SignTokenVO tokenVO = new SignTokenVO();
                    tokenVO.setTokenValue(tokenValue);
                    tokenVO.setTokenDevice(terminalInfo.getDeviceType());
                    tokenVO.setCreateTime(new Date(terminalInfo.getCreateTime()));

                    // 配置的过期时长
                    long configTimeout = SaManager.getConfig().getTimeout();
                    long tokenTimeout = StpUtil.getTokenTimeout(tokenValue);
                    tokenVO.setTokenTimeout(tokenTimeout);
                    if (tokenTimeout == -1) {
                        tokenVO.setDeadline(DateTime.now().plusDays(100).toDate());
                        tokenVO.setTokenTimeoutPercent(1d);
                    } else {
                        tokenVO.setDeadline(DateTime.now().plusSeconds(Convert.toInt(tokenTimeout)).toDate());
                        if (configTimeout == -1) {
                            tokenVO.setTokenTimeoutPercent(1d);
                        } else {
                            tokenVO.setTokenTimeoutPercent(NumberUtil.div(tokenTimeout, configTimeout));
                        }
                    }

                    // 获取指定 token 的最后活跃时间
                    long tokenLastActiveTime = StpUtil.getStpLogic().getTokenLastActiveTime(tokenValue);
                    if (tokenLastActiveTime > 0) {
                        tokenVO.setLastActiveTime(new Date(tokenLastActiveTime));
                    }
                    // 配置的闲置冻结时长activeTimeout
                    long activeTimeout = StpUtil.getStpLogic().getTokenUseActiveTimeoutOrGlobalConfig(tokenValue);
                    // 获取指定 token 剩余活跃有效期
                    long tokenActiveTimeout = StpUtil.getStpLogic().getTokenActiveTimeoutByToken(tokenValue);
                    tokenVO.setActiveTimeout(tokenActiveTimeout);
                    if (tokenActiveTimeout == -1) {
                        tokenVO.setActiveTimeoutDeadline(DateTime.now().plusDays(100).toDate());
                        tokenVO.setActiveTimeoutPercent(1d);
                    } else if (tokenActiveTimeout == -2) {
                        tokenVO.setActiveTimeoutDeadline(DateTime.now().toDate());
                        tokenVO.setActiveTimeoutPercent(0d);
                    } else {
                        tokenVO.setActiveTimeoutDeadline(DateTime.now().plusSeconds(Convert.toInt(tokenActiveTimeout)).toDate());
                        tokenVO.setActiveTimeoutPercent(NumberUtil.div(tokenActiveTimeout, activeTimeout));
                    }
                    return tokenVO;
                }).collect(Collectors.toList());

        return tokenList;
    }

    @Override
    public void removeToken(AuthSessionParam param) {
        param.getCodes().forEach(StpUtil::logoutByTokenValue);
    }

    @Override
    public void renewActive(String tokenValue) {
        // 为指定 Token 续签
        StpUtil.stpLogic.updateLastActiveToNow(tokenValue);
    }
}
