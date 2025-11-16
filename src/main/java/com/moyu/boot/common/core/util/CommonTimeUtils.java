package com.moyu.boot.common.core.util;


/**
 * 时间格式化工具类
 *
 * @author shisong
 * @since 2025-11-16
 */
public class CommonTimeUtils {

    /**
     * 一天拥有多少秒
     */
    private static final long ONE_DAY_SECONDS = 60 * 60 * 24;
    private static final long ONE_MINUTE_SECONDS = 60;

    /**
     * 读秒倒计时格式化
     **/
    public static String countdownFormat(long seconds) {
        String result;
        long days = seconds / (ONE_DAY_SECONDS);
        long hours = (seconds % (ONE_DAY_SECONDS)) / (ONE_MINUTE_SECONDS * ONE_MINUTE_SECONDS);
        long minutes = (seconds % (ONE_MINUTE_SECONDS * ONE_MINUTE_SECONDS)) / ONE_MINUTE_SECONDS;
        long second = seconds % ONE_MINUTE_SECONDS;
        if (days > 0) {
            result = days + "天" + hours + "小时" + minutes + "分" + second + "秒";
        } else if (hours > 0) {
            result = hours + "小时" + minutes + "分" + second + "秒";
        } else if (minutes > 0) {
            result = minutes + "分" + second + "秒";
        } else {
            result = second + "秒";
        }
        return result;
    }
}
