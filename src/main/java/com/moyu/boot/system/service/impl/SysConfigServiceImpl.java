package com.moyu.boot.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.moyu.boot.common.core.enums.ResultCodeEnum;
import com.moyu.boot.common.core.enums.SortOrderEnum;
import com.moyu.boot.common.core.exception.BusinessException;
import com.moyu.boot.common.core.model.BaseEntity;
import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.system.mapper.SysConfigMapper;
import com.moyu.boot.system.model.entity.SysConfig;
import com.moyu.boot.system.model.param.SysConfigParam;
import com.moyu.boot.system.model.vo.SysConfigVO;
import com.moyu.boot.system.service.SysConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 系统配置服务实现类
 *
 * @author moyusisi
 * @since 2026-05-28
 */
@Slf4j
@Service
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements SysConfigService {

    @Override
    public List<SysConfigVO> list(SysConfigParam param) {
        // 查询条件
        QueryWrapper<SysConfig> queryWrapper = Wrappers.query(SysConfig.class).checkSqlInjection();
        // 指定keyTitle查询
        queryWrapper.lambda().like(ObjectUtil.isNotEmpty(param.getKeyTitle()), SysConfig::getKeyTitle, param.getKeyTitle());
        // 指定keyName查询
        queryWrapper.lambda().like(ObjectUtil.isNotEmpty(param.getKeyName()), SysConfig::getKeyName, param.getKeyName());
        // 仅查询未删除的
        queryWrapper.lambda().eq(SysConfig::getDeleted, 0);
        // 指定排序
        if (ObjectUtil.isAllNotEmpty(param.getSortField(), param.getSortOrder())) {
            // 检查排序方式
            SortOrderEnum.validate(param.getSortOrder());
            queryWrapper.orderBy(true, param.getSortOrder().equals(SortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(param.getSortField()));
        }
        // 查询
        List<SysConfig> sysConfigList = this.list(queryWrapper);
        // 转换为voList
        List<SysConfigVO> voList = buildSysConfigVOList(sysConfigList);
        return voList;
    }

    @Override
    public PageData<SysConfigVO> pageList(SysConfigParam param) {
        // 查询条件
        QueryWrapper<SysConfig> queryWrapper = Wrappers.query(SysConfig.class).checkSqlInjection();
        // 指定keyTitle查询
        queryWrapper.lambda().like(ObjectUtil.isNotEmpty(param.getKeyTitle()), SysConfig::getKeyTitle, param.getKeyTitle());
        // 指定keyName查询
        queryWrapper.lambda().like(ObjectUtil.isNotEmpty(param.getKeyName()), SysConfig::getKeyName, param.getKeyName());
        // 指定keyvalue查询
        queryWrapper.lambda().like(ObjectUtil.isNotEmpty(param.getKeyValue()), SysConfig::getKeyValue, param.getKeyValue());
        // 仅查询未删除的
        queryWrapper.lambda().eq(SysConfig::getDeleted, 0);
        // 排序方式
        if (ObjectUtil.isAllNotEmpty(param.getSortField(), param.getSortOrder())) {
            // 检查排序方式
            SortOrderEnum.validate(param.getSortOrder());
            queryWrapper.orderBy(true, param.getSortOrder().equals(SortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(param.getSortField()));
        }
        // 分页查询
        Page<SysConfig> page = new Page<>(param.getPageNum(), param.getPageSize());
        Page<SysConfig> sysConfigPage = this.page(page, queryWrapper);
        List<SysConfigVO> voList = buildSysConfigVOList(sysConfigPage.getRecords());
        return new PageData<>(sysConfigPage.getTotal(), voList);
    }

    @Override
    public SysConfigVO detail(SysConfigParam param) {
        // 查询
        SysConfig sysConfig = this.getById(param.getId());
        if (sysConfig == null) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "未查到指定数据");
        }
        // 转换为vo
        SysConfigVO vo = BeanUtil.copyProperties(sysConfig, SysConfigVO.class);
        return vo;
    }

    @Override
    public String getValue(String keyName) {
        if (StrUtil.isEmpty(keyName)) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "keyName不能为空");
        }
        // 查询条件
        LambdaQueryWrapper<SysConfig> queryWrapper = Wrappers.lambdaQuery(SysConfig.class);
        // 查询指定字段
        queryWrapper.select(SysConfig::getKeyValue);
        // 指定keyName查询
        queryWrapper.eq(SysConfig::getKeyName, keyName);
        // 仅查询未删除的
        queryWrapper.eq(SysConfig::getDeleted, 0);
        // 仅查询生效中的
        queryWrapper.eq(SysConfig::getStatus, 0);

        // 配置值
        String keyValue = null;
        // 单个查询
        SysConfig config = this.getOne(queryWrapper);
        if (ObjectUtil.isNotEmpty(config)) {
            keyValue = config.getKeyValue();
        }
        return keyValue;
    }

    @Override
    public void add(SysConfigParam param) {
        // 属性复制
        SysConfig sysConfig = BeanUtil.copyProperties(param, SysConfig.class);
        // 其他处理
        sysConfig.setId(null);
        this.save(sysConfig);
    }

    @Override
    public void update(SysConfigParam param) {
        // 通过主键id查询原有数据
        SysConfig old = this.getById(param.getId());
        if (old == null) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "更新失败，未查到原数据");
        }
        // 属性复制
        SysConfig toUpdate = BeanUtil.copyProperties(param, SysConfig.class, BaseEntity.UPDATE_TIME, BaseEntity.UPDATE_BY);
        // 其他处理
        toUpdate.setId(param.getId());
        this.updateById(toUpdate);
    }

    @Override
    public void deleteByIds(SysConfigParam param) {
        // 待删除的id集合
        Set<Long> idSet = param.getIds();
        // 删除时先查再删
        LambdaQueryWrapper<SysConfig> queryWrapper = Wrappers.lambdaQuery(SysConfig.class);
        // 查询指定字段
        queryWrapper.select(SysConfig::getId);
        // 指定idSet集合查询
        queryWrapper.in(ObjectUtil.isNotEmpty(idSet), SysConfig::getId, idSet);
        // 查询
        List<SysConfig> configList = this.list(queryWrapper);
        // 要删除的和查询到的进行比对
        if (ObjectUtil.notEqual(idSet.size(), configList.size())) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "删除失败，未查到原数据");
        }
        // 物理删除
        //this.removeByIds(idSet);
        // 逻辑删除
        this.update(Wrappers.lambdaUpdate(SysConfig.class).in(SysConfig::getId, idSet).set(SysConfig::getDeleted, 1));
    }

    /**
     * 实体对象生成展示对象 entityList -> voList
     */
    private List<SysConfigVO> buildSysConfigVOList(List<SysConfig> entityList) {
        List<SysConfigVO> voList = new ArrayList<>();
        if (CollectionUtils.isEmpty(entityList)) {
            return voList;
        }
        for (SysConfig entity : entityList) {
            SysConfigVO vo = BeanUtil.copyProperties(entity, SysConfigVO.class);
            voList.add(vo);
        }
        return voList;
    }
}
