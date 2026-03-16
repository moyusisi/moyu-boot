package com.moyu.boot.common.core.util;

import cn.hutool.extra.servlet.ServletUtil;
import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.xdb.Searcher;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * IP工具类
 * 获取客户端IP地址和IP地址对应的地理位置信息
 *
 * @author shisong
 * @since 2026-03-16
 */
@Slf4j
public class IpUtils {

    private static final String DB_PATH = "/data/ip2region.xdb";
    private static final Searcher searcher;

    // 静态初始化
    static {
        Searcher tmpSearcher = null;
        try {
            // 从类路径加载资源文件
            InputStream inputStream = IpUtils.class.getResourceAsStream(DB_PATH);
            if (inputStream == null) {
                throw new FileNotFoundException("Resource not found: " + DB_PATH);
            }
            // 将资源文件复制到临时文件
            Path tempDbPath = Files.createTempFile("ip2region", ".xdb");
            Files.copy(inputStream, tempDbPath, StandardCopyOption.REPLACE_EXISTING);
            // 使用临时文件初始化 Searcher 对象
            tmpSearcher = Searcher.newWithFileOnly(tempDbPath.toString());
        } catch (Exception e) {
            log.error("IpUtils初始化失败", e);
        }
        // 必须保证tempSearcher非null，否则使用时会抛空指针
        searcher = tmpSearcher;
    }

    /**
     * 获取客户端IP地址
     *
     * @param request HttpServletRequest对象
     * @return 客户端IP地址
     */
    public static String getClientIp(HttpServletRequest request) {
        return ServletUtil.getClientIP(request);
    }

    /**
     * 获取本机的IP地址
     *
     * @return 本机IP地址
     */
    private static String getLocalIp() {
        String localIp = null;
        try {
            localIp = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.error("IpUtils.getLocalIp失败", e);
        }
        return localIp;
    }

    /**
     * 根据IP地址获取地理位置信息
     * 格式：国家|区域|省份|城市|运营商，如：中国|0|四川省|成都市|电信
     *
     * @param ip ip地址
     * @return 地理位置信息
     */
    public static String getRegion(String ip) {
        String region = null;
        if (searcher == null) {
            log.error("searcher为空，无法解析位置信息");
        } else {
            try {
                region = searcher.search(ip);
            } catch (Exception e) {
                log.error("IpUtils.getRegion失败", e);
            }
        }
        return region;
    }
}
