package com.github.pig.common.vo;

import lombok.Data;

/**
 * @author fmsheng
 * @description
 * @date 2018/11/28 14:35
 * @modified by
 */
@Data
public class BizRoom {

    private static final long serialVersionUID = 1L;

    private Integer roomId;

    private Integer customerId;

    private String roomNo;

    /**
     * 0-正常，1-删除
     */
    private String delFlag;
}
