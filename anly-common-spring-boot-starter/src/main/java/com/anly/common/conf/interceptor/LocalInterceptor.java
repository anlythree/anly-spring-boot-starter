package com.anly.common.conf.interceptor;

import com.anly.common.context.LocalHolder;
import com.anly.common.dto.CurrentUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 全局http拦截器
 */
@Component
public class LocalInterceptor implements HandlerInterceptor {

    /**
     * 把trace、当前用户信息存到本地线程中
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String trace = request.getHeader("Trace");
        if(StringUtils.isNotEmpty(trace)){
            LocalHolder.setTrace(trace);
        }
        CurrentUser currentUser = new CurrentUser();
        currentUser.setUserName("systemDefault");
        currentUser.setPhone("99999999999");
        currentUser.setUserId(-1);
        currentUser.setLoginType("未登录");
        LocalHolder.setCurrentUser(currentUser);
        return true;
    }
}
