package com.anly.common.exception.handler;

import com.anly.common.api.Result;
import com.anly.common.api.ResultCode;
import com.anly.common.exception.AnlyScaException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;
import java.util.StringJoiner;

/**
 * 全局异常处理
 *
 * @DATE: 2023/7/27
 * @USER: anlythree
 */
@Slf4j
@ControllerAdvice
public class AnlyScaExceptionHandler {

    /**
     * 参数校验异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public Result<?> handleParamException(MethodArgumentNotValidException e) {
        //封装需要返回的错误信息
        StringJoiner stringJoiner = new StringJoiner(",", " find param error from anly\n【", "】");
        e.getBindingResult().getFieldErrors().forEach(fieldError -> stringJoiner.add(fieldError.getField() + ":" + fieldError.getDefaultMessage()));
        log.error("MethodArgumentNotValidException!,error info:{}",stringJoiner);
        //错误返回
        return Result.fail(ResultCode.GLOBAL_PARAM_ERROR.getCode(), stringJoiner.toString());
    }


    /**
     * 自定义异常处理
     *
     * @param e
     * @return
     */
    @ExceptionHandler(AnlyScaException.class)
    @ResponseBody
    public Result<?> handleException(AnlyScaException e) {
        HttpServletRequest httpRequest = getHttpRequest();
        httpRequest.getRequestURI();
        log.error("AnlyScaException! Trace:{},complete stacktrace from anly【\n{}】",getTrace(),getStackTrace(e));
        ResultCode resultCode = Optional.ofNullable(e.getErrorCode()).orElse(ResultCode.FAILURE);
        return Result.fail(resultCode);
    }

    /**
     * 兜底异常处理
     *
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result<?> handleException(Exception e) {
        HttpServletRequest httpRequest = getHttpRequest();
        httpRequest.getRequestURI();
        log.error("Exception! Trace:{},complete stacktrace from anly【\n{}】",getTrace(),getStackTrace(e));
        // 其他系统异常
        return Result.fail(ResultCode.ERROR);
    }


    protected HttpServletRequest getHttpRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return Optional.ofNullable(attributes).map(ServletRequestAttributes::getRequest).orElse(null);
    }

    protected String getTrace() {
        return this.getHttpRequest().getHeader("TRACE");
    }

    /**
     * 获取异常的堆栈信息
     *
     * @param throwable
     * @return
     */
    public static String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        try {
            throwable.printStackTrace(pw);
            return sw.toString();
        } finally {
            pw.close();
        }
    }

}
