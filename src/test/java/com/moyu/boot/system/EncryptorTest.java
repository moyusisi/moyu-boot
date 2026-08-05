package com.moyu.boot.system;

import com.moyu.boot.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;

/**
 * @author shisong
 * @since 2026-08-05
 */
@Slf4j
public class EncryptorTest extends BaseTest {

    @Resource
    private StringEncryptor stringEncryptor;


    @Test
    public void testJasypt() {
        String plainText = "123456";
        String encryptedText = stringEncryptor.encrypt(plainText);
        log.info("encryptedText:{}", encryptedText);
        encryptedText = "NaqrHWRoyjVZBM327RZnh2yOWJzToBRlscchwHlm9fWthWM3TJOeqKASs49SoiMt";
        plainText = stringEncryptor.decrypt(encryptedText);
        log.info("plainText:{}", plainText);
    }
}
