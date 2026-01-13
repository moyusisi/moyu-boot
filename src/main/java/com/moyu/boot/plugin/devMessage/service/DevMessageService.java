package com.moyu.boot.plugin.devMessage.service;

import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.plugin.devMessage.model.entity.DevMessage;
import com.moyu.boot.plugin.devMessage.model.param.DevMessageParam;
import com.moyu.boot.plugin.devMessage.model.vo.DevMessageVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 站内消息服务类Service
 *
 * @author moyusisi
 * @since 2026-01-13
 */
public interface DevMessageService extends IService<DevMessage> {

    /**
     * 获取记录列表(不分页，通过条件自行控制数量)
     */
    List<DevMessageVO> list(DevMessageParam param);

    /**
     * 分页获取记录列表
     */
    PageData<DevMessageVO> pageList(DevMessageParam param);

    /**
     * 获取记录详情(通过主键或唯一键)
     */
     DevMessageVO detail(DevMessageParam param);

    /**
     * 添加记录
     */
    void add(DevMessageParam param);

    /**
     * 修改记录(通过主键id更新)
     */
    void update(DevMessageParam param);

    /**
     * 通过ids删除记录
     */
    void deleteByIds(DevMessageParam param);
}
