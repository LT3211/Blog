package com.lt.blog.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lt.blog.constants.BlogStatusConstants;
import com.lt.blog.constants.HttpStatusEnum;
import com.lt.blog.constants.SysConfigConstants;
import com.lt.blog.dto.AjaxPutPage;
import com.lt.blog.dto.AjaxResultPage;
import com.lt.blog.dto.Result;
import com.lt.blog.entity.TbBlogCategory;
import com.lt.blog.entity.TbBlogInfo;
import com.lt.blog.service.TbBlogCategoryService;
import com.lt.blog.service.TbBlogInfoService;
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

@Controller
@RequestMapping("/admin")
public class CategryController {
    @Autowired
    private TbBlogCategoryService blogCategoryService;

    @Autowired
    private TbBlogInfoService blogInfoService;

    /**
     * 分类的集合数据[用于下拉框]
     *
     * @param
     * @return com.site.blog.dto.Result<com.site.blog.entity.BlogCategory>
     */
    @ResponseBody
    @GetMapping("/v1/category/list")
    public Result<List<TbBlogCategory>> categoryList() {
        QueryWrapper<TbBlogCategory> queryWrapper = new QueryWrapper<TbBlogCategory>();
        queryWrapper.lambda().eq(TbBlogCategory::getIsDeleted, BlogStatusConstants.ZERO);
        List<TbBlogCategory> list = blogCategoryService.list(queryWrapper);
        if (CollectionUtils.isEmpty(list)) {
            ResultGenerator.getResultByHttp(HttpStatusEnum.INTERNAL_SERVER_ERROR);
        }
        return ResultGenerator.getResultByHttp(HttpStatusEnum.OK, list);
    }


    @GetMapping("/v1/category")
    public String gotoBlogCategory() {
        return "adminLayui/category-list";
    }

    /**
     * 分类的分页
     *
     * @param ajaxPutPage
     * @param condition
     * @return com.site.blog.dto.AjaxResultPage<com.site.blog.entity.BlogCategory>
     */
    @ResponseBody
    @GetMapping("/v1/category/paging")
    public AjaxResultPage<TbBlogCategory> getCategoryList(AjaxPutPage<TbBlogCategory> ajaxPutPage, TbBlogCategory condition) {
        QueryWrapper<TbBlogCategory> queryWrapper = new QueryWrapper<>(condition);
        queryWrapper.lambda()
                .orderByAsc(TbBlogCategory::getCategoryRank)
                .ne(TbBlogCategory::getCategoryId, 1);
        Page<TbBlogCategory> page = ajaxPutPage.putPageToPage();
        blogCategoryService.page(page, queryWrapper);
        AjaxResultPage<TbBlogCategory> result = new AjaxResultPage<>();
        result.setData(page.getRecords());
        result.setCount(page.getTotal());
        return result;
    }


    /**
     * 修改分类信息
     *
     * @param blogCategory
     * @return com.site.blog.dto.Result
     * @date 2019/8/30 14:55
     */
    @ResponseBody
    @PostMapping("/v1/category/update")
    public Result<String> updateCategory(TbBlogCategory blogCategory) {
        TbBlogCategory sqlCategory = blogCategoryService.getById(blogCategory.getCategoryId());
        boolean flag = sqlCategory.getCategoryName().equals(blogCategory.getCategoryName());
        if (flag) {
            blogCategoryService.updateById(blogCategory);
        } else {
            TbBlogInfo blogInfo = new TbBlogInfo()
                    .setBlogCategoryId(blogCategory.getCategoryId())
                    .setBlogCategoryName(blogCategory.getCategoryName());
            UpdateWrapper<TbBlogInfo> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda().eq(TbBlogInfo::getBlogCategoryId, blogCategory.getCategoryId());
            blogInfoService.update(blogInfo, updateWrapper);
            blogCategoryService.updateById(blogCategory);
        }
        return ResultGenerator.getResultByHttp(HttpStatusEnum.OK);
    }

    /**
     * 修改分类状态
     *
     * @param blogCategory
     * @return com.site.blog.dto.Result
     */
    @ResponseBody
    @PostMapping("/v1/category/isDel")
    public Result<String> updateCategoryStatus(TbBlogCategory blogCategory) {
        boolean flag = blogCategoryService.updateById(blogCategory);
        if (flag) {
            return ResultGenerator.getResultByHttp(HttpStatusEnum.OK);
        }
        return ResultGenerator.getResultByHttp(HttpStatusEnum.INTERNAL_SERVER_ERROR);
    }

    /**
     * 清除分类信息
     *
     * @param blogCategory
     * @return com.site.blog.dto.Result
     * @date 2019/9/1 15:48
     */
    @ResponseBody
    @PostMapping("/v1/category/clear")
    public Result<String> clearCategory(TbBlogCategory blogCategory) {
        UpdateWrapper<TbBlogInfo> updateWrapper = new UpdateWrapper();
        updateWrapper.lambda()
                .eq(TbBlogInfo::getBlogCategoryId, blogCategory.getCategoryId())
                .set(TbBlogInfo::getBlogCategoryId, SysConfigConstants.DEFAULT_CATEGORY.getConfigField())
                .set(TbBlogInfo::getBlogCategoryName, SysConfigConstants.DEFAULT_CATEGORY.getConfigName());
        boolean flag = blogInfoService.update(updateWrapper);
        flag = blogCategoryService.removeById(blogCategory.getCategoryId());
        if (flag) {
            return ResultGenerator.getResultByHttp(HttpStatusEnum.OK);
        }
        return ResultGenerator.getResultByHttp(HttpStatusEnum.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/v1/category/add")
    public String addBlogConfig() {
        return "adminLayui/category-add";
    }

    /**
     * 新增分类信息
     *
     * @param blogCategory
     * @return com.site.blog.dto.Result
     */
    @ResponseBody
    @PostMapping("/v1/category/add")
    public Result<String> addCategory(TbBlogCategory blogCategory) {
        blogCategory.setCreateTime(DateUtils.getLocalCurrentDate());
        boolean flag = blogCategoryService.save(blogCategory);
        if (flag) {
            return ResultGenerator.getResultByHttp(HttpStatusEnum.OK);
        }
        return ResultGenerator.getResultByHttp(HttpStatusEnum.INTERNAL_SERVER_ERROR);
    }


}
