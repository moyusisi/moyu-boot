package com.moyu.boot.common.mybatis.config;


import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DataPermissionInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.moyu.boot.common.mybatis.entity.BaseEntity;
import com.moyu.boot.common.mybatis.handler.CustomDataPermissionHandler;
import com.moyu.boot.common.mybatis.handler.CustomMetaObjectHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Mybatis Plus 配置
 *
 * @author shisong
 * @since 2024-11-24
 */
@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
public class MybatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 乐观锁插件
        interceptor.addInnerInterceptor(optimisticLockerInnerInterceptor());
        // 阻断插件
        interceptor.addInnerInterceptor(blockAttackInnerInterceptor());
        // 数据权限
        interceptor.addInnerInterceptor(new DataPermissionInterceptor(new CustomDataPermissionHandler()));
        // 分页插件。如果配置多个插件, 切记分页最后添加
        interceptor.addInnerInterceptor(paginationInnerInterceptor());
        return interceptor;
    }

    /**
     * 分页插件 https://baomidou.com/plugins/pagination/
     */
    public PaginationInnerInterceptor paginationInnerInterceptor() {
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
        // 设置数据库类型为mysql
        paginationInnerInterceptor.setDbType(DbType.MYSQL);
        // 设置最大单页限制数量，默认 500 条，-1 不受限制
        paginationInnerInterceptor.setMaxLimit(100L);
        return paginationInnerInterceptor;
    }

    /**
     * 乐观锁插件 https://baomidou.com/plugins/optimistic-locker/
     */
    public OptimisticLockerInnerInterceptor optimisticLockerInnerInterceptor() {
        return new OptimisticLockerInnerInterceptor();
    }

    /**
     * 如果是对全表的删除或更新操作，就会终止该操作 https://baomidou.com/plugins/block-attack/
     */
    public BlockAttackInnerInterceptor blockAttackInnerInterceptor() {
        return new BlockAttackInnerInterceptor();
    }

    /**
     * 自定义公共字段自动填充
     *
     * @see BaseEntity
     */
    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new CustomMetaObjectHandler();
    }
}
