package com.grgbanking.grgacd.mapper;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.github.pig.common.util.Query;
import com.grgbanking.grgacd.dto.CallDayDto;
import com.grgbanking.grgacd.dto.CallMinuteDto;
import com.grgbanking.grgacd.model.AcdCalls;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 通话记录表 Mapper 接口
 * </p>
 *
 * @author tjshan
 * @since 2019-05-05
 */
@Repository
public interface AcdCallsMapper extends BaseMapper<AcdCalls> {

    /**
     * 获取每一天各种通话类型的平均速度
     *
     * param 参数设置》》
     * <p>
     *  from_time（开始时间）: makecall_time,select_time answer_time
     *  to_time（结束时间）  : select_time answer_time,hangup_time
     *  queue_id （是否将队列加入分组）: true|false
     *  call_status（呼叫状态） 呼叫中-1 响铃中-2 已接通-3 正常挂断-4 呼叫超时-5
     *</p>
     * @param query 分页
     * @param params 参数
     * @return List
     */
     List<CallDayDto> avgDayTime(Query query, Map<String,Object> params);


    /**
     * 获取每一天通话类型的数量
     * @param query 分页
     * @param params 参数
     *   queue_id （是否将队列加入分组）: true|false
     *  call_status（呼叫状态） 呼叫中-1 响铃中-2 已接通-3 正常挂断-4 呼叫超时-5
     * @return List
     */
     List<CallDayDto> sumCount(Query query, Map<String,Object> params);

    /**
     * 获取某一天各种通话类型的平均速度
     *
     * param 参数设置》》
     * <p>
     *  from_time（开始时间）: makecall_time,select_time answer_time
     *  to_time（结束时间）  : select_time answer_time,hangup_time
     *  queue_id （是否将队列加入分组）: true|false
     *  call_status（呼叫状态） 呼叫中-1 响铃中-2 已接通-3 正常挂断-4 呼叫超时-5
     *
     *  queryDate 某一天
     *</p>
     * @param query 分页
     * @param params 参数
     * @return List
     */
     List<CallMinuteDto> avgMinuteTime(Query query, Map<String,Object> params);

    /**
     * 获取每一天通话类型的数量
     * @param query 分页
     * @param params 参数
     *   queue_id （是否将队列加入分组）: true|false
     *  call_status（呼叫状态） 呼叫中-1 响铃中-2 已接通-3 正常挂断-4 呼叫超时-5
     *  queryDate 某一天
     * @return List
     */
     List<CallMinuteDto>sumMinuteCount(Query query, Map<String,Object> params);

    /**
     * 根据id获取通话详情
     * @param callId
     * @return
     */
     AcdCalls getCallById(String callId);

    /**
     * 获取通话列表
     * 参数 callStatus 通话状态 参照 call_status
     * 参数 makeCallTime 通话开始时间 参照 makecall_time
     * 参数 hangUpTime 通话结束时间 参照 hangup_time
     * 参数 callerName 发起用户
     * 参数 agentName 客服
     * 参数 keyword 查询关键字 callerName+agentName
     * @param query
     * @param params
     * @return
     */
     List<AcdCalls> getCallList(Query<AcdCalls> query, Map<String,Object> params);

    /**
     * 根据通话状态获取通话类别
     * 参数 fromDate  开始时间
     * 参数 toDate 结束时间
     * 参数 status 类型 List
     * @param query 分页
     * @param params 参数
     * @return
     */
     List<AcdCalls> getCallListWithStatus(Query<AcdCalls> query, Map<String,Object> params);

    /**
     * 获取通话列表 按照mybatis-plus的方式
     * @param query 分页
     * @param entityWrapper 查询条件
     * @return
     */
     List<AcdCalls> getCallListEntity(Query<AcdCalls> query,  @Param("ew")EntityWrapper<AcdCalls> entityWrapper);

    /**
     * 根据通话状态获取列表
     * 参数 status 类型-List 状态列表
     * 参数 fromDate 类型 Date 开始时间 参照 makecall_time
     * 参数 toDate 类型 Date 结束时间 参照 makecall_time
     * @param query 分页
     * @param params 参数
     * @return
     */
     List<Map<String,Object>> getCallCountWithStatus(Query query, Map<String,Object> params);



}
