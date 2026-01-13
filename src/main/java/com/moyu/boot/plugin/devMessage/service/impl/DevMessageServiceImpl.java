package com.moyu.boot.plugin.devMessage.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.moyu.boot.common.core.enums.ResultCodeEnum;
import com.moyu.boot.common.core.exception.BusinessException;
import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.plugin.devMessage.mapper.DevMessageMapper;
import com.moyu.boot.plugin.devMessage.model.entity.DevMessage;
import com.moyu.boot.plugin.devMessage.model.param.DevMessageParam;
import com.moyu.boot.plugin.devMessage.model.vo.DevMessageVO;
import com.moyu.boot.plugin.devMessage.service.DevMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;


/**
 * 站内消息服务实现类
 *
 * @author moyusisi
 * @since 2026-01-13
 */
@Slf4j
@Service
public class DevMessageServiceImpl extends ServiceImpl<DevMessageMapper, DevMessage> implements DevMessageService {

    @Override
    public List<DevMessageVO> list(DevMessageParam param) {
        // 查询条件
        LambdaQueryWrapper<DevMessage> queryWrapper = Wrappers.lambdaQuery(DevMessage.class);
        // 指定title查询
        queryWrapper.like(ObjectUtil.isNotEmpty(param.getTitle()), DevMessage::getTitle, param.getTitle());
        // 指定content查询
        queryWrapper.like(ObjectUtil.isNotEmpty(param.getContent()), DevMessage::getContent, param.getContent());
        // 指定sendTime查询
        Date start = param.getSendTime1();
        Date end = param.getSendTime2();
        // 范围查询-起始
        queryWrapper.ge(ObjectUtil.isNotEmpty(start), DevMessage::getSendTime, start);
        // 范围查询-截止
        queryWrapper.le(ObjectUtil.isNotEmpty(end), DevMessage::getSendTime, end);
        // 仅查询未删除的
        queryWrapper.eq(DevMessage::getDeleted, 0);
        // 指定排序
        queryWrapper.orderByDesc(DevMessage::getSendTime);
        // 查询
        List<DevMessage> devMessageList = this.list(queryWrapper);
        // 转换为voList
        List<DevMessageVO> voList = buildDevMessageVOList(devMessageList);
        return voList;
    }

    @Override
    public PageData<DevMessageVO> pageList(DevMessageParam param) {
        // 查询条件
        LambdaQueryWrapper<DevMessage> queryWrapper = Wrappers.lambdaQuery(DevMessage.class);
        // 指定title查询
        queryWrapper.like(ObjectUtil.isNotEmpty(param.getTitle()), DevMessage::getTitle, param.getTitle());
        // 指定content查询
        queryWrapper.like(ObjectUtil.isNotEmpty(param.getContent()), DevMessage::getContent, param.getContent());
        // 指定sendTime查询
        Date start = param.getSendTime1();
        Date end = param.getSendTime2();
        // 范围查询-起始
        queryWrapper.ge(ObjectUtil.isNotEmpty(start), DevMessage::getSendTime, start);
        // 范围查询-截止
        queryWrapper.le(ObjectUtil.isNotEmpty(end), DevMessage::getSendTime, end);
        // 仅查询未删除的
        queryWrapper.eq(DevMessage::getDeleted, 0);
        // 指定排序
        queryWrapper.orderByDesc(DevMessage::getSendTime);
        // 分页查询
        Page<DevMessage> page = new Page<>(param.getPageNum(), param.getPageSize());
        Page<DevMessage> devMessagePage = this.page(page, queryWrapper);
        List<DevMessageVO> voList = buildDevMessageVOList(devMessagePage.getRecords());
        return new PageData<>(devMessagePage.getTotal(), voList);
    }

    @Override
    public DevMessageVO detail(DevMessageParam param) {
        // 查询
        DevMessage devMessage = this.getById(param.getId());
        if (devMessage == null) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "未查到指定数据");
        }
        // 转换为vo
        DevMessageVO vo = BeanUtil.copyProperties(devMessage, DevMessageVO.class);
        return vo;
    }

    @Override
    public void add(DevMessageParam param) {
        // 属性复制
        DevMessage devMessage = BeanUtil.copyProperties(param, DevMessage.class);
        // 其他处理
        devMessage.setId(null);
        this.save(devMessage);
    }

    @Override
    public void update(DevMessageParam param) {
        // 通过主键id查询原有数据
        DevMessage old = this.getById(param.getId());
        if (old == null) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "更新失败，未查到原数据");
        }
        // 属性复制
        DevMessage toUpdate = BeanUtil.copyProperties(param, DevMessage.class);
        // 其他处理
        toUpdate.setId(param.getId());
        this.updateById(toUpdate);
    }

    @Override
    public void deleteByIds(DevMessageParam param) {
        // 待删除的id集合
        Set<Long> idSet = param.getIds();
        // 物理删除
        //this.removeByIds(idSet);
        // 逻辑删除
        this.update(Wrappers.lambdaUpdate(DevMessage.class).in(DevMessage::getId, idSet).set(DevMessage::getDeleted, 1));
    }

    /**
     * 实体对象生成展示对象 entityList -> voList
     */
    private List<DevMessageVO> buildDevMessageVOList(List<DevMessage> entityList) {
        List<DevMessageVO> voList = new ArrayList<>();
        if (CollectionUtils.isEmpty(entityList)) {
            return voList;
        }
        for (DevMessage entity : entityList) {
            DevMessageVO vo = BeanUtil.copyProperties(entity, DevMessageVO.class);
            voList.add(vo);
        }
        return voList;
    }
}
