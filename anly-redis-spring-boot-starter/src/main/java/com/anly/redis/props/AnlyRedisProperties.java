package com.anly.redis.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * redis配置
 *
 * @author anlythree
 */
@Getter
@Setter
@ConfigurationProperties(AnlyRedisProperties.PREFIX)
public class AnlyRedisProperties {
	/**
	 * 前缀
	 */
	public static final String PREFIX = "anly.lettuce.redis";
	/**
	 * 是否开启Lettuce
	 */
	private Boolean enable = true;
}
