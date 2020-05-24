package com.lt.blog.controller.blog;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lt.blog.constants.BlogStatusConstants;
import com.lt.blog.constants.HttpStatusEnum;
import com.lt.blog.constants.LinkConstants;
import com.lt.blog.controller.vo.BlogDetailVO;
import com.lt.blog.dto.AjaxPutPage;
import com.lt.blog.dto.AjaxResultPage;
import com.lt.blog.dto.Result;
import com.lt.blog.entity.*;
import com.lt.blog.service.*;
import com.lt.blog.util.PageResult;
import com.lt.blog.util.ResultGenerator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 博客Controller
 */
@Controller
public class MyBlogController {

    public static String theme = "amaze";


    @Autowired
    private TbBlogInfoService blogInfoService;

    @Autowired
    private TbBlogTagService blogTagService;

    @Autowired
    private TbBlogConfigService blogConfigService;

    @Autowired
    private TbBlogTagRelationService blogTagRelationService;

    @Autowired
    private TbBlogCommentService blogCommentService;

    @Autowired
    private TbBlogLinkService blogLinkService;

    /**
     * 博客首页
     *
     * @param request
     * @return java.lang.String
     * @date 2019/9/6 7:03
     */
    @GetMapping({"/", "/index", "index.html"})
    public String index(HttpServletRequest request) {
        return this.page(request, 1);
    }

    /**
     * 博客分页
     *
     * @param request
     * @param pageNum
     * @return java.lang.String
     * @date 2019/9/6 7:03
     */
    @GetMapping({"/page/{pageNum}"})
    public String page(HttpServletRequest request, @PathVariable("pageNum") int pageNum) {
        Page<TbBlogInfo> page = new Page<TbBlogInfo>(pageNum, 8);
        blogInfoService.page(page, new QueryWrapper<TbBlogInfo>()
                .lambda()
                .eq(TbBlogInfo::getBlogStatus, BlogStatusConstants.ONE)
                .eq(TbBlogInfo::getIsDeleted, BlogStatusConstants.ZERO)
                .orderByDesc(TbBlogInfo::getCreateTime));
        PageResult blogPageResult = new PageResult
                (page.getRecords(), page.getTotal(), 8, pageNum);
        request.setAttribute("blogPageResult", blogPageResult);
        request.setAttribute("newBlogs", blogInfoService.getNewBlog());
        request.setAttribute("hotBlogs", blogInfoService.getHotBlog());
        request.setAttribute("hotTags", blogTagService.getBlogTagCountForIndex());
        request.setAttribute("pageName", "首页");
        request.setAttribute("configurations", blogConfigService.getAllConfigs());
        return "blog/" + theme + "/index";
    }


    /**
     * 搜索
     *
     * @param request
     * @param keyword
     * @return java.lang.String
     */
    @GetMapping({"/search/{keyword}"})
    public String search(HttpServletRequest request, @PathVariable("keyword") String keyword) {
        return search(request, keyword, 1);
    }

    @GetMapping({"/search/{keyword}/{pageNum}"})
    public String search(HttpServletRequest request, @PathVariable("keyword") String keyword, @PathVariable("pageNum") Integer pageNum) {

        Page<TbBlogInfo> page = new Page<TbBlogInfo>(pageNum, 8);
        blogInfoService.page(page, new QueryWrapper<TbBlogInfo>()
                .lambda().like(TbBlogInfo::getBlogTitle, keyword)
                .eq(TbBlogInfo::getBlogStatus, BlogStatusConstants.ONE)
                .eq(TbBlogInfo::getIsDeleted, BlogStatusConstants.ZERO)
                .orderByDesc(TbBlogInfo::getCreateTime));
        PageResult blogPageResult = new PageResult
                (page.getRecords(), page.getTotal(), 8, pageNum);

        request.setAttribute("blogPageResult", blogPageResult);
        request.setAttribute("pageName", "搜索");
        request.setAttribute("pageUrl", "search");
        request.setAttribute("keyword", keyword);
        request.setAttribute("newBlogs", blogInfoService.getNewBlog());
        request.setAttribute("hotBlogs", blogInfoService.getHotBlog());
        request.setAttribute("hotTags", blogTagService.getBlogTagCountForIndex());
        request.setAttribute("configurations", blogConfigService.getAllConfigs());
        return "blog/" + theme + "/list";
    }

    /**
     * 标签
     *
     * @param request
     * @param tagId
     * @return java.lang.String
     */
    @GetMapping({"/tag/{tagId}"})
    public String tag(HttpServletRequest request, @PathVariable("tagId") String tagId) {
        return tag(request, tagId, 1);
    }

    /**
     * 标签分类
     *
     * @param request
     * @param tagId
     * @param pageNum
     * @return java.lang.String
     */
    @GetMapping({"/tag/{tagId}/{pageNum}"})
    public String tag(HttpServletRequest request, @PathVariable("tagId") String tagId, @PathVariable("pageNum") Integer pageNum) {
        List<TbBlogTagRelation> list = blogTagRelationService.list(new QueryWrapper<TbBlogTagRelation>()
                .lambda().eq(TbBlogTagRelation::getTagId, tagId));
        PageResult blogPageResult = null;
        if (!list.isEmpty()) {
            Page<TbBlogInfo> page = new Page<TbBlogInfo>(pageNum, 8);
            blogInfoService.page(page, new QueryWrapper<TbBlogInfo>()
                    .lambda()
                    .eq(TbBlogInfo::getBlogStatus, BlogStatusConstants.ONE)
                    .eq(TbBlogInfo::getIsDeleted, BlogStatusConstants.ZERO)
                    .in(TbBlogInfo::getBlogId, list.stream().map(TbBlogTagRelation::getBlogId).toArray())
                    .orderByDesc(TbBlogInfo::getCreateTime));
            blogPageResult = new PageResult
                    (page.getRecords(), page.getTotal(), 8, pageNum);
        }
        request.setAttribute("blogPageResult", blogPageResult);
        request.setAttribute("pageName", "标签");
        request.setAttribute("pageUrl", "tag");
        request.setAttribute("keyword", tagId);
        request.setAttribute("newBlogs", blogInfoService.getNewBlog());
        request.setAttribute("hotBlogs", blogInfoService.getHotBlog());
        request.setAttribute("hotTags", blogTagService.getBlogTagCountForIndex());
        request.setAttribute("configurations", blogConfigService.getAllConfigs());
        return "blog/" + theme + "/list";
    }

    @GetMapping({"/category/{categoryName}"})
    public String category(HttpServletRequest request, @PathVariable("categoryName") String categoryName) {
        return category(request, categoryName, 1);
    }

    /**
     * 分类列表
     *
     * @param request
     * @param categoryName
     * @param pageNum
     * @return java.lang.String
     */
    @GetMapping({"/category/{categoryName}/{pageNum}"})
    public String category(HttpServletRequest request, @PathVariable("categoryName") String categoryName, @PathVariable("pageNum") Integer pageNum) {
        Page<TbBlogInfo> page = new Page<TbBlogInfo>(pageNum, 8);
        blogInfoService.page(page, new QueryWrapper<TbBlogInfo>()
                .lambda()
                .eq(TbBlogInfo::getBlogStatus, BlogStatusConstants.ONE)
                .eq(TbBlogInfo::getIsDeleted, BlogStatusConstants.ZERO)
                .eq(TbBlogInfo::getBlogCategoryName, categoryName)
                .orderByDesc(TbBlogInfo::getCreateTime));
        PageResult blogPageResult = new PageResult
                (page.getRecords(), page.getTotal(), 8, pageNum);

        request.setAttribute("blogPageResult", blogPageResult);
        request.setAttribute("pageName", "分类");
        request.setAttribute("pageUrl", "category");
        request.setAttribute("keyword", categoryName);
        request.setAttribute("newBlogs", blogInfoService.getNewBlog());
        request.setAttribute("hotBlogs", blogInfoService.getHotBlog());
        request.setAttribute("hotTags", blogTagService.getBlogTagCountForIndex());
        request.setAttribute("configurations", blogConfigService.getAllConfigs());
        return "blog/" + theme + "/list";
    }

    /**
     * 文章详情
     *
     * @param request
     * @param blogId
     * @return java.lang.String
     */
    @GetMapping({"/blog/{blogId}", "/article/{blogId}"})
    public String detail(HttpServletRequest request, @PathVariable("blogId") Long blogId) {
        // 获得文章info
        TbBlogInfo blogInfo = blogInfoService.getById(blogId);
        List<TbBlogTagRelation> blogTagRelations = blogTagRelationService.list(new QueryWrapper<TbBlogTagRelation>()
                .lambda()
                .eq(TbBlogTagRelation::getBlogId, blogId));
        blogInfo.setBlogViews(blogInfo.getBlogViews() + 1);
        blogInfoService.updateById(blogInfo);

        // 获得关联的标签列表
        List<Integer> tagIds = new ArrayList<>();
        List<TbBlogTag> tagList = new ArrayList<>();
        if (!blogTagRelations.isEmpty()) {
            tagIds = blogTagRelations.stream()
                    .map(TbBlogTagRelation::getTagId).collect(Collectors.toList());
            tagList = blogTagService.list(new QueryWrapper<TbBlogTag>().lambda().in(TbBlogTag::getTagId, tagIds));
        }

        // 关联评论的Count
        Integer blogCommentCount = blogCommentService.count(new QueryWrapper<TbBlogComment>()
                .lambda()
                .eq(TbBlogComment::getCommentStatus, BlogStatusConstants.ONE)
                .eq(TbBlogComment::getIsDeleted, BlogStatusConstants.ZERO)
                .eq(TbBlogComment::getBlogId, blogId));

        BlogDetailVO blogDetailVO = new BlogDetailVO();
        BeanUtils.copyProperties(blogInfo, blogDetailVO);
        blogDetailVO.setCommentCount(blogCommentCount);
        request.setAttribute("blogDetailVO", blogDetailVO);
        request.setAttribute("tagList", tagList);
        request.setAttribute("pageName", "详情");
        request.setAttribute("configurations", blogConfigService.getAllConfigs());
        return "blog/" + theme + "/detail";
    }


    /**
     * 评论列表
     *
     * @param ajaxPutPage
     * @param blogId
     * @return com.site.blog.dto.AjaxResultPage<com.site.blog.entity.BlogComment>
     */
    @GetMapping("/blog/listComment")
    @ResponseBody
    public AjaxResultPage<TbBlogComment> listComment(AjaxPutPage<TbBlogComment> ajaxPutPage, Integer blogId) {
        Page<TbBlogComment> page = ajaxPutPage.putPageToPage();
        blogCommentService.page(page, new QueryWrapper<TbBlogComment>()
                .lambda()
                .eq(TbBlogComment::getBlogId, blogId)
                .eq(TbBlogComment::getCommentStatus, BlogStatusConstants.ONE)
                .eq(TbBlogComment::getIsDeleted, BlogStatusConstants.ZERO)
                .orderByDesc(TbBlogComment::getCommentCreateTime));
        AjaxResultPage<TbBlogComment> ajaxResultPage = new AjaxResultPage<>();
        ajaxResultPage.setCount(page.getTotal());
        ajaxResultPage.setData(page.getRecords());
        return ajaxResultPage;
    }


    /**
     * 友链界面
     *
     * @param request
     * @return java.lang.String
     */
    @GetMapping({"/link"})
    public String link(HttpServletRequest request) {
        request.setAttribute("pageName", "友情链接");
        List<TbBlogLink> favoriteLinks = blogLinkService.list(new QueryWrapper<TbBlogLink>()
                .lambda().eq(TbBlogLink::getLinkType, LinkConstants.LINK_TYPE_FRIENDSHIP.getLinkTypeId())
        );
        List<TbBlogLink> recommendLinks = blogLinkService.list(new QueryWrapper<TbBlogLink>()
                .lambda().eq(TbBlogLink::getLinkType, LinkConstants.LINK_TYPE_RECOMMEND.getLinkTypeId())
        );
        List<TbBlogLink> personalLinks = blogLinkService.list(new QueryWrapper<TbBlogLink>()
                .lambda().eq(TbBlogLink::getLinkType, LinkConstants.LINK_TYPE_PRIVATE.getLinkTypeId())
        );
        //判断友链类别并封装数据 0-友链 1-推荐 2-个人网站
        request.setAttribute("favoriteLinks", favoriteLinks);
        request.setAttribute("recommendLinks", recommendLinks);
        request.setAttribute("personalLinks", personalLinks);
        request.setAttribute("configurations", blogConfigService.getAllConfigs());
        return "blog/" + theme + "/link";
    }


    /**
     * 提交评论
     *
     * @return com.site.blog.dto.Result
     */
    @PostMapping(value = "/blog/comment")
    @ResponseBody
    public Result<String> comment(HttpServletRequest request,
                                  @Validated TbBlogComment blogComment) {
        String ref = request.getHeader("Referer");
        if (StringUtils.isEmpty(ref)) {
            return ResultGenerator.getResultByHttp(HttpStatusEnum.INTERNAL_SERVER_ERROR, "非法请求");
        }
        boolean flag = blogCommentService.save(blogComment);
        if (flag) {
            return ResultGenerator.getResultByHttp(HttpStatusEnum.OK);
        }
        return ResultGenerator.getResultByHttp(HttpStatusEnum.INTERNAL_SERVER_ERROR);
    }

}
