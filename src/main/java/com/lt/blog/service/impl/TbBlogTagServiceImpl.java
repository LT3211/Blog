package com.lt.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lt.blog.constants.BlogStatusConstants;
import com.lt.blog.dao.TbBlogTagRelationMapper;
import com.lt.blog.entity.BlogTagCount;
import com.lt.blog.entity.TbBlogTag;
import com.lt.blog.dao.TbBlogTagMapper;
import com.lt.blog.entity.TbBlogTagRelation;
import com.lt.blog.service.TbBlogTagService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 标签表 服务实现类
 * </p>
 *
 * @author liteng
 * @since 2020-05-21
 */
@Service
public class TbBlogTagServiceImpl extends ServiceImpl<TbBlogTagMapper, TbBlogTag> implements TbBlogTagService {

    @Autowired
    private TbBlogTagMapper blogTagMapper;

    @Autowired
    private TbBlogTagRelationMapper blogTagRelationMapper;


    @Override
    public List<BlogTagCount> getBlogTagCountForIndex() {
        QueryWrapper<TbBlogTag> queryWrapper = new QueryWrapper<TbBlogTag>();
        queryWrapper.lambda()
                .eq(TbBlogTag::getIsDeleted, BlogStatusConstants.ZERO);
        List<TbBlogTag> list = blogTagMapper.selectList(queryWrapper);
        List<BlogTagCount> blogTagCounts = list.stream()
                .map(blogTag -> new BlogTagCount()
                        .setTagId(blogTag.getTagId())
                        .setTagName(blogTag.getTagName())
                        .setTagCount(
                                blogTagRelationMapper.selectCount(new QueryWrapper<TbBlogTagRelation>()
                                        .lambda().eq(TbBlogTagRelation::getTagId, blogTag.getTagId()))
                        )).collect(Collectors.toList());
        return blogTagCounts;
    }
}
