package com.anly.common.conf.logback;

/**
 * logback 用于打印trace的分词器
 * @DATE: 2023/10/28
 * @USER: anlythree
 */
import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.anly.common.holder.LocalHolder;
import org.apache.commons.lang3.StringUtils;

public class TraceConverter extends ClassicConverter {

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
