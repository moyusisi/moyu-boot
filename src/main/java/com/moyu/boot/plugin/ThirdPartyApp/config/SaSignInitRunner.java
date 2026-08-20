package com.moyu.boot.plugin.ThirdPartyApp.config;

import cn.dev33.satoken.sign.config.SaSignConfig;
import cn.dev33.satoken.sign.template.SaSignMany;
import cn.dev33.satoken.sign.template.SaSignTemplate;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.moyu.boot.plugin.ThirdPartyApp.model.entity.ThirdPartyApp;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 初始化获取签名配置的方法
 * 容器完全初始化完成后、项目正式对外提供服务前，所有Bean、数据库、Redis全部加载完毕后 执行
 *
 * @author shisong
 * @since 2026-08-20
 */
@Component
public class SaSignInitRunner implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        // 全局覆盖查找签名配置的静态方法，全局生效
        SaSignMany.findSaSignConfigMethod = (appId -> {
            // DB读取
            ThirdPartyApp thirdPartyApp = Db.getOne(Wrappers.lambdaQuery(ThirdPartyApp.class)
                    .eq(ThirdPartyApp::getAppCode, appId));
            if (thirdPartyApp == null) {
                return null;
            }
            return new SaSignConfig(thirdPartyApp.getAppSecret()).setDigestAlgo(thirdPartyApp.getDigestAlgo());
        });
        // 可以自定义签名参数的key,header中的应用标识名为X-AppCode
        SaSignTemplate.nonce = SaSignTemplate.nonce;
    }
}
