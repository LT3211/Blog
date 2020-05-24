package com.lt.blog.service.impl;

import com.lt.blog.service.TbBlogTagService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring配置文件
@ContextConfiguration({"classpath:spring/spring-dao.xml",
        "classpath:spring/spring-service.xml"})
public class TbBlogTagServiceImplTest {

    @Autowired
    private TbBlogTagService blogTagService;

    @Test
    public void getBlogTagCountForIndex() {
        System.out.println( blogTagService.getBlogTagCountForIndex());
    }
}