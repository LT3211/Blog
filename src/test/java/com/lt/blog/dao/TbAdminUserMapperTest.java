package com.lt.blog.dao;

import com.lt.blog.entity.TbAdminUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

/**
 * 配置spring和junit整合，junit启动时加载SpringIOC容器
 * spring-test，junit
 */
@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring配置文件
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class TbAdminUserMapperTest {


    @Autowired
    private TbAdminUserMapper tbAdminUserMapper;

    @Test
    public void findAById() {
        TbAdminUser tbAdminUser = tbAdminUserMapper.selectById("1");
        System.out.println(tbAdminUser);
    }
}