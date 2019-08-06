package com.github.pig.admin.model.entity;

import com.baomidou.mybatisplus.enums.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.github.pig.common.base.BaseEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 平台中会有多个客户，每个客户下面会有多个用户、多个会议室。客户签约时会登记客户信息。
 * </p>
 *
 * @author fmsheng
 * @since 2018-12-25
 */
@TableName("biz_customer")
@Data
public class BizCustomer extends BaseEntity<BizCustomer> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "customer_id", type = IdType.AUTO)
    private Integer customerId;
    /**
     * 客户编号，如：18020
     */
    @TableField("customer_no")
    private String customerNo;

    @TableField("customer_name")
    private String customerName;
    /**
     * 客户简介
     */
    @TableField("customer_info")
    private String customerInfo;
    /**
     * 联系电话
     */
	@TableField("telephone")
    private String call;
    private String email;
    /**
     * 签约方式：0 -- 包月； 1 -- 包年； 2 -- 按次； 3 -- 自营；4 - 按时间
     */
    private String type;
    /**
     * 0 --并发会议;1 --并发通话方;2 --并发接入方
     */
    private String licence;
    @TableField("create_time")
    private Date createTime;
    @TableField("update_time")
    private Date updateTime;
    /**
     * 计费开始时间（签约方式为包年、包月时使用）
     */
    @TableField("billing_start_time")
    private Date billingStartTime;
    /**
     * 计费结束时间（签约方式为包年、包月时使用）
     */
    @TableField("billing_end_time")
    private Date billingEndTime;
    /**
     * 剩余流量或次数（签约方式为按次时为剩余使用次数、按流量时为剩余流量）
     */
    @TableField("billing_left")
    private String billingLeft;
    /**
     * 最大会议室数量
     */
    @TableField("num_room")
    private Integer numRoom;
    /**
     * 最大会议数量
     */
    @TableField("num_meeting")
    private Integer numMeeting;
    /**
     * 最大通话方数量
     */
    @TableField("num_video")
    private Integer numVideo;
    /**
     * 最大接入方数量
     */
    @TableField("num_join")
    private Integer numJoin;
    /**
     * 付款方式：1-现金；2-银行转账；3-第三方支付
     */
    @TableField("pay_type")
    private String payType;
    /**
     * 0-未签约；1-正常；2-过期；3-已解约
     */
    private String status;

    /**
     * 删除时间
     */
    @TableField("del_time")
    private Date delTime;

    @Override
    protected Serializable pkVal() {
        return this.customerId;
    }
}
