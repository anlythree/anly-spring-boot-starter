package com.anly.runner;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;


/**
 * 读取配置文件中的spring.application.name并在项目初始化时在控制台打印启动成功信息
 *
 * @author anlythree
 * @date 2020/12/23 9:32
 */
@ComponentScan("com.anlythree.**")
@Component
public class PrintApplicationNameRunner implements ApplicationRunner {


    @Value("${spring.application.name}")
    private String applicationName;

    @Override
    public void run(ApplicationArguments args) {
        System.out.println(applicationName+" is working!!! :）");
    }
}

