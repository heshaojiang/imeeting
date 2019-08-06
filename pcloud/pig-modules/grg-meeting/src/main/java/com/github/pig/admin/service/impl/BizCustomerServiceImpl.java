package com.github.pig.admin.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.github.pig.admin.mapper.BizCustomerMapper;
import com.github.pig.admin.model.entity.BizCustomer;
import com.github.pig.admin.model.entity.SysUser;
import com.github.pig.admin.service.BizCustomerService;
import com.github.pig.common.util.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 平台中会有多个客户，每个客户下面会有多个用户、多个会议室。客户签约时会登记客户信息。 服务实现类
 * </p>
 *
 * @author fmsheng
 * @since 2018-07-11
 */
@Service
public class BizCustomerServiceImpl extends ServiceImpl<BizCustomerMapper, BizCustomer> implements BizCustomerService {

    @Autowired
    private BizCustomerMapper bizCustomerMapper;

    @Override
    public Boolean insertBizCustomer (BizCustomer bizCustomer){

        int result = bizCustomerMapper.insert(bizCustomer);
        if(result==1){
            return true;
        }
        return false;
    }

    /**
     *
     * @param query   查询条件
     * @param wrapper wapper
     * @return page
     */
    @Override
    public Page selectBizCustomerPage(Query<Object> query, EntityWrapper<Object> wrapper) {
        query.setRecords(bizCustomerMapper.selectBizCustomerPage(query, query.getCondition()));
        return query;
    }

    @Override
    public BizCustomer selectBizCustomerByCustomerName(String customerName) {
        return bizCustomerMapper.selectBizCustomerByCustomerName(customerName);
    }

    @Override
    public Boolean checkPhonenumExist(String phonenum) {
        Boolean exist = false;
        if (selectOne(new EntityWrapper<BizCustomer>().eq("telephone", phonenum).and().eq("del_flag", "0")) != null){
            exist = true;
        }
        return exist;
    }
}
