package com.lt.blog.service;

import com.lt.blog.entity.BlogTagCount;
import com.lt.blog.entity.TbBlogTag;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 标签表 服务类
 * </p>
 *
 * @author liteng
 * @since 2020-05-21
 */
public interface TbBlogTagService extends IService<TbBlogTag> {

    List<BlogTagCount> getBlogTagCountForIndex();
}
