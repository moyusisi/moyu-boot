package com.moyu.boot;

import cn.dev33.satoken.sign.SaSignManager;
import cn.dev33.satoken.sign.config.SaSignConfig;
import cn.dev33.satoken.sign.template.SaSignMany;
import cn.dev33.satoken.sign.template.SaSignUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SmUtil;
import com.google.common.base.CaseFormat;
import com.moyu.boot.common.core.util.IpUtils;
import com.moyu.boot.system.constant.SysConstants;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.iv.RandomIvGenerator;
import org.jasypt.salt.RandomSaltGenerator;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 本地测试类
 *
 * @author shisong
 * @since 2024-11-25
 */
@Slf4j
public class UnitTest {

    @Test
    public void test() {
        log.info("测试基类正确执行，不要改");
    }

    @Test
    public void testCamelCase() {
        String underscoreName = "Hello_world_DTO";
        String camelCaseName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, underscoreName);
        log.info(camelCaseName);
    }

    @SneakyThrows
    @Test
    public void testId() {
        // 24位: 67c7b60fd19001d4b33539b6
        log.info(IdUtil.objectId());
        // 21位随机NanoId: R06PP2RUZeS9j6g0bNxyW
        log.info(IdUtil.nanoId());
        // 指定位数: g4Pq_XFDRq
        log.info(IdUtil.nanoId(10));
        // 32位: 512e1c1d55b14cd2ad107e8150d0ac26
        log.info(IdUtil.simpleUUID());
        // 32位有横线: c577c28c-aa92-460f-a3c1-bfaa0c59d2ba
        log.info(IdUtil.fastUUID());
        // 19个数 Long: 1897111148578750464
        log.info("Long:{}", IdUtil.getSnowflakeNextId());
        // 随机: Jg3Q3QvK3V
        log.info(RandomUtil.randomString(10));
    }

    @Test
    public void testIp() {
        log.info(IpUtils.getRegion("127.0.0.1"));
    }

    @Test
    public void testEncode() {
        String sm4Key = "KeyMustBe16Size.";
        byte[] key = sm4Key.getBytes(StandardCharsets.UTF_8);

        String rawPassword = SysConstants.DEFAULT_PASSWORD;
        String pwd = SmUtil.sm4(key).encryptHex(rawPassword);

        String encodedPassword = "5b0b3e32ecc28623dc0f9af02227f27c";
        String plain = SmUtil.sm4(key).decryptStr(encodedPassword, StandardCharsets.UTF_8);

        log.info(plain);
    }


    /**
     * 构建加密器
     */
    private StandardPBEStringEncryptor buildEncryptor() {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword("mySecretKey");
        encryptor.setAlgorithm("PBEWITHHMACSHA512ANDAES_256");
        encryptor.setSaltGenerator(new RandomSaltGenerator());
        encryptor.setIvGenerator(new RandomIvGenerator());
        return encryptor;
    }

    @Test
    public void testJasyptEncode() {
        // 加密器
        StandardPBEStringEncryptor encryptor = buildEncryptor();

        // 加解密示例
        String plainText = "123456";
        String encryptedText = encryptor.encrypt(plainText);
        log.info("encryptedText:{}", encryptedText);
        encryptedText = "RpUCoqFV5RXBxCHYVFcdKn1Nb13rCbhOXWhHDk2hDDHe79e8IoQia3cejJD2Hn6o";
        plainText = encryptor.decrypt(encryptedText);
        log.info("plainText:{}", plainText);
    }

    @Test
    public void testSign() {
        SaSignConfig signConfig = new SaSignConfig("0123456789hijklmnopq");
        SaSignManager.setConfig(signConfig);

        Map<String, SaSignConfig> signMany = new HashMap<>();
        signMany.put("app1", signConfig);
        SaSignManager.setSignMany(signMany);

        // 请求参数
        Map<String, Object> paramMap = new LinkedHashMap<>();
        paramMap.put("userId", 10001);
        paramMap.put("money", 1000);
        // 补全 timestamp、nonce、sign 参数，并序列化为 kv 字符串
        String paramStr = SaSignUtil.addSignParamsAndJoin(paramMap);
        String paramStr2 = SaSignMany.getSignTemplate("app1").addSignParamsAndJoin(paramMap);
        Map<String, Object> paramStr3 = SaSignMany.getSignTemplate("app1").addSignParams(paramMap);
        log.info("paramStr:{}", paramStr);
        log.info("paramStr2:{}", paramStr2);
        log.info("paramStr2:{}", paramStr3);
//        SaSignUtil.checkRequest(SaHolder.getRequest(), "id", "name");

    }
}
