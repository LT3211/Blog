package com.lt.blog.service;

import com.lt.blog.entity.TbAdminUser;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 后台管理员信息表 服务类
 * </p>
 *
 * @author liteng
 * @since 2020-05-21
 */
public interface TbAdminUserService extends IService<TbAdminUser> {

    boolean validatePassword(Integer userId, String oldPwd);

    boolean updateUserInfo(TbAdminUser adminUser);

}
