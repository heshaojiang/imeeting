package com.github.pig.admin.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.github.pig.admin.model.entity.BizRoom;
import com.github.pig.admin.service.BizRoomService;
import com.github.pig.admin.service.SysUserService;
import com.github.pig.common.constant.CommonConstant;
import com.github.pig.common.constant.MeetingConstant;
import com.github.pig.common.util.Query;
import com.github.pig.common.util.R;
import com.github.pig.common.util.RCode;
import com.github.pig.common.util.RespCode;
import com.github.pig.common.vo.RoomVo;
import com.github.pig.common.web.BaseController;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 客户注册时会关联注册会议室，会议室信息表记录其基本属性 前端控制器
 * </p>
 *
 * @author fmsheng
 * @since 2018-07-11
 */
@RestController
@RequestMapping("/room")
public class BizRoomController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(BizRoomController.class);

    @Autowired
    private SysUserService userService;

    @Autowired
    private BizRoomService bizRoomService;

    /**
    * 通过ID查询
    *
    * @param id ID
    * @return BizRoom
    */
    @GetMapping("/{id}")
    public R<RoomVo> get(@PathVariable Integer id) {
        return new R<>(Boolean.TRUE,bizRoomService.selectRoomVoById(id));
    }


    /**
    * 分页查询信息
    *
    * @param params 分页对象
    * @return 分页对象
    */
    @RequestMapping("/page")
    public R<Page> page(@RequestParam Map<String, Object> params) {

        //String userName = UserUtils.getUser();
        //UserVo userVo = userService.findUserByUsername(userName);

        //会议室拥有者
        //params.put("roomUser",userVo.getUserId());

        //当前用户所属客户
        Integer customerId = userService.getCustomerIdByUserId();

        params.put("customerId", customerId);

        return new R<>(Boolean.TRUE, bizRoomService.selectBizRoomPage(new Query<>(params), new EntityWrapper<>()));
    }

    /**
     * 添加
     * @param  bizRoom  实体
     * @return success/false
     */
    @PostMapping
    public R<Boolean> add(@RequestBody BizRoom bizRoom) {
        if (StringUtils.isEmpty(bizRoom.getRoomName())){
            return new R<>(Boolean.FALSE, RespCode.CNSL_NAME_EMPTY);
        }
        if(bizRoomService.selectBizRoomByRoomNo(bizRoom.getRoomNo())!=null){
            return new R<>(Boolean.FALSE, RespCode.CNSL_ROOM_NO_EXIST);
        }

        bizRoom.setDelFlag(CommonConstant.STATUS_NORMAL);
        bizRoom.setCreateTime(new Date());
        return new R<>(bizRoomService.insert(bizRoom));
    }

    /**
     * 删除
     * @param ids ID
     * @return success/false
     */
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable("id") String[] ids) {
        for (String  id: ids) {
            BizRoom bizRoom = new BizRoom();
            bizRoom.setRoomId(Integer.parseInt(id));
            bizRoom.setUpdateTime(new Date());
            bizRoom.setDelFlag(CommonConstant.STATUS_DEL);
            boolean result = bizRoomService.updateById(bizRoom);
            if (!result){
                return  new R<>(Boolean.FALSE);
            }
        }
        return new R<>(Boolean.TRUE);
    }

    /**
     * 编辑
     * @param  bizRoom  实体
     * @return success/false
     */
    @PutMapping
    public R<Boolean>  edit(@RequestBody BizRoom bizRoom) {
        bizRoom.setUpdateTime(new Date());
        return new R<>(bizRoomService.updateById(bizRoom));
    }

    /**
     * 根据客户ID，获取会议号
     *
     * @param
     * @return success/false
     */
    @GetMapping("/getRoomNo")
    public R<List<BizRoom>> getRoomNo() {
        Integer customerId = userService.getCustomerIdByUserId();
        List<BizRoom> roomList = bizRoomService.selectList(new EntityWrapper<BizRoom>().eq(CommonConstant.DEL_FLAG,CommonConstant.STATUS_NORMAL).eq("customer_id",customerId));
        return new R<>(true,roomList);
    }
}
