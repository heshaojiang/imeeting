package com.github.pig.admin.service;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.github.pig.admin.model.entity.BizCustomer;
import com.github.pig.common.util.Query;

import java.util.List;

/**
 * <p>
 * 平台中会有多个客户，每个客户下面会有多个用户、多个会议室。客户签约时会登记客户信息。 服务类
 * </p>
 *
 * @author fmsheng
 * @since 2018-07-11
 */
public interface BizCustomerService extends IService<BizCustomer> {

    Boolean insertBizCustomer (BizCustomer bizCustomer);

    /**
     *
     * @param objectQuery         查询条件
     * @param objectEntityWrapper wapper
     * @return page
     */
    Page selectBizCustomerPage(Query<Object> objectQuery, EntityWrapper<Object> objectEntityWrapper);


    BizCustomer selectBizCustomerByCustomerName(String customerName);

    Boolean checkPhonenumExist(String phonenum);
}
