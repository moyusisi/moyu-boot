package com.moyu.boot.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.base.Strings;
import com.moyu.boot.common.core.enums.ResultCodeEnum;
import com.moyu.boot.common.core.exception.BusinessException;
import com.moyu.boot.common.core.model.BaseEntity;
import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.common.mybatis.util.DataScopeHelper;
import com.moyu.boot.plugin.daySeq.service.DaySeqService;
import com.moyu.boot.system.constant.SysConstants;
import com.moyu.boot.system.mapper.SysUserMapper;
import com.moyu.boot.system.model.entity.SysUser;
import com.moyu.boot.system.model.param.SysUserParam;
import com.moyu.boot.system.model.vo.SysUserVO;
import com.moyu.boot.system.service.PasswordEncoder;
import com.moyu.boot.system.service.SysConfigService;
import com.moyu.boot.system.service.SysOrgService;
import com.moyu.boot.system.service.SysUserService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 用户信息服务实现类
 *
 * @author shisong
 * @since 2024-12-25 20:35:45
 */
@Slf4j
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Resource
    private SysOrgService sysOrgService;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private SysConfigService sysConfigService;

    @Resource
    private DaySeqService daySeqService;

    @Override
    public List<SysUserVO> list(SysUserParam param) {
        // 指定orgCode的所有的children，包含本身
        List<String> children = new ArrayList<>();
        if (StrUtil.isNotBlank(param.getOrgCode())) {
            children = sysOrgService.childrenCodeList(param.getOrgCode());
        }
        // 查询条件
        QueryWrapper queryWrapper = QueryWrapper.create();
        // 指定userId查询
        queryWrapper.eq(SysUser::getUserId, param.getUserId(), ObjectUtil.isNotEmpty(param.getUserId()));
        // 指定account查询
        queryWrapper.eq(SysUser::getAccount, param.getAccount(), ObjectUtil.isNotEmpty(param.getAccount()));
        // 指定name查询
        queryWrapper.like(SysUser::getName, param.getName(), ObjectUtil.isNotEmpty(param.getName()));
        // 指定phone查询
        queryWrapper.eq(SysUser::getPhone, param.getPhone(), ObjectUtil.isNotEmpty(param.getPhone()));
        // 指定codeSet查询
        queryWrapper.in(SysUser::getAccount, param.getCodeSet(), ObjectUtil.isNotEmpty(param.getCodeSet()));
        // 指定orgCode时查children
        queryWrapper.in(SysUser::getOrgCode, children, ObjectUtil.isNotEmpty(children));
        // 指定status查询
        queryWrapper.eq(SysUser::getStatus, param.getStatus(), ObjectUtil.isNotEmpty(param.getStatus()));
        // 仅查询未删除的
        queryWrapper.eq(SysUser::getDeleted, 0);
        // 查询
        List<SysUserVO> voList = this.listAs(queryWrapper, SysUserVO.class);
        return voList;
    }

    @Override
    public PageData<SysUserVO> pageList(SysUserParam param) {
        // 指定orgCode的所有的children，包含本身
        List<String> children = new ArrayList<>();
        if (StrUtil.isNotBlank(param.getOrgCode())) {
            children = sysOrgService.childrenCodeList(param.getOrgCode());
        }
        // 查询条件
        QueryWrapper queryWrapper = QueryWrapper.create();
        // 指定userId查询
        queryWrapper.eq(SysUser::getUserId, param.getUserId(), ObjectUtil.isNotEmpty(param.getUserId()));
        // 指定account查询
        queryWrapper.eq(SysUser::getAccount, param.getAccount(), ObjectUtil.isNotEmpty(param.getAccount()));
        // 指定name查询
        queryWrapper.like(SysUser::getName, param.getName(), ObjectUtil.isNotEmpty(param.getName()));
        // 指定phone查询
        queryWrapper.eq(SysUser::getPhone, param.getPhone(), ObjectUtil.isNotEmpty(param.getPhone()));
        // 指定orgCode时查children
        queryWrapper.in(SysUser::getOrgCode, children, ObjectUtil.isNotEmpty(children));
        // 指定status查询
        queryWrapper.eq(SysUser::getStatus, param.getStatus(), ObjectUtil.isNotEmpty(param.getStatus()));
        // 仅查询未删除的
        queryWrapper.eq(SysUser::getDeleted, 0);
        // 限制数据权限
        DataScopeHelper.dataScopeFilter(queryWrapper, SysUser::getCreateBy, SysUser::getOrgCode);
        // 分页查询
        Page<SysUserVO> page = Page.of(param.getPageNum(), param.getPageSize());
        Page<SysUserVO> voPage = this.pageAs(page, queryWrapper, SysUserVO.class);
        return new PageData<>(voPage.getTotalRow(), voPage.getRecords());
    }

    @Override
    public List<SysUserVO> userSelector(SysUserParam param) {
        // 查询条件
        QueryWrapper queryWrapper = QueryWrapper.create();
        // 仅查询部分字段
        queryWrapper.select(SysUser::getAccount, SysUser::getName);
        // 指定account查询
        queryWrapper.like(SysUser::getAccount, param.getAccount(), ObjectUtil.isNotEmpty(param.getAccount()));
        // 限制数据权限
        DataScopeHelper.dataScopeFilter(queryWrapper, SysUser::getAccount, SysUser::getOrgCode);
        // 查询
        return this.listAs(queryWrapper, SysUserVO.class);
    }

    @Override
    public SysUserVO detail(SysUserParam param) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.eq(SysUser::getId, param.getId(), ObjectUtil.isNotEmpty(param.getId()))
                .eq(SysUser::getUserId, param.getUserId(), ObjectUtil.isNotEmpty(param.getUserId()))
                .eq(SysUser::getAccount, param.getAccount(), ObjectUtil.isNotEmpty(param.getAccount()));
        // id、code均为唯一标识
        SysUserVO vo = this.getOneAs(queryWrapper, SysUserVO.class);
        if (vo == null) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "未查到指定数据");
        }
        return vo;
    }

    @Override
    public void add(SysUserParam param) {
        // 若指定了唯一编码code，则必须全局唯一
        if (!Strings.isNullOrEmpty(param.getAccount())) {
            // 查询指定code
            SysUser user = this.getOne(QueryWrapper.create()
                    .eq(SysUser::getAccount, param.getAccount()));
            if (user != null) {
                throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "此账号已存在，请更换账号");
            }
        }
        // 属性复制
        SysUser user = BeanUtil.copyProperties(param, SysUser.class);
        user.setId(null);
        // 用户唯一id，202602110001
        user.setUserId(daySeqService.nextId());
        // user.setUserId(IdUtil.getSnowflakeNextIdStr());
        // 若指定了直属组织，则设置所属组织
        if (ObjectUtil.isNotEmpty(user.getOrgCode())) {
            // 获取组织结构树
            Tree<String> rootTree = sysOrgService.singleTree();
            Tree<String> orgNode = rootTree.getNode(user.getOrgCode());
            // 设置直属机构名称
            user.setOrgName(orgNode.getName().toString());
            // 组织机构层级路径,逗号分隔,父节点在后
            List<String> list = TreeUtil.getParentsId(orgNode, true);
            user.setOrgPath(SysConstants.COMMA_JOINER.join(list));
        }
        // 初始密码为系统默认
        if (ObjectUtil.isEmpty(user.getPassword())) {
            String defaultPwd = sysConfigService.getValueWithCache(SysConstants.Config.DEFAULT_PWD);
            defaultPwd = StrUtil.emptyToDefault(defaultPwd, SysConstants.DEFAULT_PASSWORD);
            user.setPassword(passwordEncoder.encode(defaultPwd));
        }
        this.save(user);
    }

    @Override
    public void update(SysUserParam param) {
        // 通过主键id查询原有数据
        SysUser old = this.getById(param.getId());
        if (old == null) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "更新失败，未查到原数据");
        }
        // 属性复制(userId、account不能变)
        String[] ignoreProperties = new String[]{"userId", "account", BaseEntity.UPDATE_TIME, BaseEntity.UPDATE_BY};
        SysUser toUpdate = BeanUtil.copyProperties(param, SysUser.class, ignoreProperties);
        toUpdate.setId(old.getId());
        // 若新指定了直属组织，则设置所属组织
        if (ObjectUtil.notEqual(old.getOrgCode(), toUpdate.getOrgCode()) && ObjectUtil.isNotEmpty(toUpdate.getOrgCode())) {
            // 获取组织结构树
            Tree<String> rootTree = sysOrgService.singleTree();
            Tree<String> orgNode = rootTree.getNode(param.getOrgCode());
            // 设置直属机构名称
            toUpdate.setOrgName(orgNode.getName().toString());
            // 组织机构层级路径,逗号分隔,父节点在后
            List<String> list = TreeUtil.getParentsId(orgNode, true);
            toUpdate.setOrgPath(SysConstants.COMMA_JOINER.join(list));
        }
        this.updateById(toUpdate);
    }

    @Override
    public void deleteByIds(SysUserParam param) {
        // 待删除的id集合
        Set<Long> idSet = param.getIds();
        // 删除时先查再删
        long count = this.count(QueryWrapper.create().in(SysUser::getId, idSet));
        // 查到的数量比对
        if (idSet.size() != count) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "删除失败，未查到原数据");
        }
        // 物理删除
        //this.removeByIds(idSet);
        // 逻辑删除
        UpdateChain.of(SysUser.class)
                .set(SysUser::getDeleted, 1)
                .where(SysUser::getId).in(idSet)
                .update();
    }

    @Override
    public void updatePassword(SysUserParam param) {
        // 先查原有数据
        SysUser old = this.getOne(QueryWrapper.create().eq(SysUser::getAccount, param.getAccount()));
        if (old == null) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "更新失败，未查到原数据");
        }
        UpdateChain.of(SysUser.class)
                .set(SysUser::getPassword, passwordEncoder.encode(param.getPassword()))
                .where(SysUser::getId).eq(old.getId())
                .update();
    }

    @Override
    public void resetPassword(SysUserParam param) {
        // 先查原有数据
        SysUser old = this.getOne(QueryWrapper.create().eq(SysUser::getAccount, param.getAccount()));
        if (old == null) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "更新失败，未查到原数据");
        }
        String defaultPwd = sysConfigService.getCacheValue(SysConstants.Config.DEFAULT_PWD);
        defaultPwd = StrUtil.emptyToDefault(defaultPwd, SysConstants.DEFAULT_PASSWORD);
        UpdateChain.of(SysUser.class)
                .set(SysUser::getPassword, passwordEncoder.encode(defaultPwd))
                .where(SysUser::getId).eq(old.getId())
                .update();
    }
}




