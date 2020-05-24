package com.lt.blog.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lt.blog.constants.BlogStatusConstants;
import com.lt.blog.constants.HttpStatusEnum;
import com.lt.blog.constants.SysConfigConstants;
import com.lt.blog.dto.AjaxPutPage;
import com.lt.blog.dto.AjaxResultPage;
import com.lt.blog.dto.Result;
import com.lt.blog.entity.TbBlogInfo;
import com.lt.blog.entity.TbBlogLink;
import com.lt.blog.entity.TbBlogTag;
import com.lt.blog.entity.TbBlogTagRelation;
import com.lt.blog.service.TbBlogInfoService;
import com.lt.blog.service.TbBlogTagRelationService;
import com.lt.blog.service.TbBlogTagService;
import com.lt.blog.util.DateUtils;
import com.lt.blog.util.ResultGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 标签Controller
 */
@Controller
@RequestMapping("/admin")
public class TagController {
    @Autowired
    private TbBlogTagService blogTagService;

    @Autowired
    private TbBlogInfoService blogInfoService;

    @Autowired
    private TbBlogTagRelationService blogTagRelationService;

    @GetMapping("/v1/tags")
    public String gotoTag() {
        return "adminLayui/tag-list";
    }


    /**
     * @Description: 返回未删除状态下的所有标签
     * @Param: []
     * @return: com.zhulin.blog.dto.Result<com.zhulin.blog.entity.BlogTag>
     */
    @ResponseBody
    @GetMapping("/v1/tags/list")
    public Result<List<TbBlogTag>> tagsList() {
        QueryWrapper<TbBlogTag> queryWrapper = new QueryWrapper<TbBlogTag>();
        queryWrapper.lambda().eq(TbBlogTag::getIsDeleted, BlogStatusConstants.ZERO);
        List<TbBlogTag> list = blogTagService.list(queryWrapper);
        if (CollectionUtils.isEmpty(list)) {
            ResultGenerator.getResultByHttp(HttpStatusEnum.INTERNAL_SERVER_ERROR);
        }
        return ResultGenerator.getResultByHttp(HttpStatusEnum.OK, list);
    }

    /**
     * 标签分页
     *
     * @param ajaxPutPage
     * @param condition
     * @return com.site.blog.dto.AjaxResultPage<com.site.blog.entity.BlogTag>
     */
    @ResponseBody
    @GetMapping("/v1/tags/paging")
    public AjaxResultPage<TbBlogTag> getCategoryList(AjaxPutPage<TbBlogTag> ajaxPutPage, TbBlogTag condition) {
        QueryWrapper<TbBlogTag> queryWrapper = new QueryWrapper<>(condition);
        queryWrapper.lambda()
                .ne(TbBlogTag::getTagId, 1);
        Page<TbBlogTag> page = ajaxPutPage.putPageToPage();
        blogTagService.page(page, queryWrapper);
        AjaxResultPage<TbBlogTag> result = new AjaxResultPage<>();
        result.setData(page.getRecords());
        result.setCount(page.getTotal());
        return result;
    }

    /**
     * 修改标签状态
     *
     * @param blogTag
     * @return com.site.blog.dto.Result
     */
    @ResponseBody
    @PostMapping("/v1/tags/isDel")
    public Result<String> updateCategoryStatus(TbBlogTag blogTag) {
        boolean flag = blogTagService.updateById(blogTag);
        if (flag) {
            return ResultGenerator.getResultByHttp(HttpStatusEnum.OK);
        }
        return ResultGenerator.getResultByHttp(HttpStatusEnum.INTERNAL_SERVER_ERROR);
    }

    /**
     * 添加标签
     *
     * @param blogTag
     * @return com.site.blog.dto.Result
     */
    @ResponseBody
    @PostMapping("/v1/tags/add")
    public Result<String> addTag(TbBlogTag blogTag) {
        blogTag.setCreateTime(DateUtils.getLocalCurrentDate());
        boolean flag = blogTagService.save(blogTag);
        if (flag) {
            return ResultGenerator.getResultByHttp(HttpStatusEnum.OK);
        } else {
            return ResultGenerator.getResultByHttp(HttpStatusEnum.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * 清除标签
     *
     * @param tagId
     * @return com.site.blog.dto.Result
     */
    @ResponseBody
    @PostMapping("/v1/tags/clear")
    public Result<String> clearTag(Integer tagId) throws RuntimeException {
        QueryWrapper<TbBlogTagRelation> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TbBlogTagRelation::getTagId, tagId);
        //查出博客和tag关联数据
        List<TbBlogTagRelation> tagRelationList = blogTagRelationService.list(queryWrapper);
        // 批量更新的BlogInfo信息
        List<TbBlogInfo> infoList = tagRelationList.stream()
                .map(tagRelation -> new TbBlogInfo()
                        .setBlogId(tagRelation.getBlogId())
                        .setBlogTags(SysConfigConstants.DEFAULT_TAG.getConfigName())).collect(Collectors.toList());
        List<Long> blogIds = infoList.stream().map(TbBlogInfo::getBlogId).collect(Collectors.toList());
        // 批量更新的tagRelation信息
        List<TbBlogTagRelation> tagRelations = tagRelationList.stream()
                .map(tagRelation -> new TbBlogTagRelation()
                        .setBlogId(tagRelation.getBlogId())
                        .setTagId(Integer.valueOf(SysConfigConstants.DEFAULT_CATEGORY.getConfigField())))
                .collect(Collectors.toList());
        //如果Tag有关联的博客
        if (infoList != null && !infoList.isEmpty()) {
            //更新博客的Tag
            blogInfoService.updateBatchById(infoList);
            //删除博客和Tag的关联信息
            blogTagRelationService.remove(new QueryWrapper<TbBlogTagRelation>()
                    .lambda()
                    .in(TbBlogTagRelation::getBlogId, blogIds));
            //保存博客和默认Tag的信息
            blogTagRelationService.saveBatch(tagRelations);
        }
        //删除Tag
        blogTagService.removeById(tagId);
        return ResultGenerator.getResultByHttp(HttpStatusEnum.OK);
    }
}
