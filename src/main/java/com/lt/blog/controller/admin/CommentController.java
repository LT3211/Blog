package com.lt.blog.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lt.blog.constants.HttpStatusEnum;
import com.lt.blog.dto.AjaxPutPage;
import com.lt.blog.dto.AjaxResultPage;
import com.lt.blog.dto.Result;
import com.lt.blog.entity.TbBlogComment;
import com.lt.blog.service.TbBlogCommentService;
import com.lt.blog.util.DateUtils;
import com.lt.blog.util.ResultGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 评论标签
 */
@Controller
@RequestMapping("/admin")
public class CommentController {

    @Resource
    private TbBlogCommentService blogCommentService;

    @GetMapping("/v1/comment")
    public String gotoComment() {
        return "adminLayui/comment-list";
    }


    /**
     * 返回评论列表
     *
     * @param ajaxPutPage
     * @param condition
     * @return com.site.blog.dto.AjaxResultPage<com.site.blog.entity.BlogComment>
     */
    @ResponseBody
    @GetMapping("/v1/comment/paging")
    public AjaxResultPage<TbBlogComment> getCommentList(AjaxPutPage<TbBlogComment> ajaxPutPage, TbBlogComment condition) {
        QueryWrapper<TbBlogComment> queryWrapper = new QueryWrapper<>(condition);
        Page<TbBlogComment> page = ajaxPutPage.putPageToPage();
        blogCommentService.page(page, queryWrapper);
        AjaxResultPage<TbBlogComment> result = new AjaxResultPage<>();
        result.setData(page.getRecords());
        result.setCount(page.getTotal());
        return result;
    }


    /**
     * 修改评论状态
     *
     * @param blogComment
     * @return com.site.blog.dto.Result<java.lang.String>
     */
    @ResponseBody
    @PostMapping(value = {"/v1/comment/isDel", "/v1/comment/commentStatus"})
    public Result<String> updateCommentStatus(TbBlogComment blogComment) {
        boolean flag = blogCommentService.updateById(blogComment);
        if (flag) {
            return ResultGenerator.getResultByHttp(HttpStatusEnum.OK);
        }
        return ResultGenerator.getResultByHttp(HttpStatusEnum.INTERNAL_SERVER_ERROR);
    }


    /**
     * 删除评论
     *
     * @param commentId
     * @return com.site.blog.dto.Result<java.lang.String>
     */
    @ResponseBody
    @PostMapping("/v1/comment/delete")
    public Result<String> deleteComment(@RequestParam Long commentId) {
        boolean flag = blogCommentService.removeById(commentId);
        if (flag) {
            return ResultGenerator.getResultByHttp(HttpStatusEnum.OK);
        }
        return ResultGenerator.getResultByHttp(HttpStatusEnum.INTERNAL_SERVER_ERROR);
    }

    /**
     * 编辑评论
     *
     * @param blogComment
     * @return com.site.blog.dto.Result<java.lang.String>
     * @date 2020/4/24 21:21
     */
    @ResponseBody
    @PostMapping("/v1/comment/edit")
    public Result<String> editComment(TbBlogComment blogComment) {
        blogComment.setReplyCreateTime(DateUtils.getLocalCurrentDate());
        boolean flag = blogCommentService.updateById(blogComment);
        if (flag) {
            return ResultGenerator.getResultByHttp(HttpStatusEnum.OK);
        } else {
            return ResultGenerator.getResultByHttp(HttpStatusEnum.INTERNAL_SERVER_ERROR);
        }
    }

}
