package com.lt.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lt.blog.entity.TbBlogInfo;
import com.lt.blog.entity.TbBlogTagRelation;
import com.lt.blog.dao.TbBlogTagRelationMapper;
import com.lt.blog.service.TbBlogTagRelationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 博客跟标签的关系表 服务实现类
 * </p>
 *
 * @author liteng
 * @since 2020-05-21
 */
@Service
public class TbBlogTagRelationServiceImpl extends ServiceImpl<TbBlogTagRelationMapper, TbBlogTagRelation> implements TbBlogTagRelationService {

    @Autowired
    private TbBlogTagRelationMapper blogTagRelationMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void removeAndsaveBatch(List<Integer> blogTagIds, TbBlogInfo blogInfo) {
        Long blogId = blogInfo.getBlogId();
        List<TbBlogTagRelation> list = blogTagIds.stream().map(blogTagId -> new TbBlogTagRelation()
                .setTagId(blogTagId)
                .setBlogId(blogId)).collect(Collectors.toList());
        blogTagRelationMapper.delete(new QueryWrapper<TbBlogTagRelation>()
                .lambda()
                .eq(TbBlogTagRelation::getBlogId, blogInfo.getBlogId()));
        for (TbBlogTagRelation item : list) {
            blogTagRelationMapper.insert(item);
        }

    }
}
