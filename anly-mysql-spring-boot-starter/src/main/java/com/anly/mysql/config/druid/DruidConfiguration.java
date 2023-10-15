package com.anly.mysql.config.druid;

import com.anly.common.factory.YamlPropertySourceFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.PropertySource;

/**
 * Druid配置
 * todo-anlythree 因为druid暂时没有完全适配springboot3,这里不过多配置（2023-10-14），等待之后完成
 *
 * @author anlythree
 */
@AutoConfiguration
@PropertySource(factory = YamlPropertySourceFactory.class, value = "classpath:anly-druid.yml")
public class DruidConfiguration {
	/**
	 * 注册一个StatViewServlet
	 *
	 * @return
	 */
//	@Bean
//	public ServletRegistrationBean DruidStatViewServle() {
//
//		//org.springframework.boot.context.embedded.ServletRegistrationBean提供类的进行注册.
//		ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
//
//		//添加初始化参数：initParams
//
//		//白名单：
//		servletRegistrationBean.addInitParameter("allow", "127.0.0.1");
//
//		//IP黑名单 (存在共同时，deny优先于allow) : 如果满足deny的话提示:Sorry, you are not permitted to view this page.
//		servletRegistrationBean.addInitParameter("deny", "192.168.0.114");
//
//		//登录查看信息的账号密码.
//		servletRegistrationBean.addInitParameter("loginUsername", "admin");
//		servletRegistrationBean.addInitParameter("loginPassword", "matecloud");
//
//		//是否能够重置数据.
//		servletRegistrationBean.addInitParameter("resetEnable", "false");
//		return servletRegistrationBean;
//	}
//
//	/**
//	 * 注册一个：filterRegistrationBean
//	 *
//	 * @return
//	 */
//	@Bean
//	public FilterRegistrationBean druidStatFilter() {
//		FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new WebStatFilter());
//
//		//添加过滤规则.
//		filterRegistrationBean.addUrlPatterns("/*");
//
//		//添加不需要忽略的格式信息.
//		filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
//
//		return filterRegistrationBean;
//	}
}
