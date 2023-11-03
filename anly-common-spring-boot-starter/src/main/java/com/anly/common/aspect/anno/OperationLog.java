package com.anly.common.aspect.anno;

import java.lang.annotation.*;

/**
 * @DATE: 2023/11/3
 * @USER: anlythree
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {
    /**
     * 描述
     * @return
     */
    String description() default "";
}
