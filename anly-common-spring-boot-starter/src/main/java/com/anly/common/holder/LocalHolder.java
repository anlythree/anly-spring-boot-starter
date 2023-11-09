package com.anly.common.holder;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.anly.common.dto.CurrentUser;
import io.micrometer.common.util.StringUtils;
import lombok.experimental.UtilityClass;
import org.assertj.core.util.Maps;

import java.util.Map;

/**
 * 本地常用变量holder
 * 基于阿里的TransmittableThreadLocal实现
 * 不使用InheritableThreadLocal的原因是：从线程池里获取的话，获取不到父线程中的变量
 * 注意：父子线程之间的变量是共享的！父线程的变量修改后子线程的变量也会修改！
 * @DATE: 2023/7/10
 * @USER: anlythree
 */
@UtilityClass
public class LocalHolder {

    /**
     * 支持父子线程之间的租户id传递
     */
    private final ThreadLocal<String> THREAD_LOCAL_TENANT = new TransmittableThreadLocal<>();

    /**
     * 支持父子线程之间的trace传递
     */
    private final ThreadLocal<String> THREAD_LOCAL_TRACE = new TransmittableThreadLocal<>();

    /**
     * 支持父子线程之间的请求URL传递
     */
    private final ThreadLocal<String> THREAD_LOCAL_URL = new TransmittableThreadLocal<>();

    /**
     * 支持父子线程之间的登陆用户信息传递
     */
    private final ThreadLocal<CurrentUser> THREAD_LOCAL_CURRENT_USER = new TransmittableThreadLocal<>();

    /**
     * 支持父子线程之间的客户端ip传递
     */
    private final ThreadLocal<String> THREAD_LOCAL_REMOTE_IP = new TransmittableThreadLocal<>();

    /**
     * 支持父子线程之间的自定义信息传递
     */
    private final ThreadLocal<Map<String,Object>> THREAD_LOCAL_CUSTOM = new TransmittableThreadLocal<>();

    /**
     * TTL 设置租户ID
     *
     * @param tenantId 租户ID
     */
    public void setTenantId(String tenantId) {
        THREAD_LOCAL_TENANT.set(tenantId);
    }

    /**
     * 获取TTL中的租户ID
     *
     * @return String
     */
    public String getTenantId() {
        return THREAD_LOCAL_TENANT.get();
    }

    /**
     * TTL 设置trace
     *
     * @param trace 流水号
     */
    public void setTrace(String trace) {
        THREAD_LOCAL_TRACE.set(trace);
    }

    /**
     * 获取TTL中的租户ID
     *
     * @return String
     */
    public String getTrace() {
        String trace = THREAD_LOCAL_TRACE.get();
        return StringUtils.isEmpty(trace) ? "EMPTY_TRACE":trace;
    }

    /**
     * 清除tenantId
     */
    public void clearTrace() {
        THREAD_LOCAL_TRACE.remove();
    }

    /**
     * TTL 设置当前用户
     *
     * @param currentUser 当前用户
     */
    public void setCurrentUser(CurrentUser currentUser) {
        THREAD_LOCAL_CURRENT_USER.set(currentUser);
    }

    /**
     * 获取TTL中的当前用户
     *
     * @return String
     */
    public CurrentUser getCurrentUser() {
        return THREAD_LOCAL_CURRENT_USER.get();
    }

    /**
     * 清除当前用户
     */
    public void clearCurrentUser() {
        THREAD_LOCAL_CURRENT_USER.remove();
    }

    /**
     * TTL 设置当前url
     *
     * @param url 当前用户
     */
    public void setUrl(String url) {
        THREAD_LOCAL_URL.set(url);
    }

    /**
     * 获取TTL中的当前url
     *
     * @return String
     */
    public String getUrl() {
        return THREAD_LOCAL_URL.get();
    }

    /**
     * TTL 设置当前请求来源ip
     *
     * @param ip 请求来源ip
     */
    public void setRemoteIp(String ip) {
        THREAD_LOCAL_REMOTE_IP.set(ip);
    }

    /**
     * 获取TTL中的当前请求来源ip
     *
     * @return String
     */
    public String getRemoteIp() {
        return THREAD_LOCAL_REMOTE_IP.get();
    }

    /**
     * TTL 设置自定义变量
     * @param fieldName 自定义变量名称
     * @param obj 自定义变量值
     */
    public void set(String fieldName,Object obj) {
        if(THREAD_LOCAL_CUSTOM.get() == null){
            THREAD_LOCAL_CUSTOM.set(Maps.newHashMap(fieldName,obj));
        }
        THREAD_LOCAL_CUSTOM.get().put(fieldName,obj);
    }

    /**
     * 获取TTL中的自定义变量
     * @param fieldName 自定义变量名称
     * @return 自定义变量值
     */
    public Object get(String fieldName) {
        if(THREAD_LOCAL_CUSTOM.get() == null){
            return null;
        }
        return THREAD_LOCAL_CUSTOM.get().get(fieldName);
    }

    /**
     * 清除当前自定义变量
     * @param fieldName 自定义变量名称
     */
    public void clear(String fieldName) {
        if(THREAD_LOCAL_CUSTOM.get() == null){
            return;
        }
        THREAD_LOCAL_CUSTOM.get().remove(fieldName);
    }
}

