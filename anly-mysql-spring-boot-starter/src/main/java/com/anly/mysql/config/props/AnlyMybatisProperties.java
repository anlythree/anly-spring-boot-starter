package com.anly.mysql.config.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * 日志打印配置
 *
 * @author anlythree
 */
@Getter
@Setter
@RefreshScope
@ConfigurationProperties("anly.mybatis")
public class AnlyMybatisProperties {
    /**
     * 是否打印可执行 sql
     */
    private boolean sql = true;
}
