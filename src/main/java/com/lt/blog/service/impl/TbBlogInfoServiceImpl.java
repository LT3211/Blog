package com.lt.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.lt.blog.constants.BlogStatusConstants;
import com.lt.blog.controller.vo.SimpleBlogListVO;
import com.lt.blog.dao.TbBlogCommentMapper;
import com.lt.blog.dao.TbBlogTagRelationMapper;
import com.lt.blog.entity.TbBlogComment;
import com.lt.blog.entity.TbBlogInfo;
import com.lt.blog.dao.TbBlogInfoMapper;
import com.lt.blog.entity.TbBlogTagRelation;
import com.lt.blog.service.TbBlogInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 博客信息表 服务实现类
 * </p>
 *
 * @author liteng
 * @since 2020-05-21
 */
@Service
public class TbBlogInfoServiceImpl extends ServiceImpl<TbBlogInfoMapper, TbBlogInfo> implements TbBlogInfoService {

    @Autowired
    private TbBlogInfoMapper blogInfoMapper;

    @Autowired
    private TbBlogTagRelationMapper blogTagRelationMapper;

    @Autowired
    private TbBlogCommentMapper blogCommentMapper;

    @Override
    public List<SimpleBlogListVO> getNewBlog() {
        List<SimpleBlogListVO> simpleBlogListVOS = new ArrayList<>();
        Page<TbBlogInfo> page = new Page<>(1, 5);
        blogInfoMapper.selectPage(page, new QueryWrapper<TbBlogInfo>()
                .lambda()
                .eq(TbBlogInfo::getBlogStatus, BlogStatusConstants.ONE)
                .eq(TbBlogInfo::getIsDeleted, BlogStatusConstants.ZERO)
                .orderByDesc(TbBlogInfo::getCreateTime));
        for (TbBlogInfo blogInfo : page.getRecords()) {
            SimpleBlogListVO simpleBlogListVO = new SimpleBlogListVO();
            BeanUtils.copyProperties(blogInfo, simpleBlogListVO);
            simpleBlogListVOS.add(simpleBlogListVO);
        }
        return simpleBlogListVOS;
    }

    @Override
    public List<SimpleBlogListVO> getHotBlog() {
        List<SimpleBlogListVO> simpleBlogListVOS = new ArrayList<>();
        Page<TbBlogInfo> page = new Page<>(1, 5);
        blogInfoMapper.selectPage(page, new QueryWrapper<TbBlogInfo>()
                .lambda()
                .eq(TbBlogInfo::getBlogStatus, BlogStatusConstants.ONE)
                .eq(TbBlogInfo::getIsDeleted, BlogStatusConstants.ZERO)
                .orderByDesc(TbBlogInfo::getBlogViews));
        for (TbBlogInfo blogInfo : page.getRecords()) {
            SimpleBlogListVO simpleBlogListVO = new SimpleBlogListVO();
            BeanUtils.copyProperties(blogInfo, simpleBlogListVO);
            simpleBlogListVOS.add(simpleBlogListVO);
        }
        return simpleBlogListVOS;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean clearBlogInfo(Long blogId) {
        if (SqlHelper.retBool(blogInfoMapper.deleteById(blogId))) {
            QueryWrapper<TbBlogTagRelation> tagRelationWrapper = new QueryWrapper<>();
            tagRelationWrapper.lambda().eq(TbBlogTagRelation::getBlogId, blogId);
            blogTagRelationMapper.delete(tagRelationWrapper);
            QueryWrapper<TbBlogComment> commentWrapper = new QueryWrapper<>();
            commentWrapper.lambda().eq(TbBlogComment::getBlogId, blogId);
            blogCommentMapper.delete(commentWrapper);
            return true;
        }
        return false;
    }
}
