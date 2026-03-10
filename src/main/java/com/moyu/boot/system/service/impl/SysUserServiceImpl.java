package com.moyu.boot.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Strings;
import com.moyu.boot.common.core.enums.DataScopeEnum;
import com.moyu.boot.common.core.enums.ResultCodeEnum;
import com.moyu.boot.common.core.exception.BusinessException;
import com.moyu.boot.common.core.model.BaseEntity;
import com.moyu.boot.common.core.model.PageData;
import com.moyu.boot.common.authZ.service.PasswordEncoder;
import com.moyu.boot.common.authZ.util.LoginUserUtils;
import com.moyu.boot.plugin.dayId.service.DayIdService;
import com.moyu.boot.system.constant.SysConstants;
import com.moyu.boot.system.mapper.SysUserMapper;
import com.moyu.boot.system.model.entity.SysUser;
import com.moyu.boot.system.model.param.SysUserParam;
import com.moyu.boot.system.model.vo.SysUserVO;
import com.moyu.boot.system.service.SysOrgService;
import com.moyu.boot.system.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author shisong
 * @description 针对表【sys_user(用户信息表)】的数据库操作Service实现
 * @createDate 2024-12-25 20:35:45
 */
@Slf4j
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Resource
    private SysOrgService sysOrgService;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private DayIdService dayIdService;

    @Override
    public List<SysUserVO> list(SysUserParam param) {
        // 查询指定的组织所有的children，包含本身
        List<String> childrenCode = new ArrayList<>();
        if (StrUtil.isNotBlank(param.getOrgCode())) {
            childrenCode = sysOrgService.childrenCodeList(param.getOrgCode());
        }
        // 查询条件
        LambdaQueryWrapper<SysUser> queryWrapper = Wrappers.lambdaQuery(SysUser.class);
        // 指定userId查询
        queryWrapper.eq(ObjectUtil.isNotEmpty(param.getUserId()), SysUser::getUserId, param.getUserId());
        // 指定account查询
        queryWrapper.eq(ObjectUtil.isNotEmpty(param.getAccount()), SysUser::getAccount, param.getAccount());
        // 指定name查询
        queryWrapper.like(ObjectUtil.isNotEmpty(param.getName()), SysUser::getName, param.getName());
        // 指定phone查询
        queryWrapper.eq(ObjectUtil.isNotEmpty(param.getPhone()), SysUser::getPhone, param.getPhone());
        // 指定codeSet查询
        queryWrapper.in(ObjectUtil.isNotEmpty(param.getCodeSet()), SysUser::getAccount, param.getCodeSet());
        // 指定orgCode时查询所属组织
        queryWrapper.in(ObjectUtil.isNotEmpty(childrenCode), SysUser::getOrgCode, childrenCode);
        // 指定status查询
        queryWrapper.eq(ObjectUtil.isNotEmpty(param.getStatus()), SysUser::getStatus, param.getStatus());
        // 仅查询未删除的
        queryWrapper.eq(SysUser::getDeleted, 0);
        // 查询
        List<SysUser> userList = this.list(queryWrapper);
        // 转换为voList
        List<SysUserVO> voList = buildSysUserVOList(userList);
        return voList;
    }

    @Override
    public PageData<SysUserVO> pageList(SysUserParam param) {
        // 查询条件
        LambdaQueryWrapper<SysUser> queryWrapper = Wrappers.lambdaQuery(SysUser.class);
        // 指定userId查询
        queryWrapper.eq(ObjectUtil.isNotEmpty(param.getUserId()), SysUser::getUserId, param.getUserId());
        // 指定account查询
        queryWrapper.eq(ObjectUtil.isNotEmpty(param.getAccount()), SysUser::getAccount, param.getAccount());
        // 指定name查询
        queryWrapper.like(ObjectUtil.isNotEmpty(param.getName()), SysUser::getName, param.getName());
        // 指定phone查询
        queryWrapper.eq(ObjectUtil.isNotEmpty(param.getPhone()), SysUser::getPhone, param.getPhone());
        // 指定orgCode查询
        String parentCode = param.getOrgCode();
        if (ObjectUtil.isNotEmpty(parentCode)) {
            // 查询指定的组织所有的children，包含本身
            List<String> children = sysOrgService.childrenCodeList(parentCode);
            queryWrapper.in(ObjectUtil.isNotEmpty(children), SysUser::getOrgCode, children);
        }
        // 指定status查询
        queryWrapper.eq(ObjectUtil.isNotEmpty(param.getStatus()), SysUser::getStatus, param.getStatus());
        // 仅查询未删除的
        queryWrapper.eq(SysUser::getDeleted, 0);
        // 非ROOT则限制数据权限
        if (!LoginUserUtils.isRoot()) {
            // 指定的列名
            Integer dataScope = LoginUserUtils.getDataScope();
            Set<String> scopeSet = LoginUserUtils.getScopes();
            if (DataScopeEnum.SELF.getCode().equals(dataScope)) {
                String username = LoginUserUtils.getUsername();
                queryWrapper.eq(SysUser::getCreateBy, username);
            } else if (DataScopeEnum.ORG.getCode().equals(dataScope)) {
                String orgCode = LoginUserUtils.getOrgCode();
                queryWrapper.eq(SysUser::getOrgCode, orgCode);
            } else if (DataScopeEnum.ORG_CHILD.getCode().equals(dataScope)) {
                queryWrapper.in(ObjectUtil.isNotEmpty(scopeSet), SysUser::getOrgCode, scopeSet);
            } else if (DataScopeEnum.COMPANY.getCode().equals(dataScope)) {
                queryWrapper.in(ObjectUtil.isNotEmpty(scopeSet), SysUser::getOrgCode, scopeSet);
            } else if (DataScopeEnum.ORG_DEFINE.getCode().equals(dataScope)) {
                queryWrapper.in(ObjectUtil.isNotEmpty(scopeSet), SysUser::getOrgCode, scopeSet);
            }
            log.debug("数据权限为:{}, 已追加过滤条件", DataScopeEnum.getByCode(dataScope));
        }
        // 分页查询
        Page<SysUser> page = new Page<>(param.getPageNum(), param.getPageSize());
        Page<SysUser> userPage = this.page(page, queryWrapper);
        List<SysUserVO> voList = buildSysUserVOList(userPage.getRecords());
        return new PageData<>(userPage.getTotal(), voList);
    }

    @Override
    public SysUserVO detail(SysUserParam param) {
        LambdaQueryWrapper<SysUser> queryWrapper = new QueryWrapper<SysUser>().checkSqlInjection().lambda()
                .eq(ObjectUtil.isNotEmpty(param.getId()), SysUser::getId, param.getId())
                .eq(ObjectUtil.isNotEmpty(param.getUserId()), SysUser::getUserId, param.getUserId())
                .eq(ObjectUtil.isNotEmpty(param.getAccount()), SysUser::getAccount, param.getAccount());
        // id、code均为唯一标识
        SysUser user = this.getOne(queryWrapper);
        if (user == null) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "未查到指定数据");
        }
        // 转换为vo
        SysUserVO vo = BeanUtil.copyProperties(user, SysUserVO.class);
        return vo;
    }

    @Override
    public void add(SysUserParam param) {
        // 若指定了唯一编码code，则必须全局唯一
        if (!Strings.isNullOrEmpty(param.getAccount())) {
            // 查询指定code
            SysUser user = this.getOne(new LambdaQueryWrapper<SysUser>()
                    .eq(SysUser::getAccount, param.getAccount()));
            if (user != null) {
                throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "此账号已存在，请更换账号");
            }
        }
        // 属性复制
        SysUser user = BeanUtil.copyProperties(param, SysUser.class);
        user.setId(null);
        // 用户唯一id，202602100001
        user.setUserId(dayIdService.nextId());
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
            user.setPassword(passwordEncoder.encode(SysConstants.DEFAULT_PASSWORD));
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
        // 逻辑删除
        UpdateWrapper<SysUser> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("id", idSet).set("deleted", 1);
        this.update(updateWrapper);
    }

    @Override
    public void updatePassword(SysUserParam param) {
        // 先查原有数据
        SysUser old = this.getOne(Wrappers.lambdaQuery(SysUser.class).eq(SysUser::getAccount, param.getAccount()));
        if (old == null) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "更新失败，未查到原数据");
        }
        this.update(Wrappers.lambdaUpdate(SysUser.class)
                .eq(SysUser::getId, old.getId())
                .set(SysUser::getPassword, passwordEncoder.encode(param.getPassword())));
    }

    @Override
    public void resetPassword(SysUserParam param) {
        // 先查原有数据
        SysUser old = this.getOne(Wrappers.lambdaQuery(SysUser.class).eq(SysUser::getAccount, param.getAccount()));
        if (old == null) {
            throw new BusinessException(ResultCodeEnum.INVALID_PARAMETER_ERROR, "更新失败，未查到原数据");
        }
        this.update(Wrappers.lambdaUpdate(SysUser.class)
                .eq(SysUser::getId, old.getId())
                .set(SysUser::getPassword, passwordEncoder.encode(SysConstants.DEFAULT_PASSWORD)));
    }

    /**
     * 实体对象生成展示对象 entityList -> voList
     */
    private List<SysUserVO> buildSysUserVOList(List<SysUser> entityList) {
        List<SysUserVO> voList = new ArrayList<>();
        if (CollectionUtils.isEmpty(entityList)) {
            return voList;
        }
        for (SysUser entity : entityList) {
            SysUserVO vo = BeanUtil.copyProperties(entity, SysUserVO.class);
            voList.add(vo);
        }
        return voList;
    }
}




