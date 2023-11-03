package com.anly.common.conf.logback;

/**
 * @DATE: 2023/10/28
 * @USER: anlythree
 */
import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.anly.common.context.LocalHolder;
import org.apache.commons.lang3.StringUtils;

public class TraceConverter extends ClassicConverter {

    // todo-anlythree 异步日志添加trace https://blog.csdn.net/HeiheiChihiro/article/details/128023013
    // 实测，异步获取不到session中的信息
    @Override
    public String convert(ILoggingEvent event) {
        try {
            String trace = LocalHolder.getTrace();
            if (StringUtils.isBlank(trace)) {
                return "[no trace]";
            } else {
                return "[trace:" + trace+"]";
            }
        } catch (Exception e) {
            return event.getMessage();
        }
    }
}
