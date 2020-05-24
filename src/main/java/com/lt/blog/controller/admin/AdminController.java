package com.lt.blog.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lt.blog.constants.*;
import com.lt.blog.dto.Result;
import com.lt.blog.entity.*;
import com.lt.blog.service.*;
import com.lt.blog.util.MD5Utils;
import com.lt.blog.util.ResultGenerator;
import com.lt.blog.util.UploadFileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;

/**
 * 管理员Controller
 */
@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private TbAdminUserService adminUserService;
    @Autowired
    private TbBlogInfoService blogInfoService;
    @Autowired
    private TbBlogTagService blogTagService;
    @Autowired
    private TbBlogCategoryService blogCategoryService;
    @Autowired
    private TbBlogCommentService blogCommentService;
    @Autowired
    private TbBlogConfigService blogConfigService;
    @Autowired
    private TbBlogLinkService blogLinkService;


    /**
     * 路由:跳转到登陆页面
     *
     * @return
     */
    @GetMapping(value = "/v1/login")
    public String login() {
        return "adminLayui/login";
    }

    @GetMapping("/v1/welcome")
    public String welcome() {
        return "adminLayui/welcome";
    }

    /**
     * 注销登陆
     *
     * @param session
     * @return
     */
    @GetMapping("v1/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "adminLayui/login";
    }

    /**
     * 返回个人信息页面
     *
     * @return
     */
    @GetMapping("/v1/userInfo")
    public String gotoUserInfo() {
        return "adminLayui/userInfo-edit";
    }

    @ResponseBody
    @PostMapping(value = "/v1/login")
    public Result<String> login(String username, String password,
                                HttpSession session) {
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            return ResultGenerator.getResultByHttp(HttpStatusEnum.BAD_REQUEST);
        }
        QueryWrapper<TbAdminUser> queryWrapper = new QueryWrapper<TbAdminUser>(
                new TbAdminUser().setLoginUserName(username)
                        .setLoginPassword(MD5Utils.MD5Encode(password, "UTF-8"))
        );

        TbAdminUser adminUser = adminUserService.getOne(queryWrapper);
        if (adminUser != null) {
            session.setAttribute(SessionConstants.LOGIN_USER, adminUser.getNickName());
            session.setAttribute(SessionConstants.LOGIN_USER_ID, adminUser.getAdminUserId());
            session.setAttribute(SessionConstants.LOGIN_USER_NAME, adminUser.getLoginUserName());
            session.setAttribute(SessionConstants.AUTHOR_IMG, blogConfigService.getById(
                    SysConfigConstants.SYS_AUTHOR_IMG.getConfigField()));
            return ResultGenerator.getResultByHttp(HttpStatusEnum.OK, "/admin/v1/index");
        } else {
            return ResultGenerator.getResultByHttp(HttpStatusEnum.UNAUTHORIZED);
        }
    }

    /**
     * 验证密码是否正确
     *
     * @param oldPwd
     * @param session
     * @return
     */
    @ResponseBody
    @GetMapping("/v1/password")
    public Result<String> validatePassword(String oldPwd, HttpSession session) {
        Integer userId = (Integer) session.getAttribute(SessionConstants.LOGIN_USER_ID);

        boolean flag = adminUserService.validatePassword(userId, oldPwd);
        if (flag) {
            return ResultGenerator.getResultByHttp(HttpStatusEnum.OK);
        }
        return ResultGenerator.getResultByHttp(HttpStatusEnum.BAD_REQUEST);
    }

    /**
     * 返回首页相关数据
     *
     * @param session
     * @return
     */
    @GetMapping("/v1/index")
    public String index(HttpSession session) {
        session.setAttribute("categoryCount", blogCategoryService.count(
                new QueryWrapper<TbBlogCategory>().lambda().eq(TbBlogCategory::getIsDeleted,
                        BlogStatusConstants.ZERO)
        ));
        session.setAttribute("blogCount", blogInfoService.count(
                new QueryWrapper<TbBlogInfo>().lambda().eq(TbBlogInfo::getIsDeleted,
                        BlogStatusConstants.ZERO)
        ));
        session.setAttribute("linkCount", blogLinkService.count(
                new QueryWrapper<TbBlogLink>().lambda().eq(TbBlogLink::getIsDeleted,
                        BlogStatusConstants.ZERO)
        ));
        session.setAttribute("tagCount", blogTagService.count(
                new QueryWrapper<TbBlogTag>().lambda().eq(TbBlogTag::getIsDeleted,
                        BlogStatusConstants.ZERO)
        ));
        session.setAttribute("commentCount", blogCommentService.count(
                new QueryWrapper<TbBlogComment>().lambda().eq(TbBlogComment::getIsDeleted,
                        BlogStatusConstants.ZERO)
        ));
        session.setAttribute("sysList", blogConfigService.list());
        return "adminLayui/index";
    }

    /**
     * 修改用户信息，成功之后清空session并跳转到登陆页
     *
     * @param session
     * @param userName
     * @param newPwd
     * @param nickName
     * @return
     */
    @ResponseBody
    @RequestMapping("/v1/userInfo")
    public Result<String> userInfoUpdate(HttpSession session, String userName, String newPwd,
                                         String nickName) {
        if (StringUtils.isEmpty(newPwd) || StringUtils.isEmpty(nickName)) {
            return ResultGenerator.getResultByHttp(HttpStatusEnum.BAD_REQUEST);
        }
        Integer loginUserId = (int) session.getAttribute(SessionConstants.LOGIN_USER_ID);
        TbAdminUser adminUser = new TbAdminUser()
                .setAdminUserId(loginUserId)
                .setLoginUserName(userName)
                .setNickName(nickName)
                .setLoginPassword(MD5Utils.MD5Encode(newPwd, "UTF-8"));
        if (adminUserService.updateUserInfo(adminUser)) {
            //修改成功够清空session中的数据，前端控制器跳转至登录页
            return ResultGenerator.getResultByHttp(HttpStatusEnum.OK, "/admin/v1/logout");
        } else {
            return ResultGenerator.getResultByHttp(HttpStatusEnum.INTERNAL_SERVER_ERROR);
        }
    }

    @ResponseBody
    @GetMapping("/v1/reload")
    public boolean reload(HttpSession session) {
        Integer userId = (Integer) session.getAttribute(SessionConstants.LOGIN_USER_ID);
        return userId != null && userId != 0;
    }

    /**
     * @Description: 用户头像上传
     */
    @PostMapping("/upload/authorImg")
    @ResponseBody
    public Result<String> upload(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        String suffixName = UploadFileUtils.getSuffixName(file);
        //生成文件名称通用方法
        String newFileName = UploadFileUtils.getNewFileName(suffixName);
        //文件夹
        File fileDirectory = new File(UploadConstants.UPLOAD_AUTHOR_IMG);
        //创建文件
        File destFile = new File(UploadConstants.UPLOAD_AUTHOR_IMG + newFileName);
        try {
            if (!fileDirectory.exists()) {
                if (!fileDirectory.mkdirs()) {
                    throw new IOException("文件夹创建失败,路径为：" + fileDirectory);
                }
            }
            file.transferTo(destFile);
            String sysAuthorImg = UploadConstants.SQL_AUTHOR_IMG + newFileName;
            TbBlogConfig blogConfig = new TbBlogConfig()
                    .setConfigField(SysConfigConstants.SYS_AUTHOR_IMG.getConfigField())
                    .setConfigValue(sysAuthorImg);
            blogConfigService.updateById(blogConfig);
            return ResultGenerator.getResultByHttp(HttpStatusEnum.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return ResultGenerator.getResultByHttp(HttpStatusEnum.INTERNAL_SERVER_ERROR);
        }
    }
}
