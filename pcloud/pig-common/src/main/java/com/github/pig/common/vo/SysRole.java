package com.github.pig.common.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * <p>
 * </p>
 *
 * @author lengleng
 * @since 2017-10-29
 */
@Data
public class SysRole implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer roleId;
    private String roleName;
    private String roleCode;
    private String roleDesc;
    private String level;
    private Date createTime;
    private Date updateTime;
    private String delFlag;
    private String delTime;
}
