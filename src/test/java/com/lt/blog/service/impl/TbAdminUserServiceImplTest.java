package com.lt.blog.service.impl;

import com.lt.blog.service.TbAdminUserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * 配置spring和junit整合，junit启动时加载SpringIOC容器
 * spring-test，junit
 */
@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring配置文件
@ContextConfiguration({"classpath:spring/spring-dao.xml",
        "classpath:spring/spring-service.xml"})
public class TbAdminUserServiceImplTest {

    @Autowired
    private TbAdminUserService adminUserService;

    @Test
    public void validatePassword() {

        boolean b = adminUserService.validatePassword(1, "000000");
        System.out.println(b);
    }
}