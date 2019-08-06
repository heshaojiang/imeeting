package com.github.pig.admin.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.github.pig.admin.model.entity.BizRoom;
import com.github.pig.common.util.Query;
import com.github.pig.common.vo.RoomVo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 客户注册时会关联注册会议室，会议室信息表记录其基本属性 Mapper 接口
 * </p>
 *
 * @author fmsheng
 * @since 2018-07-11
 */
public interface BizRoomMapper extends BaseMapper<BizRoom> {

      BizRoom selectBizRoomByRoomNo(String roomNo);

      /**
       *
       * @param query 查询对象
       * @param condition 条件
       * @return List
       */
      List<Object> selectBizRoomPage(Query<Object> query, Map<String, Object> condition);

      /**
       * 通过ID查询Room信息
       *
       * @param id 用户ID
       * @return userVo
       */
      RoomVo selectRoomVoById(Integer id);

      boolean updateUserIdById(Integer roomId);
}
