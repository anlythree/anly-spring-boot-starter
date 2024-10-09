package com.anly.common.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * dto基类
 * @DATE: 2023/8/14
 * @USER: anlythree
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseDto {
    /**
     * id
     */
    private Integer id;

    /**
     * 创建者id
     */
    private Integer creatorId;

    /**
     * 租户id
     */
    private Integer tenantId;

    /**
     * 乐观锁
     */
    private String lockVersion;
}
