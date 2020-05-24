package com.lt.blog.controller.admin;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lt.blog.constants.HttpStatusEnum;
import com.lt.blog.constants.LinkConstants;
import com.lt.blog.dto.AjaxPutPage;
import com.lt.blog.dto.AjaxResultPage;
import com.lt.blog.dto.Result;
import com.lt.blog.entity.TbBlogLink;
import com.lt.blog.service.TbBlogLinkService;
import com.lt.blog.util.DateUtils;
import com.lt.blog.util.ResultGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 友链Controller
 */
@Controller
@RequestMapping("/admin")
public class LinkController {

    @Autowired
    private TbBlogLinkService blogLinkService;

    @GetMapping("/v1/linkType")
    public String gotoLink(){
        return "adminLayui/link-list";
    }

    @ResponseBody
    @GetMapping("/v1/linkType/list")
    public Result<List<TbBlogLink>> linkTypeList(){
        List<TbBlogLink> links = new ArrayList<>();
        links.add(new TbBlogLink().setLinkType(LinkConstants.LINK_TYPE_FRIENDSHIP.getLinkTypeId())
                .setLinkName(LinkConstants.LINK_TYPE_FRIENDSHIP.getLinkTypeName()));
        links.add(new TbBlogLink().setLinkType(LinkConstants.LINK_TYPE_RECOMMEND.getLinkTypeId())
                .setLinkName(LinkConstants.LINK_TYPE_RECOMMEND.getLinkTypeName()));
        links.add(new TbBlogLink().setLinkType(LinkConstants.LINK_TYPE_PRIVATE.getLinkTypeId())
                .setLinkName(LinkConstants.LINK_TYPE_PRIVATE.getLinkTypeName()));
        return ResultGenerator.getResultByHttp(HttpStatusEnum.OK,links);
    }

    @ResponseBody
    @GetMapping("/v1/link/paging")
    public AjaxResultPage<TbBlogLink> getLinkList(AjaxPutPage<TbBlogLink> ajaxPutPage, TbBlogLink condition){
        QueryWrapper<TbBlogLink> queryWrapper = new QueryWrapper<>(condition);
        queryWrapper.lambda()
                .orderByAsc(TbBlogLink::getLinkRank);
        Page<TbBlogLink> page = ajaxPutPage.putPageToPage();
        blogLinkService.page(page,queryWrapper);
        AjaxResultPage<TbBlogLink> result = new AjaxResultPage<>();
        result.setData(page.getRecords());
        result.setCount(page.getTotal());
        return result;
    }

    @ResponseBody
    @PostMapping("/v1/link/isDel")
    public Result<String> updateLinkStatus(TbBlogLink blogLink){
        boolean flag = blogLinkService.updateById(blogLink);
        if (flag){
            return ResultGenerator.getResultByHttp(HttpStatusEnum.OK);
        }
        return ResultGenerator.getResultByHttp(HttpStatusEnum.INTERNAL_SERVER_ERROR);
    }

    @ResponseBody
    @PostMapping("/v1/link/clear")
    public Result<String> clearLink(Integer linkId){
        boolean flag = blogLinkService.removeById(linkId);
        if (flag){
            return ResultGenerator.getResultByHttp(HttpStatusEnum.OK);
        }
        return ResultGenerator.getResultByHttp(HttpStatusEnum.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/v1/link/edit")
    public String editLink(Integer linkId, Model model){
        if (linkId != null){
            TbBlogLink blogLink = blogLinkService.getById(linkId);
            model.addAttribute("blogLink",blogLink);
        }
        return "adminLayui/link-edit";
    }

    @ResponseBody
    @PostMapping("/v1/link/edit")
    public Result<String> updateAndSaveLink(TbBlogLink blogLink){
        blogLink.setCreateTime(DateUtils.getLocalCurrentDate());
        boolean flag;
        if (blogLink.getLinkId() != null){
            flag = blogLinkService.updateById(blogLink);
        }else{
            flag = blogLinkService.save(blogLink);
        }
        if (flag){
            return ResultGenerator.getResultByHttp(HttpStatusEnum.OK);
        }
        return ResultGenerator.getResultByHttp(HttpStatusEnum.INTERNAL_SERVER_ERROR);
    }

}
