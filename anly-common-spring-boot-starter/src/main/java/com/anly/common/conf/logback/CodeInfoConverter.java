package com.anly.common.conf.logback;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.anly.common.utils.AnlyLog;

/**
 * logback用于打印代码信息的分词器
 * @DATE: 2024/7/9
 * @USER: anlythree
 */
public class CodeInfoConverter extends ClassicConverter {

    @Override
    public String convert(ILoggingEvent event) {
        try {
            String codeInfo = "";
            StackTraceElement[] stackTraceElements = event.getCallerData();
            if(AnlyLog.class.getName().equals(stackTraceElements[0].getClassName())){
                // AnlyLogUtil类打印时，获取调用工具类的代码信息
                codeInfo = getSimpleName(stackTraceElements[1].getClassName())+":"+stackTraceElements[1].getLineNumber();
            }else {
                // 除了AnlyLogUtil类打印都直接获取当前堆栈代码信息
                codeInfo = getSimpleName(stackTraceElements[0].getClassName())+":"+stackTraceElements[0].getLineNumber();
            }
            return "(" + codeInfo + ")";
        } catch (Exception e) {
            return event.getMessage();
        }
    }

    /**
     *  通过className获取simpleName
     * @param className
     * @return
     */
    public String getSimpleName(String className){
        return className.substring(className.lastIndexOf(".")+1);
    }
}
