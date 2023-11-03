package com.anly.common.aspect;

import com.alibaba.fastjson2.JSONObject;
import com.anly.common.aspect.anno.OperationLog;
import com.anly.common.context.LocalHolder;
import com.anly.common.utils.AnlyTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @DATE: 2023/11/3
 * @USER: anlythree
 */
@Aspect
@Component
@Slf4j
public class OperationLogAspect {

    @Pointcut("@annotation(operationLog)")
    private void recordLogAspect(OperationLog operationLog){}

    /**
     * 目标方法执行成功
     * 日志切面:在关键操作前记录日志
     *
     * @param
     * @return
     */
    @Around("recordLogAspect(operationLog)")
    public Object executeReturnning(ProceedingJoinPoint jp, OperationLog operationLog) throws Throwable {
        // 记录开始时间 & 打印请求信息
        LocalDateTime startTime = LocalDateTime.now();
        log.info("request-->url:["+ LocalHolder.getUrl() +"],description:["+operationLog.description()+"],param:"+ JSONObject.toJSONString(jp.getArgs()));
        Object proceed = jp.proceed();
        // 计算相应时间 & 打印相应信息
        log.info("response-->url:["+ LocalHolder.getUrl() +"],description:["+operationLog.description()+"],time used:["+ AnlyTimeUtil.timeInterval(startTime).toMillis() +"ms],result:"+ JSONObject.toJSONString(proceed));
        return proceed;
    }
}
