package com.anly.common.utils;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;

/**
 * 自定义日志打印类
 *
 * @DATE: 2024/7/5
 * @USER: anlythree
 */
@Slf4j
public class AnlyLog {

    /**
     * 打印json格式的info级别log
     *
     * @param message
     * @param args
     */
    public static void info(String message, Object... args) {
        updateJsonArgsStr(args);
        log.info(message, args);
    }

    /**
     * 打印json格式的error级别log
     *
     * @param message
     * @param args
     */
    public static void error(String message, Object... args) {
        updateJsonArgsStr(args);
        log.error(message, args);
    }

    /**
     * 打印json格式的warn级别log
     *
     * @param message
     * @param args
     */
    public static void warn(String message, Object... args) {
        updateJsonArgsStr(args);
        log.warn(message, args);
    }

    /**
     * 打印json格式的debug级别log
     *
     * @param message
     * @param args
     */
    public static void debug(String message, Object... args) {
        updateJsonArgsStr(args);
        log.debug(message, args);
    }

    /**
     * 修改args为json格式的字符串
     *
     * @param args
     */
    public static void updateJsonArgsStr(Object[] args) {
        if (args == null || args.length == 0) {
            return;
        }
        for (int i = 0; i < args.length; i++) {
            args[i] = "【" + args[i].getClass().getSimpleName() +":"+ JSONObject.toJSONString(args[i]) + "】";
        }

    }
}
