package com.anly.mysql.config.mybatis;

import com.anly.mysql.config.mybatis.injector.AnlySqlInjector;
import com.anly.mysql.config.mybatis.interceptor.SqlLogInterceptor;
import com.anly.mysql.config.props.AnlyMybatisProperties;
import com.anly.common.factory.YamlPropertySourceFactory;
import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import lombok.AllArgsConstructor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;


/**
 * mybatis plus配置中心
 *
 * @author anlythree
 */
@Configuration
@AllArgsConstructor
@EnableTransactionManagement
@EnableConfigurationProperties(AnlyMybatisProperties.class)
@PropertySource(factory = YamlPropertySourceFactory.class, value = "classpath:anly-mybatisplus.yml")
@MapperScan(value = {"com.anlythree.**.mapper.**","com.anlythree.**.business.**.dao"})
public class MybatisPlusConfiguration {


	/**
	 * sql 注入
	 */
	@Bean
	public ISqlInjector sqlInjector() {
		return new AnlySqlInjector();
	}

	/**
	 * sql 日志
	 */
	@Bean
//	@Profile({"local", "dev", "test"})
	@ConditionalOnProperty(value = "anly.mybatis.sql")
	public SqlLogInterceptor sqlLogInterceptor() {
		return new SqlLogInterceptor();
	}



}
