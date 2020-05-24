package com.lt.blog.service;

import com.lt.blog.controller.vo.SimpleBlogListVO;
import com.lt.blog.entity.TbBlogInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 博客信息表 服务类
 * </p>
 *
 * @author liteng
 * @since 2020-05-21
 */
public interface TbBlogInfoService extends IService<TbBlogInfo> {


    /**
     * 返回最新的五条文章列表
     * @return
     */
    List<SimpleBlogListVO> getNewBlog();

    /**
     * 返回点击最多的五条文章列表
     * @return
     */
    List<SimpleBlogListVO> getHotBlog();

    /**
     * 清除文章
     * @param blogId
     * @return
     */
    boolean clearBlogInfo(Long blogId);

}
