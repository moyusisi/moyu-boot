package com.moyu.boot.common.core.model;

import java.io.Serializable;
import java.util.StringJoiner;

/**
 * 分页请求
 *
 * @author shisong02
 * @since 2022-10-24
 */
public class BasePageParam implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 当前页码，当前第几页
     */
    private Integer pageNum;

    /**
     * 每页大小
     */
    private Integer pageSize;

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", BasePageParam.class.getSimpleName() + "[", "]")
                .add("pageNum=" + pageNum)
                .add("pageSize=" + pageSize)
                .toString();
    }
}
