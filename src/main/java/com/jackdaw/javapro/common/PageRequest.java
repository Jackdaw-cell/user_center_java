package com.jackdaw.javapro.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用分页请求类
 * @author Jackdaw
 */
@Data
public class PageRequest implements Serializable {

    private static final long serialVersionUID = -5860707094194210842L;

    /**
     * 页面大小
     */
    protected long pageSize = 10;

    /**
     * 页码
     */
    protected long pageNum = 1;
}
