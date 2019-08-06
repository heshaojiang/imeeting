package com.github.pig.admin.model.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.github.pig.common.base.BaseEntity;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 字典表
 * </p>
 *
 * @author lengleng
 * @since 2017-11-19
 */
@TableName("sys_dict")
@Data
public class SysDict extends BaseEntity<SysDict> {

    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
	@TableId(value="id", type= IdType.AUTO)
	private Integer id;
    /**
     * 数据值
     */
	@TableField("value")
	private String value;
    /**
     * 标签名(中文)
     */
	@TableField("label")
	private String label;

	/**
	 * 标签名(英文)
	 */
	@TableField("label_en_us")
	private String labelEnUs;
    /**
     * 类型
     */
	@TableField("type")
	private String type;
    /**
     * 描述
     */
	@TableField("description")
	private String description;
    /**
     * 排序（升序）
     */
	@TableField("sort")
	private BigDecimal sort;
    /**
     * 创建时间
     */
	@TableField("create_time")
	private Date createTime;
    /**
     * 更新时间
     */
	@TableField("update_time")
	private Date updateTime;
    /**
     * 备注信息
     */
	@TableField("remarks")
	private String remarks;

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

}
