package com.github.pig.admin.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.github.pig.admin.model.entity.BizRoom;
import com.github.pig.admin.model.entity.SysUser;
import com.github.pig.common.util.Query;
import com.github.pig.common.vo.RoomVo;

/**
 * <p>
 * 客户注册时会关联注册会议室，会议室信息表记录其基本属性 服务类
 * </p>
 *
 * @author fmsheng
 * @since 2018-07-11
 */
public interface BizRoomService extends IService<BizRoom> {

    Boolean insertBizRoom (BizRoom bizRoom);

    BizRoom selectBizRoomByRoomNo(String roomNo);

    /**
     *
     *
     * @param objectQuery         查询条件
     * @param objectEntityWrapper wapper
     * @return page
     */
    Page selectBizRoomPage(Query<Object> objectQuery, EntityWrapper<Object> objectEntityWrapper);

    /**
     * 通过ID查询用户信息
     * @param id 用户ID
     * @return 用户信息
     */
    RoomVo selectRoomVoById(Integer id);

    /**
     * @author: wjqiu
     * @date: 2019-01-16
     * @description: 创建新的会议室，room_no为空时，则使用随机数创建会议室
     */
    boolean newRoom(String room_no,SysUser sysUser);
}
