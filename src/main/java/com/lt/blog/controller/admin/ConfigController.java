package com.lt.blog.controller.admin;

import com.lt.blog.constants.HttpStatusEnum;
import com.lt.blog.dto.AjaxResultPage;
import com.lt.blog.dto.Result;
import com.lt.blog.entity.TbBlogConfig;
import com.lt.blog.service.TbBlogConfigService;
import com.lt.blog.util.DateUtils;
import com.lt.blog.util.ResultGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *blog 配置controller
 */
@Controller
@RequestMapping("/admin")
public class ConfigController {

    @Autowired
    private TbBlogConfigService blogConfigService;

    /**
     * 跳转系统配置界面
     * @return java.lang.String
     */
    @GetMapping("/v1/blogConfig")
    public String gotoBlogConfig(){
        return "adminLayui/sys-edit";
    }

    /**
     * 返回系统配置信息
     * @param
     * @return com.site.blog.dto.AjaxResultPage<com.site.blog.entity.BlogConfig>
     */
    @ResponseBody
    @GetMapping("/v1/blogConfig/list")
    public AjaxResultPage<TbBlogConfig> getBlogConfig(){
        AjaxResultPage<TbBlogConfig> ajaxResultPage = new AjaxResultPage<>();
        List<TbBlogConfig> list = blogConfigService.list();
        if (CollectionUtils.isEmpty(list)){
            ajaxResultPage.setCode(500);
            return ajaxResultPage;
        }
        ajaxResultPage.setData(blogConfigService.list());
        return ajaxResultPage;
    }


    /**
     * 修改系统信息
     * @param blogConfig
     * @return com.site.blog.dto.Result
     */
    @ResponseBody
    @PostMapping("/v1/blogConfig/edit")
    public Result<String> updateBlogConfig(TbBlogConfig blogConfig){
        blogConfig.setUpdateTime(DateUtils.getLocalCurrentDate());
        boolean flag = blogConfigService.updateById(blogConfig);
        if (flag){
            return ResultGenerator.getResultByHttp(HttpStatusEnum.OK);
        }else{
            return ResultGenerator.getResultByHttp(HttpStatusEnum.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/v1/blogConfig/add")
    public String addBlogConfig(){
        return "adminLayui/sys-add";
    }


    /**
     * 新增系统信息项
     * @param blogConfig
     * @return com.site.blog.dto.Result
     */
    @ResponseBody
    @PostMapping("/v1/blogConfig/add")
    public Result<String> addBlogConfig(TbBlogConfig blogConfig){
        blogConfig.setCreateTime(DateUtils.getLocalCurrentDate());
        blogConfig.setUpdateTime(DateUtils.getLocalCurrentDate());
        boolean flag = blogConfigService.save(blogConfig);
        if (flag){
            return ResultGenerator.getResultByHttp(HttpStatusEnum.OK);
        }else{
            return ResultGenerator.getResultByHttp(HttpStatusEnum.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * 删除配置信息项
     * @param configField
     * @return com.site.blog.dto.Result
     */
    @ResponseBody
    @PostMapping("/v1/blogConfig/del")
    public Result<String> delBlogConfig(@RequestParam String configField){
        boolean flag = blogConfigService.removeById(configField);
        if (flag){
            return ResultGenerator.getResultByHttp(HttpStatusEnum.OK);
        }else{
            return ResultGenerator.getResultByHttp(HttpStatusEnum.INTERNAL_SERVER_ERROR);
        }
    }

}
