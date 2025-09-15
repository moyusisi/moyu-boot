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
import com.moyu.boot.plugin.codegen.mapper.GenTableMapper;
import com.moyu.boot.plugin.codegen.model.entity.GenTable;
import com.moyu.boot.plugin.codegen.model.param.GenTableParam;
import com.moyu.boot.plugin.codegen.service.GenTableService;
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
public class GenTableServiceImpl extends ServiceImpl<GenTableMapper, GenTable> implements GenTableService {

    @Override
    public List<GenTable> list(GenTableParam param) {
        LambdaQueryWrapper<GenTable> queryWrapper = Wrappers.lambdaQuery(GenTable.class)
                // 关键词搜索(表名、业务名)
                .like(StrUtil.isNotBlank(param.getSearchKey()), GenTable::getTableName, param.getSearchKey())
                .or()
                .like(StrUtil.isNotBlank(param.getSearchKey()), GenTable::getBusinessName, param.getSearchKey())
                .orderByAsc(GenTable::getUpdateTime);
        // 查询
        List<GenTable> genTableList = this.list(queryWrapper);
        return genTableList;
    }

    @Override
    public PageData<GenTable> pageList(GenTableParam param) {
        // 查询条件
        LambdaQueryWrapper<GenTable> queryWrapper = Wrappers.lambdaQuery(GenTable.class)
                // 关键词搜索(表名、业务名)
                .like(StrUtil.isNotBlank(param.getSearchKey()), GenTable::getTableName, param.getSearchKey())
                .or()
                .like(StrUtil.isNotBlank(param.getSearchKey()), GenTable::getBusinessName, param.getSearchKey())
                .orderByAsc(GenTable::getUpdateTime);
        // 分页查询
        Page<GenTable> page = new Page<>(param.getPageNum(), param.getPageSize());
        Page<GenTable> rolePage = this.page(page, queryWrapper);
        return new PageData<>(rolePage.getTotal(), rolePage.getRecords());
    }

    @Override
    public GenTable detail(GenTableParam param) {
        LambdaQueryWrapper<GenTable> queryWrapper = Wrappers.lambdaQuery(GenTable.class)
                .eq(ObjectUtil.isNotEmpty(param.getId()), GenTable::getId, param.getId())
                .eq(ObjectUtil.isNotEmpty(param.getTableName()), GenTable::getTableName, param.getTableName());
        // id、code均为唯一标识
        GenTable genTable = this.getOne(queryWrapper);
        if (genTable == null) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER, "未查到指定数据");
        }
        return genTable;
    }

    @Override
    public void add(GenTableParam param) {
        // 若指定了唯一键，则必须全局唯一
        if (!Strings.isNullOrEmpty(param.getTableName())) {
            // 查询指定code
            GenTable old = this.getOne(new LambdaQueryWrapper<GenTable>().eq(GenTable::getTableName, param.getTableName()));
            if (old != null) {
                throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER, "唯一编码重复，请更换或留空自动生成");
            }
        }
        // 属性复制
        GenTable genTable = BeanUtil.copyProperties(param, GenTable.class);
        genTable.setId(null);
        this.save(genTable);
    }

    @Override
    public void deleteByIds(GenTableParam param) {
        // 待删除的id集合
        Set<Long> idSet = param.getIds();
        // 通过查询条件 物理删除
        LambdaQueryWrapper<GenTable> queryWrapper = Wrappers.lambdaQuery(GenTable.class)
                .in(ObjectUtil.isNotEmpty(idSet), GenTable::getId, idSet);
        this.remove(queryWrapper);
        // 或直接通过ids 物理删除
        // this.removeByIds(idSet);
    }

    @Override
    public void edit(GenTableParam param) {
        GenTable oldGenTable = this.detail(param);
        // 属性复制
        GenTable updateGenTable = BeanUtil.copyProperties(param, GenTable.class);
        updateGenTable.setId(oldGenTable.getId());
        this.updateById(updateGenTable);
    }
}
