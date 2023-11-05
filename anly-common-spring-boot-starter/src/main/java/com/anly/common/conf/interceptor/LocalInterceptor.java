package com.anly.common.conf.interceptor;

import com.anly.common.holder.LocalHolder;
import com.anly.common.dto.CurrentUser;
import com.anly.common.enums.YesOrNotEnum;
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
            // 添加trace到LocalHolder中
            LocalHolder.setTrace(trace);
        }
        CurrentUser currentUser = new CurrentUser();
        String uId = request.getHeader("uId");
        if(StringUtils.isNotEmpty(uId) && uId.matches("^-?\\d+$")){
            currentUser.setUserId(Integer.parseInt(uId));
            currentUser.setLoginType(YesOrNotEnum.Y);
        }else {
            currentUser.setUserId(-1);
            currentUser.setLoginType(YesOrNotEnum.N);
        }
        // 添加当前用户 & 登陆状态
        LocalHolder.setCurrentUser(currentUser);
        // 添加url到LocalHolder中
        LocalHolder.setUrl(request.getRequestURI());
        // 添加来源Ip到LocalHolder中
        LocalHolder.setRemoteIp(request.getRemoteAddr());
        return true;
    }
}
