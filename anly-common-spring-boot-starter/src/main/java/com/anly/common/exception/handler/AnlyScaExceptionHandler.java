package com.anly.common.exception.handler;

import com.alibaba.fastjson2.JSONObject;
import com.anly.common.api.Result;
import com.anly.common.api.ResultCode;
import com.anly.common.exception.AnlyException;
import com.anly.common.exception.AuthException;
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
        StringJoiner stringJoiner = new StringJoiner(",", " find param error \n【", "】");
        e.getBindingResult().getFieldErrors().forEach(fieldError -> stringJoiner.add(fieldError.getField() + ":" + fieldError.getDefaultMessage()));
        log.error("MethodArgumentNotValidException!,error info:{}",stringJoiner);
        //错误返回
        Result<Object> fail = Result.fail(ResultCode.PARAM_ERROR.getCode(), stringJoiner.toString());
        log.error("error response:"+ JSONObject.toJSONString(fail));
        return fail;
    }

    /**
     * 认证异常处理
     *
     * @param e
     * @return
     */
    @ExceptionHandler(AuthException.class)
    @ResponseBody
    public Result<?> handleAuthException(AuthException e) {
        HttpServletRequest httpRequest = getHttpRequest();
        httpRequest.getRequestURI();
        log.error("AuthException! authType:{},authMessage:{},complete stacktrace from anly【\n{}】",
                e.getAuthType(),e.getMessage(),getStackTrace(e));
        ResultCode resultCode = Optional.ofNullable(e.getErrorCode()).orElse(ResultCode.FAILURE);
        Result<Object> fail = Result.fail(resultCode, e.getMessage());
        log.error("error response:"+ JSONObject.toJSONString(fail));
        return fail;
    }


    /**
     * 自定义异常处理
     *
     * @param e
     * @return
     */
    @ExceptionHandler(AnlyException.class)
    @ResponseBody
    public Result<?> handleAnlyException(AnlyException e) {
        HttpServletRequest httpRequest = getHttpRequest();
        httpRequest.getRequestURI();
        String originExceptionStr = "";
        if(e.getOriginException() != null){
            // 如果有异常的原始报错，则在结尾展示
            originExceptionStr = "complete origin exception is 【"+ getStackTrace(e.getOriginException())+"】";
        }
        log.error("AnlyException! complete stacktrace from anly【\n{}】"+ originExceptionStr+e.getMessage(),getStackTrace(e));
        ResultCode resultCode = Optional.ofNullable(e.getErrorCode()).orElse(ResultCode.FAILURE);
        Result<Object> fail = Result.fail(resultCode, e.getMessage());
        log.error("error response:"+ JSONObject.toJSONString(fail));
        return fail;
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
        log.error("Exception! complete stacktrace from anly【\n{}】",getStackTrace(e));
        // 其他系统异常
        Result<Object> fail = Result.fail(ResultCode.ERROR);
        log.error("error response:"+ JSONObject.toJSONString(fail));
        return fail;
    }


    protected HttpServletRequest getHttpRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return Optional.ofNullable(attributes).map(ServletRequestAttributes::getRequest).orElse(null);
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
