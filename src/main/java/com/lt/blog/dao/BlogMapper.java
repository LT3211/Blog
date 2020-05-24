package com.lt.blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lt.blog.util.PageQueryUtils;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BlogMapper extends BaseMapper<TbBlogInfoMapper> {
    int deleteByPrimaryKey(Long blogId);

    int insert(TbBlogInfoMapper record);

    int insertSelective(TbBlogInfoMapper record);

    //通过主键查找博客信息，带内容
    TbBlogInfoMapper selectByPrimaryKey(Long blogId);

    int updateByPrimaryKeySelective(TbBlogInfoMapper record);

    int updateByPrimaryKeyWithBLOBs(TbBlogInfoMapper record);

    int updateByPrimaryKey(TbBlogInfoMapper record);

    List<TbBlogInfoMapper> findBlogList(PageQueryUtils pageUtil);

    List<TbBlogInfoMapper> findBlogListByType(@Param("type") int type, @Param("limit") int limit);

    int getTotalBlogs(PageQueryUtils pageUtil);

    int removeBatch(Integer[] ids);

    List<TbBlogInfoMapper> getBlogsPageByTagId(PageQueryUtils pageUtil);

    int getTotalBlogsByTagId(PageQueryUtils pageUtil);

    TbBlogInfoMapper selectBySubUrl(String subUrl);

    int updateBlogCategorys(@Param("categoryName") String categoryName, @Param("categoryId") Integer categoryId, @Param("ids")Integer[] ids);

}