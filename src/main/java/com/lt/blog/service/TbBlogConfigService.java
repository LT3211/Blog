package com.lt.blog.service;

import com.lt.blog.entity.TbBlogConfig;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author liteng
 * @since 2020-05-21
 */
public interface TbBlogConfigService extends IService<TbBlogConfig> {

    Map<String, String> getAllConfigs();

}
