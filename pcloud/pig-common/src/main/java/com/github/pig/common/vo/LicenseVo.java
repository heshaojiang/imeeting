package com.github.pig.common.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author: wjqiu
 * @date: 2019-02-25
 * @description:
 */
@Data
public class LicenseVo implements Serializable {

    private String machineCode;
    private String dateStart;
    private String dateEnd;
    private String maxMeetingNum;
    private String maxViewNum;
    private String maxVideoNum;

}
