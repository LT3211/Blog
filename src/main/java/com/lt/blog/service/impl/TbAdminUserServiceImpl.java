package com.lt.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.parser.SqlInfo;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.lt.blog.entity.TbAdminUser;
import com.lt.blog.dao.TbAdminUserMapper;
import com.lt.blog.service.TbAdminUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lt.blog.util.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 后台管理员信息表 服务实现类
 * </p>
 *
 * @author liteng
 * @since 2020-05-21
 */
@Service
public class TbAdminUserServiceImpl extends ServiceImpl<TbAdminUserMapper, TbAdminUser> implements TbAdminUserService {

    @Autowired
    private TbAdminUserMapper adminUserMapper;

    /**
     * 验证密码
     *
     * @param userId
     * @param oldPwd
     * @return
     */
    @Override
    public boolean validatePassword(Integer userId, String oldPwd) {
        QueryWrapper<TbAdminUser> queryWrapper = new QueryWrapper<>(
                new TbAdminUser().setAdminUserId(userId)
                        .setLoginPassword(MD5Utils.MD5Encode(oldPwd, "UTF-8"))
        );
        TbAdminUser tbAdminUser = adminUserMapper.selectOne(queryWrapper);
        return !StringUtils.isEmpty(tbAdminUser);
    }

    @Transactional
    @Override
    public boolean updateUserInfo(TbAdminUser adminUser) {
        return SqlHelper.retBool(adminUserMapper.updateById(adminUser));
    }
}
