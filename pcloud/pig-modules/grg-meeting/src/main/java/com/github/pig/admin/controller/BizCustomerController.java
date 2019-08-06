package com.github.pig.admin.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.github.pig.admin.model.entity.BizCustomer;
import com.github.pig.admin.service.BizCustomerService;
import com.github.pig.admin.service.SysUserService;
import com.github.pig.common.constant.CommonConstant;
import com.github.pig.common.util.Query;
import com.github.pig.common.util.R;
import com.github.pig.common.util.RespCode;
import com.github.pig.common.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;


/**
 * <p>
 * 平台中会有多个客户，每个客户下面会有多个用户、多个会议室。客户签约时会登记客户信息。 前端控制器
 * </p>
 *
 * @author fmsheng
 * @since 2018-07-11
 */
@RestController
@RequestMapping("/bizCustomer")
public class BizCustomerController extends BaseController {

    @Autowired
    private BizCustomerService bizCustomerService;

    @Autowired
    private SysUserService userService;

    /**
    * 通过ID查询
    *
    * @param id ID
    * @return BizCustomer
    */
    @GetMapping("/{id}")
    public R<BizCustomer> get(@PathVariable Integer id) {
        return new R<>(Boolean.TRUE,bizCustomerService.selectById(id));
    }


    /**
    * 分页查询信息
    *
    * @param params 分页对象
    * @return 分页对象
    */
    @RequestMapping("/page")
    public R<Page> page(@RequestParam Map<String, Object> params) {
        Integer customerId = userService.getCustomerIdByUserId();
        params.put("customerId",customerId);
        Page page = bizCustomerService.selectBizCustomerPage(new Query<>(params), new EntityWrapper<>());
        return new R<>(Boolean.TRUE, page);
    }


    /**
     * @author fmsheng
     * @param
     * @description 所属客户下拉
     * @date 2018/11/12 9:58
     */
    @RequestMapping("/select")
    public R<List<BizCustomer>> selectBizCustomerList() {
        Integer customerId = userService.getCustomerIdByUserId();

        //if (UserUtils.getUserRole().equals(MeetingConstant.USER_ROLE_Admin)){
        if (customerId == 0) {
            //管理员权限 可查看所有客户
            return new R<>(Boolean.TRUE, bizCustomerService.selectList(new EntityWrapper<BizCustomer>().ne("customer_id", 0).eq("del_flag", CommonConstant.STATUS_NORMAL)));
        }
        return new R<>(Boolean.TRUE, bizCustomerService.selectList(new EntityWrapper<BizCustomer>().ne("customer_id", 0).eq("customer_id", customerId).eq("del_flag", CommonConstant.STATUS_NORMAL)));
    }

    /**
     * 添加
     * @param  bizCustomer  实体
     * @return success/false
     */
    @PostMapping
    public R<Boolean> add(@RequestBody BizCustomer bizCustomer) {
        String customerName=bizCustomer.getCustomerName();
        if(bizCustomerService.selectBizCustomerByCustomerName(customerName)!=null)
        {
            return new R<>(Boolean.FALSE,RespCode.CNSL_CUSTOMER_NAME_EXIST);
        }
        bizCustomer.setDelFlag(CommonConstant.STATUS_NORMAL);
        bizCustomer.setCreateTime(new Date());
        return new R<>(bizCustomerService.insert(bizCustomer));
    }

    /**
     * 删除
     * @param ids
     * @return success/false
     */
    @DeleteMapping("/{id}")
    @Transactional
    public R<Boolean> delete(@PathVariable("id") String[] ids) {
        ArrayList<String> list = new ArrayList<String>(Arrays.asList(ids)) ;
        bizCustomerService.deleteBatchIds(list);
        return  new R<>(Boolean.TRUE);
    }

    /**
     * 编辑
     * @param  bizCustomer  实体
     * @return success/false
     */
    @PutMapping
    public R<Boolean> edit(@RequestBody BizCustomer bizCustomer) {
        bizCustomer.setUpdateTime(new Date());
        return new R<>(bizCustomerService.updateById(bizCustomer));
    }
}
