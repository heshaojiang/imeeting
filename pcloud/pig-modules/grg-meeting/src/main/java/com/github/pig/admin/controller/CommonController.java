package com.github.pig.admin.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.github.pig.admin.model.entity.SysUser;
import com.github.pig.admin.service.BizCustomerService;
import com.github.pig.admin.service.BizMeetingService;
import com.github.pig.admin.service.IvxMeetingService;
import com.github.pig.admin.service.SysUserService;
import com.github.pig.common.util.R;
import com.github.pig.common.util.RespCode;
import com.github.pig.common.web.BaseController;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author: wjqiu
 * @date: 2019-01-24
 * @description: 通用接口
 */
@RestController
@RequestMapping("/common")
public class CommonController extends BaseController {

    @Autowired
    private BizMeetingService bizMeetingService;

    @Autowired
    private SysUserService userService;

    @Autowired
    private IvxMeetingService ivxMeetingService;

    @Autowired
    private BizCustomerService bizCustomerService;
    private static final Logger logger = LoggerFactory.getLogger(CommonController.class);

    /**
     * @author: wjqiu
     * @date: 2019-01-24
     * @description: 检查给定的字符串是否已经在使用中
     * 返回 SUCCESS 则字符串未在使用
     * 返回 FAIL 则字符串已经在使用中
     */
    @RequestMapping("/validate")
    public R<Boolean> validate(@RequestParam Map<String, Object> params) {
        String str = (String) params.get("str");
        String type = (String) params.get("type");
        if (StringUtils.isEmpty(type) || StringUtils.isEmpty(str)){
            return new R<>(Boolean.FALSE, RespCode.IME_INVALIDPARAMETER);
        }
        boolean bValidate = false;
        if (type.equals("meetingid")){
            //会议ID
            if (bizMeetingService.findMeetingVoByMeetingId(str) == null){
                bValidate = true;
            }
        } else if (type.equals("username")){
            //用户名
            if (!userService.checkNameExist(str)){
                bValidate = true;
            }
        } else if (type.equals("customername")){
            //客户名
            if (bizCustomerService.selectBizCustomerByCustomerName(str) == null){
                bValidate = true;
            }
        } else if (type.equals("phonenum")){
            //用户手机号
            if (!userService.checkPhonenumExist(str)){
                bValidate = true;
            }
        }else if (type.equals("customerPhoneNum")){
            //客户手机号
            if (!bizCustomerService.checkPhonenumExist(str)){
                bValidate = true;
            }
        }
        if (bValidate) {
            return new R<>(Boolean.TRUE, RespCode.SUCCESS);
        } else {
            return new R<>(Boolean.FALSE, RespCode.FAIL);
        }

    }
}
