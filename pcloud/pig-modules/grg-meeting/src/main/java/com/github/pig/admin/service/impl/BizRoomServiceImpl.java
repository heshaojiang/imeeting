package com.github.pig.admin.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.github.pig.admin.mapper.BizRoomMapper;
import com.github.pig.admin.model.entity.BizRoom;
import com.github.pig.admin.model.entity.SysUser;
import com.github.pig.admin.service.BizRoomService;
import com.github.pig.common.constant.CommonConstant;
import com.github.pig.common.util.Query;
import com.github.pig.common.util.R;
import com.github.pig.common.util.RCode;
import com.github.pig.common.vo.RoomVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Random;

/**
 * <p>
 * 客户注册时会关联注册会议室，会议室信息表记录其基本属性 服务实现类
 * </p>
 *
 * @author fmsheng
 * @since 2018-07-11
 */
@Service
public class BizRoomServiceImpl extends ServiceImpl<BizRoomMapper, BizRoom> implements BizRoomService {

    @Autowired
    private BizRoomMapper bizRoomMapper;

    private static String firstNum = "1";

    private static String[] secondNumArray = {"3", "4", "5", "7", "8"};

    @Override
    public Boolean insertBizRoom (BizRoom bizRoom){

        int result = bizRoomMapper.insert(bizRoom);
        if(result==1){
            return true;
        }
        return false;
    }

    @Override
    public BizRoom selectBizRoomByRoomNo(String roomNo) {
        return bizRoomMapper.selectBizRoomByRoomNo(roomNo);
    }

    @Override
    public Page selectBizRoomPage(Query<Object> query, EntityWrapper<Object> wrapper) {
        query.setRecords(bizRoomMapper.selectBizRoomPage(query, query.getCondition()));
        return query;
    }

    @Override
    public RoomVo selectRoomVoById(Integer id) {
        return bizRoomMapper.selectRoomVoById(id);
    }

    /**
     * 生成会议号
     */
    public String RoomNo() {

        StringBuffer stringBuffer = new StringBuffer();
        Random random = new Random();
        String secondNum = secondNumArray[random.nextInt(secondNumArray.length)];
        stringBuffer.append(firstNum);
        stringBuffer.append(secondNum);
        for (int i = 0; i < 9; i++) {
            Integer thirdNum = random.nextInt(10);
            stringBuffer.append(thirdNum.toString());
        }
        return stringBuffer.toString();
    }

    @Override
    public boolean newRoom(String room_no,SysUser sysUser) {
        //生成会议号
        String roomNo;
        while (true) {
            roomNo = RoomNo();
            BizRoom bizRoom = this.selectBizRoomByRoomNo(roomNo);
            if (bizRoom != null) {
                continue;
            } else {
                break;
            }
        }
        //创建会议室ID
        BizRoom bizRoom = new BizRoom();
        bizRoom.setUserId(sysUser.getUserId());
        bizRoom.setCustomerId(sysUser.getCustomerId());
        bizRoom.setRoomNo(roomNo);
        bizRoom.setStatus(CommonConstant.STATUS_NORMAL);
        bizRoom.setDelFlag(CommonConstant.STATUS_NORMAL);
        bizRoom.setCreateTime(new Date());
        return insertBizRoom(bizRoom);
    }

}
