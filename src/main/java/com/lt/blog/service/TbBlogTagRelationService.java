package com.lt.blog.service;

import com.lt.blog.entity.TbBlogInfo;
import com.lt.blog.entity.TbBlogTagRelation;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 博客跟标签的关系表 服务类
 * </p>
 *
 * @author liteng
 * @since 2020-05-21
 */
public interface TbBlogTagRelationService extends IService<TbBlogTagRelation> {

    /**
     * 移除本来的标签保存新标签
     * @param blogTagIds
     * @param blogInfo
     */
    void removeAndsaveBatch(List<Integer> blogTagIds, TbBlogInfo blogInfo);

}
