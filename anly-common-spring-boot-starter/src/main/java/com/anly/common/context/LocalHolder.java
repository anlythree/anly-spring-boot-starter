package com.anly.common.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.anly.common.dto.CurrentUser;
import lombok.experimental.UtilityClass;

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

    private final ThreadLocal<String> THREAD_LOCAL_TRACE = new TransmittableThreadLocal<>();

    private final ThreadLocal<String> THREAD_LOCAL_URL = new TransmittableThreadLocal<>();

    private final ThreadLocal<CurrentUser> THREAD_LOCAL_CURRENT_USER = new TransmittableThreadLocal<>();

    /**
     * TTL 设置租户ID<br/>
     * <b>谨慎使用此方法,避免嵌套调用 </b>
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
     * 清除tenantId
     */
    public void clearTenantId() {
        THREAD_LOCAL_TENANT.remove();
    }

    /**
     * TTL 设置trace<br/>
     * <b>谨慎使用此方法,避免嵌套调用 </b>
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
        return THREAD_LOCAL_TRACE.get();
    }

    /**
     * 清除tenantId
     */
    public void clearTrace() {
        THREAD_LOCAL_TRACE.remove();
    }

    /**
     * TTL 设置当前用户<br/>
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
     * TTL 设置当前url<br/>
     *
     * @param url 当前用户
     */
    public void setCurrentUser(String url) {
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
     * 清除当前url
     */
    public void clearUrl() {
        THREAD_LOCAL_URL.remove();
    }

}

