package com.github.pig.common.base;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableLogic;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * @description:通用实体抽象类
 * @author: hsjiang
 * @date: 2019-05-23 13:58
 **/
@Data
public abstract class BaseEntity<T extends Model> extends Model<T> {
    /**
     * 0-正常,1-删除
     * 添加逻辑删除的注解
     */
    @TableLogic
    @TableField("del_flag")
    @JsonIgnore
    private String delFlag = "0";
    /**
     * 是否为更新的标志
     */
    @TableField(exist = false)
    private boolean update = false;
    /** 
     * @description: 添加一些通用属性的值
     * @author: hsjiang 
     * @date: 2019/5/23 
    **/
    public void setCommonValue(){
        if(update){
            //设置修改时间
        }
    }

}
