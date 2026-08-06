package com.moyu.boot.plugin.jasypt.model.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * jasypt的加解密参数
 *
 * @author shisong
 * @since 2026-08-06
 */
@Data
public class EncryptorParam {

    /**
     * PEB的密钥
     */
    @NotBlank(message = "密钥不能为空")
    private String secretKey;

    /**
     * 算法（<a href="https://github.com/jas502n/database-jasypt">可参考</a>）
     * 常见如下:
     * PBEWithMD5AndDES
     * PBEWithSHA1AndDESede
     * PBEWithSHA1AndRC2_40
     * PBEWithSHA1AndRC2_128
     * PBEWithSHA1AndRC4_40
     * PBEWithSHA1AndRC4_128
     * PBEWithMD5AndTripleDES
     * PBEWithHmacSHA1AndAES_128
     * PBEWithHmacSHA224AndAES_128
     * PBEWithHmacSHA256AndAES_128
     * PBEWithHmacSHA384AndAES_128
     * PBEWithHmacSHA512AndAES_128
     * PBEWithHmacSHA1AndAES_256
     * PBEWithHmacSHA224AndAES_256
     * PBEWithHmacSHA256AndAES_256
     * PBEWithHmacSHA384AndAES_256
     * PBEWithHmacSHA512AndAES_256
     */
    @NotBlank(message = "算法不能为空")
    private String algorithm;

    /**
     * 明文
     */
    private String plainText;

    /**
     * 密文
     */
    private String encryptedText;
}
