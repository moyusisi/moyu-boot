package com.moyu.boot.common.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * 分页数据
 *
 * @author shisong02
 * @since 2023-12-28
 */
public class PageData<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 总数量
     */
    private Long total;

    /**
     * 当前页内数据
     */
    private List<T> records;

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }

    /**
     * 返回一个无数据的空结果页
     */
    public static <T> PageData<T> emptyPage() {
        return new PageData<>(0L, new ArrayList<>());
    }

    /**
     * 构造方法
     */
    public PageData(Long total, List<T> records) {
        this.total = total;
        this.records = records;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PageData.class.getSimpleName() + "[", "]")
                .add("total=" + total)
                .add("records=" + records)
                .toString();
    }
}
