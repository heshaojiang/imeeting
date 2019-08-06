package com.github.pig.admin.mapper;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.github.pig.admin.model.entity.BizCustomer;
import com.github.pig.common.util.Query;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 平台中会有多个客户，每个客户下面会有多个用户、多个会议室。客户签约时会登记客户信息。 Mapper 接口
 * </p>
 *
 * @author fmsheng
 * @since 2018-07-11
 */
public interface BizCustomerMapper extends BaseMapper<BizCustomer> {

    /**
     * @param query 查询对象
     * @param condition 条件
     * @return List
     */
    List<Object> selectBizCustomerPage(Query<Object> query, Map<String, Object> condition);

    BizCustomer selectBizCustomerByCustomerName(String customerName);

}
