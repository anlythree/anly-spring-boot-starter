package com.anly.common.conf.interceptor;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 拦截器注册配置类
 */
@Component
public class WebConfig  implements WebMvcConfigurer {

    /**
     * 注册拦截器
     */
    @Resource
    private LocalInterceptor localInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册拦截器，并设置拦截路径
        registry.addInterceptor(localInterceptor).addPathPatterns("/**");
    }
}
