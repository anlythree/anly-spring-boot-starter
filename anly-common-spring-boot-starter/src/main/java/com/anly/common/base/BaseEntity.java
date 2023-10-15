package com.anly.common.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 实体类基类
 * @DATE: 2023/8/14
 * @USER: anlythree
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseEntity {

    private Integer id;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer creatorId;

    private Integer tenantId;

    private String lockVersion;

}
