package com.lt.blog.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lt.blog.constants.BlogStatusConstants;
import com.lt.blog.constants.HttpStatusEnum;
import com.lt.blog.constants.UploadConstants;
import com.lt.blog.dto.AjaxPutPage;
import com.lt.blog.dto.AjaxResultPage;
import com.lt.blog.dto.Result;
import com.lt.blog.entity.TbBlogInfo;
import com.lt.blog.entity.TbBlogTagRelation;
import com.lt.blog.service.TbBlogCommentService;
import com.lt.blog.service.TbBlogInfoService;
import com.lt.blog.service.TbBlogTagRelationService;
import com.lt.blog.util.DateUtils;
import com.lt.blog.util.ResultGenerator;
import com.lt.blog.util.UploadFileUtils;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class BlogController {

    @Autowired
    private TbBlogInfoService blogInfoService;

    @Autowired
    private TbBlogTagRelationService blogTagRelationService;

    @Autowired
    private TbBlogCommentService blogCommentService;

    /**
     * 跳转博客编辑界面
     *
     * @param blogId
     * @param model
     * @return
     */
    @GetMapping("/v1/blog/edit")
    public String gotoBlogEdit(@RequestParam(required = false) Long blogId, Model model) {
        if (blogId != null) {
            TbBlogInfo blogInfo = blogInfoService.getById(blogId);
            List<TbBlogTagRelation> list = blogTagRelationService.list(
                    new QueryWrapper<TbBlogTagRelation>()
                            .lambda()
                            .eq(TbBlogTagRelation::getBlogId, blogId)
            );
            List<Integer> tags = null;
            if (!CollectionUtils.isEmpty(list)) {
                tags = list.stream().map(
                        blogTagRelation -> blogTagRelation.getTagId())
                        .collect(Collectors.toList());
            }
            model.addAttribute("blogTags", tags);
            model.addAttribute("blogInfo", blogInfo);
        }
        return "adminLayui/blog-edit";
    }

    /**
     * 跳转博客列表界面
     *
     * @return java.lang.String
     */
    @GetMapping("/v1/blog")
    public String gotoBlogList() {
        return "adminLayui/blog-list";
    }


    /**
     * 保存文章图片
     *
     * @param request
     * @param file
     * @return
     */
    @ResponseBody
    @PostMapping("/v1/blog/uploadFile")
    public Map<String, Object> uploadFileByEditormd(HttpServletRequest request,
                                                    @RequestParam(name = "editormd-image-file") MultipartFile file) {
        String suffixName = UploadFileUtils.getSuffixName(file);
        //生成文件名称通用方法
        String newFileName = UploadFileUtils.getNewFileName(suffixName);
        File fileDirectory = new File(UploadConstants.FILE_UPLOAD_DIC);
        //创建文件
        File destFile = new File(UploadConstants.FILE_UPLOAD_DIC + newFileName);
        System.out.println(destFile);
        Map<String, Object> result = new HashMap<>();
        try {
            if (!fileDirectory.exists()) {
                if (!fileDirectory.mkdirs()) {
                    throw new IOException("文件夹创建失败,路径为：" + fileDirectory);
                }
            }
            file.transferTo(destFile);
            String fileUrl = UploadConstants.FILE_SQL_DIC + newFileName;
            result.put("success", 1);
            result.put("message", "上传成功");
            result.put("url", fileUrl);
        } catch (IOException e) {
            result.put("success", 0);
        }
        return result;
    }

    /**
     * 保存文章内容
     *
     * @param blogTagIds
     * @param blogInfo
     * @return
     */
    @ResponseBody
    @PostMapping("/v1/blog/edit")
    public Result<String> saveBlog(@RequestParam("blogTagIds[]") List<Integer> blogTagIds, TbBlogInfo blogInfo) {
        if (CollectionUtils.isEmpty(blogTagIds) || ObjectUtils.isEmpty(blogInfo)) {
            return ResultGenerator.getResultByHttp(HttpStatusEnum.BAD_REQUEST);
        }
        blogInfo.setCreateTime(DateUtils.getLocalCurrentDate());
        blogInfo.setUpdateTime(DateUtils.getLocalCurrentDate());

        if (blogInfoService.saveOrUpdate(blogInfo)) {
            blogTagRelationService.removeAndsaveBatch(blogTagIds, blogInfo);
            return ResultGenerator.getResultByHttp(HttpStatusEnum.OK);
        }
        return ResultGenerator.getResultByHttp(HttpStatusEnum.INTERNAL_SERVER_ERROR);
    }

    /**
     * 文章分页列表
     *
     * @param ajaxPutPage 分页参数
     * @param condition   筛选条件
     * @return com.site.blog.dto.AjaxResultPage<com.site.blog.entity.BlogInfo>
     */
    @ResponseBody
    @GetMapping("/v1/blog/list")
    public AjaxResultPage<TbBlogInfo> getContractList(AjaxPutPage<TbBlogInfo> ajaxPutPage, TbBlogInfo condition) {
        QueryWrapper<TbBlogInfo> queryWrapper = new QueryWrapper<>(condition);
        queryWrapper.lambda().orderByDesc(TbBlogInfo::getUpdateTime);
        Page<TbBlogInfo> page = ajaxPutPage.putPageToPage();
        blogInfoService.page(page, queryWrapper);
        AjaxResultPage<TbBlogInfo> result = new AjaxResultPage<>();
        result.setData(page.getRecords());
        result.setCount(page.getTotal());
        return result;
    }

    /**
     * 修改博客的部分状态相关信息
     *
     * @param blogInfo
     * @return com.site.blog.dto.Result
     */
    @ResponseBody
    @PostMapping("/v1/blog/blogStatus")
    public Result<String> updateBlogStatus(TbBlogInfo blogInfo) {
        blogInfo.setUpdateTime(DateUtils.getLocalCurrentDate());
        boolean flag = blogInfoService.updateById(blogInfo);
        if (flag) {
            return ResultGenerator.getResultByHttp(HttpStatusEnum.OK);
        }
        return ResultGenerator.getResultByHttp(HttpStatusEnum.INTERNAL_SERVER_ERROR);
    }

    /**
     * 修改文章的删除状态为已删除
     *
     * @param blogId
     * @return com.site.blog.dto.Result
     */
    @ResponseBody
    @PostMapping("/v1/blog/delete")
    public Result<String> deleteBlog(@RequestParam Long blogId) {
        TbBlogInfo blogInfo = new TbBlogInfo()
                .setBlogId(blogId)
                .setIsDeleted(BlogStatusConstants.ONE)
                .setUpdateTime(DateUtils.getLocalCurrentDate());
        boolean flag = blogInfoService.updateById(blogInfo);
        if (flag) {
            return ResultGenerator.getResultByHttp(HttpStatusEnum.OK);
        }
        return ResultGenerator.getResultByHttp(HttpStatusEnum.INTERNAL_SERVER_ERROR);
    }

    /**
     * 清除文章
     *
     * @param blogId
     * @return com.site.blog.dto.Result
     */
    @ResponseBody
    @PostMapping("/v1/blog/clear")
    public Result<String> clearBlog(@RequestParam Long blogId) {
        if (blogInfoService.clearBlogInfo(blogId)) {
            return ResultGenerator.getResultByHttp(HttpStatusEnum.OK);
        }
        return ResultGenerator.getResultByHttp(HttpStatusEnum.INTERNAL_SERVER_ERROR);
    }

    /**
     * 还原文章
     *
     * @param blogId
     * @return com.site.blog.dto.Result
     */
    @ResponseBody
    @PostMapping("/v1/blog/restore")
    public Result<String> restoreBlog(@RequestParam Long blogId) {
        TbBlogInfo blogInfo = new TbBlogInfo()
                .setBlogId(blogId)
                .setIsDeleted(BlogStatusConstants.ZERO)
                .setUpdateTime(DateUtils.getLocalCurrentDate());
        boolean flag = blogInfoService.updateById(blogInfo);
        if (flag) {
            return ResultGenerator.getResultByHttp(HttpStatusEnum.OK);
        }
        return ResultGenerator.getResultByHttp(HttpStatusEnum.INTERNAL_SERVER_ERROR);
    }

    @ResponseBody
    @GetMapping("v1/blog/select")
    public List<TbBlogInfo> getBlogInfoSelect() {
        return blogInfoService.list();
    }

}
