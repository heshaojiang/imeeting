package com.grgbanking.grgacd.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.github.pig.common.util.Query;
import com.grgbanking.grgacd.common.Agent;
import com.grgbanking.grgacd.common.CallStatus;
import com.grgbanking.grgacd.dto.CallDayDto;
import com.grgbanking.grgacd.dto.CallMinuteDto;
import com.grgbanking.grgacd.dto.ChatRecord;
import com.grgbanking.grgacd.model.AcdCalls;

import java.util.List;

/**
 * <p>
 * 通话记录表 服务类
 * </p>
 *
 * @author ${author}
 * @since 2019-05-05
 */
public interface AcdCallsService extends IService<AcdCalls> {

    AcdCalls addCall(AcdCalls acdCalls);


    /***************************************************************/


    /**
     * 平均接听速度
     * @param query 分页
     * @return page
     */
    Page<CallDayDto> getAvgAnswerSpeed(Query<CallDayDto> query);


    /**
     * 平均无接听时间
     * @param query 分页
     * @return page
     */
    Page<CallDayDto> getAbandonTime(Query<CallDayDto> query);

    /**
     * 平均通话时间
     * @param query 分页
     * @return page
     */
    Page<CallDayDto> getNormalTime(Query<CallDayDto> query);


    /**
     * 无接听Call数
     * @param query 分页
     * @return page
     */
    Page<CallDayDto> getAbanCount(Query<CallDayDto> query);

    /**
     * 正常Call数
     * @param query 分页
     * @return page
     */
    Page<CallDayDto> getNormalCount(Query<CallDayDto> query);


    /***************************************************************/

    /**
     * 30分钟一组 每一天平均接听速度
     * @param query 分页
     * @return page
     */
    Page<CallMinuteDto> getMinuteAvgAnswerSpeed(Query<CallMinuteDto> query);


    /**
     * 30分钟一组 每一天平均无接听时间
     * @param query 分页
     * @return page
     */
    Page<CallMinuteDto> getMinuteAbandonTime(Query<CallMinuteDto> query);

    /**
     * 30分钟一组 每一天平均通话时间
     * @param query 分页
     * @return page
     */
    Page<CallMinuteDto> getMinuteNormalTime(Query<CallMinuteDto> query);



    /**
     * 30分钟一组 获取某一天无接听call数
     * @param query 分页
     * @return page
     */
    Page<CallMinuteDto> getMinuteAbandCount(Query<CallMinuteDto> query);


    /**
     * 30分钟一组 获取某一天接听call数
     * @param query 分页
     * @return page
     */
    Page<CallMinuteDto> getMinuteNormalCount(Query<CallMinuteDto> query);

    /**
     * 批量删除Call
     * @param callsIds id集合
     * @return boolean
     */
    Boolean batchDeleteCalls(String[] callsIds);

    AcdCalls setStatus(String callId, CallStatus callStatus);

    /**
     * 更新通话记录
     * @param callId 通话ID
     * @param  queueId 队列ID
     * @param  agent 客服
     * @param callStatus 状态
     * @return
     */
    AcdCalls updateCalls(String callId, String queueId, Agent agent, CallStatus callStatus);

    AcdCalls getCallById(String callId);

    /**
     * 获取所有通话记录
     * @param query
     * @return
     */
    Page<AcdCalls> getCallPage(Query<AcdCalls> query);

    /**
     * 当前正在通话中的服务记录
     * 包括 RING CONNECT LINE
     * @param query
     * @return
     */
    Page<AcdCalls> getCallCurrentPage(Query<AcdCalls> query);

    List<AcdCalls> getCallCurrent();

    Page<Object> getCallStatus(Query<Object> query) throws Exception;

    List<String> getStatusFields(Query<Object> query);

    AcdCalls updateCallById(AcdCalls calls);

    /**
     * 将聊天记录保存到缓存中
     * @param chatRecord
     * @return
     * @author hsjiang
     * @date 2019/6/27/027
     **/
    Boolean saveChatRecordToCache(ChatRecord chatRecord);

    /**
     * 更新通话记录的缓存
     * 没有则新建
     */
    void updateCache(AcdCalls calls);

    /**
     * 删除缓存中的通话记录
     * @param callId
     */
    void deleteCache(String callId);

    Integer getCallingLength();

}
