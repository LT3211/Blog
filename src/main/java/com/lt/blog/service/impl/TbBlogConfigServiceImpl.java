package com.lt.blog.service.impl;

import com.lt.blog.entity.TbBlogConfig;
import com.lt.blog.dao.TbBlogConfigMapper;
import com.lt.blog.service.TbBlogConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author liteng
 * @since 2020-05-21
 */
@Service
public class TbBlogConfigServiceImpl extends ServiceImpl<TbBlogConfigMapper, TbBlogConfig> implements TbBlogConfigService {

    @Autowired
    private TbBlogConfigMapper blogConfigMapper;
    
    @Override
    public Map<String, String> getAllConfigs() {
        List<TbBlogConfig> list = blogConfigMapper.selectList(null);
        //java8新特性
        return list.stream().collect(Collectors.toMap(
                TbBlogConfig::getConfigField,TbBlogConfig::getConfigValue
        ));
    }
}
