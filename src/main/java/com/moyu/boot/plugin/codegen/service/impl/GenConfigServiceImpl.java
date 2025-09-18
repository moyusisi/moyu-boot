package com.moyu.boot.plugin.codegen.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Strings;
import com.moyu.boot.common.core.enums.ResultCodeEnum;
import com.moyu.boot.common.core.exception.BusinessException;
import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.plugin.codegen.mapper.GenConfigMapper;
import com.moyu.boot.plugin.codegen.model.entity.GenConfig;
import com.moyu.boot.plugin.codegen.model.param.GenTableParam;
import com.moyu.boot.plugin.codegen.service.GenConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * 针对表【gen_config(代码生成实体配置表)】的数据库操作Service实现
 *
 * @author shisong
 * @since 2025-09-14
 */
@Slf4j
@Service
public class GenConfigServiceImpl extends ServiceImpl<GenConfigMapper, GenConfig> implements GenConfigService {

    @Override
    public List<GenConfig> list(GenTableParam param) {
        LambdaQueryWrapper<GenConfig> queryWrapper = Wrappers.lambdaQuery(GenConfig.class)
                // 关键词搜索(表名、业务名)
                .like(StrUtil.isNotBlank(param.getSearchKey()), GenConfig::getTableName, param.getSearchKey())
                .or()
                .like(StrUtil.isNotBlank(param.getSearchKey()), GenConfig::getBusinessName, param.getSearchKey())
                .orderByAsc(GenConfig::getUpdateTime);
        // 查询
        List<GenConfig> genConfigList = this.list(queryWrapper);
        return genConfigList;
    }

    @Override
    public PageData<GenConfig> pageList(GenTableParam param) {
        // 查询条件
        LambdaQueryWrapper<GenConfig> queryWrapper = Wrappers.lambdaQuery(GenConfig.class)
                // 关键词搜索(表名、业务名)
                .like(StrUtil.isNotBlank(param.getSearchKey()), GenConfig::getTableName, param.getSearchKey())
                .or()
                .like(StrUtil.isNotBlank(param.getSearchKey()), GenConfig::getBusinessName, param.getSearchKey())
                .orderByAsc(GenConfig::getUpdateTime);
        // 分页查询
        Page<GenConfig> page = new Page<>(param.getPageNum(), param.getPageSize());
        Page<GenConfig> rolePage = this.page(page, queryWrapper);
        return new PageData<>(rolePage.getTotal(), rolePage.getRecords());
    }

    @Override
    public GenConfig detail(GenTableParam param) {
        LambdaQueryWrapper<GenConfig> queryWrapper = Wrappers.lambdaQuery(GenConfig.class)
                .eq(ObjectUtil.isNotEmpty(param.getId()), GenConfig::getId, param.getId())
                .eq(ObjectUtil.isNotEmpty(param.getTableName()), GenConfig::getTableName, param.getTableName());
        // id、code均为唯一标识
        GenConfig genConfig = this.getOne(queryWrapper);
        if (genConfig == null) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER, "未查到指定数据");
        }
        return genConfig;
    }

    @Override
    public void add(GenTableParam param) {
        // 若指定了唯一键，则必须全局唯一
        if (!Strings.isNullOrEmpty(param.getTableName())) {
            // 查询指定code
            GenConfig old = this.getOne(new LambdaQueryWrapper<GenConfig>().eq(GenConfig::getTableName, param.getTableName()));
            if (old != null) {
                throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER, "唯一编码重复，请更换或留空自动生成");
            }
        }
        // 属性复制
        GenConfig genConfig = BeanUtil.copyProperties(param, GenConfig.class);
        genConfig.setId(null);
        this.save(genConfig);
    }

    @Override
    public void deleteByIds(GenTableParam param) {
        // 待删除的id集合
        Set<Long> idSet = param.getIds();
        // 通过查询条件 物理删除
        LambdaQueryWrapper<GenConfig> queryWrapper = Wrappers.lambdaQuery(GenConfig.class)
                .in(ObjectUtil.isNotEmpty(idSet), GenConfig::getId, idSet);
        this.remove(queryWrapper);
        // 或直接通过ids 物理删除
        // this.removeByIds(idSet);
    }

    @Override
    public void edit(GenTableParam param) {
        GenConfig oldGenConfig = this.detail(param);
        // 属性复制
        GenConfig updateGenConfig = BeanUtil.copyProperties(param, GenConfig.class);
        updateGenConfig.setId(oldGenConfig.getId());
        this.updateById(updateGenConfig);
    }
}
