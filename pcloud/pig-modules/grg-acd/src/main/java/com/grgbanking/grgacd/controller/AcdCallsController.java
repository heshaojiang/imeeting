package com.grgbanking.grgacd.controller;


import com.baomidou.mybatisplus.plugins.Page;
import com.github.pig.common.util.Query;
import com.github.pig.common.util.R;
import com.github.pig.common.util.RespCode;
import com.github.pig.common.util.exception.GrgException;
import com.grgbanking.grgacd.common.CallStatus;
import com.grgbanking.grgacd.dto.CallDayDto;
import com.grgbanking.grgacd.dto.CallDto;
import com.grgbanking.grgacd.dto.CallMinuteDto;
import com.grgbanking.grgacd.dto.ChatRecord;
import com.grgbanking.grgacd.dto.converter.CallConverter;
import com.grgbanking.grgacd.model.AcdCalls;
import com.grgbanking.grgacd.service.AcdCallsService;
import grgfileserver.utils.MyFileUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 通话记录表 前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2019-05-05
 */
@Slf4j
@RestController
@RequestMapping("/calls")
@Api
public class AcdCallsController {

    private final AcdCallsService acdCallsService;

    @Autowired
    public AcdCallsController(AcdCallsService acdCallsService) {
        this.acdCallsService = acdCallsService;
    }

    /**
     * 添加通话记录
     *
     * @param acdCalls 通话记录
     * @return R
     */
    @ApiOperation(value = "添加通话记录")
    @PostMapping("/addCall")
    public R<Boolean> addCall(@RequestBody AcdCalls acdCalls) {
        acdCallsService.addCall(acdCalls);
        return new R<>(Boolean.TRUE);
    }
    /**
     * **********************************************
     * 日通话统计<Summary Daily>
     * **********************************************
     */


    /**
     * <p>
     * 平均接听速度
     * 1.	接听速度=answer_time-makecall_time
     * 2.	平均接听速度=SUM（当天接听通话的answer_time-makecall_time）
     * </p>
     *
     * @return R
     */
    @GetMapping("/day/avgSpeed")
    public R<Page> getAvgSpeed(@RequestParam Map<String, Object> params) {
        Page<CallDayDto> page = acdCallsService.getAvgAnswerSpeed(new Query<>(params));
        return new R<>(Boolean.TRUE, page);
    }


    /**
     * <p>
     * 平均无接听时间
     * 1.无接听=通话状态为呼叫超时
     * 2.平均无接听时间=SUM（当天无接听通话记录的hangup_time-makecall_time）\ 无接听通话数
     * </p>
     *
     * @return R
     */
    @GetMapping("/day/avgAbanTime")
    public R<Object> getAvgAbanTime(@RequestParam Map<String, Object> params) {
        Page<CallDayDto> page = acdCallsService.getAbandonTime(new Query<>(params));
        return new R<>(Boolean.TRUE, page);
    }

    /**
     * <p>
     * 无接听Call数
     * 1. 无接听=通话状态为呼叫超时
     * 2. 无接听Call数=SUM（当天无接听通话数）
     * </p>
     *
     * @return R
     */
    @GetMapping("/day/abanCalls")
    public R<Object> getAbanCalls(@RequestParam Map<String, Object> params) {
        Page<CallDayDto> page = acdCallsService.getAbanCount(new Query<>(params));
        return new R<>(Boolean.TRUE, page);
    }


    /**
     * <p>
     * 正常通话数
     * 1. 正常通话=通话状态为正常挂断
     * 2. 正常通话数=SUM（当天正常通话数）
     * </p>
     *
     * @return R
     */
    @GetMapping("/day/norCalls")
    public R<Object> getNormalCalls(@RequestParam Map<String, Object> params) {
        Page<CallDayDto> page = acdCallsService.getNormalCount(new Query<>(params));
        return new R<>(Boolean.TRUE, page);
    }

    /**
     * <p>
     * 平均通话时间
     * 1. 正常通话=通话状态为正常挂断
     * 2. 平均正常通话时间=SUM（当天正常通话的hangup_time-answer_time）/（当天正常通话数）
     * </p>
     *
     * @return R
     */
    @GetMapping("/day/abgAcdTime")
    public R<Object> getAvgAcdTime(@RequestParam Map<String, Object> params) {
        Page<CallDayDto> page = acdCallsService.getNormalTime(new Query<>(params));
        return new R<>(Boolean.TRUE, page);
    }


    /**
     * **********************************************
     * 时段通话统计<Summary Interval>
     * **********************************************
     */


    /**
     * <p>
     * 平均接听速度
     * 1.	接听速度=answer_time-makecall_time
     * 2.	平均接听速度=SUM（当天接听通话的answer_time-makecall_time）
     * </p>
     *
     * @return R
     */
    @GetMapping("/minute/avgSpeed")
    public R<Page> getMinuteAvgSpeed(@RequestParam Map<String, Object> params) {
        Page<CallMinuteDto> page = acdCallsService.getMinuteAvgAnswerSpeed(new Query<>(params));
        return new R<>(Boolean.TRUE, page);
    }


    /**
     * <p>
     * 平均无接听时间
     * 1.无接听=通话状态为呼叫超时
     * 2.平均无接听时间=SUM（当天无接听通话记录的hangup_time-makecall_time）\ 无接听通话数
     * </p>
     *
     * @return R
     */
    @GetMapping("/minute/avgAbanTime")
    public R<Object> getMinuteAvgAbanTime(@RequestParam Map<String, Object> params) {
        Page<CallMinuteDto> page = acdCallsService.getMinuteAbandonTime(new Query<>(params));
        return new R<>(Boolean.TRUE, page);
    }


    /**
     * <p>
     * 无接听Call数
     * 1. 无接听=通话状态为呼叫超时
     * 2. 无接听Call数=SUM（当天无接听通话数）
     * </p>
     *
     * @return R
     */
    @GetMapping("/minute/abanCalls")
    public R<Object> getMinuteAbanCalls(@RequestParam Map<String, Object> params) {
        Page<CallMinuteDto> page = acdCallsService.getMinuteAbandCount(new Query<>(params));
        return new R<>(Boolean.TRUE, page);
    }


    /**
     * <p>
     * 正常通话数
     * 1. 正常通话=通话状态为正常挂断
     * 2. 正常通话数=SUM（当天正常通话数）
     * </p>
     *
     * @return R
     */
    @GetMapping("/minute/norCalls")
    public R<Object> getMinuteNormalCalls(@RequestParam Map<String, Object> params) {
        Page<CallMinuteDto> page = acdCallsService.getMinuteNormalCount(new Query<>(params));
        return new R<>(Boolean.TRUE, page);
    }


    /**
     * <p>
     * 平均通话时间
     * 1. 正常通话=通话状态为正常挂断
     * 2. 平均正常通话时间=SUM（当天正常通话的hangup_time-answer_time）/（当天正常通话数）
     * </p>
     *
     * @return R
     */
    @GetMapping("/minute/abgAcdTime")
    public R<Object> getMinuteAvgAcdTime(@RequestParam Map<String, Object> params) {
        Page<CallMinuteDto> page = acdCallsService.getMinuteNormalTime(new Query<>(params));
        return new R<>(Boolean.TRUE, page);
    }

    /**
     * **********************************************
     * 基本数据统计  <Summary Interval>
     * **********************************************
     */

    /**
     * 通话列表
     * @param params
     * @return
     */
    @GetMapping("/page")
    public R<Object> getCallsPage(@RequestParam Map<String, Object> params){
        Page<CallDto> page = CallConverter.INSTANCE.map(acdCallsService.getCallPage(new Query<>(params)));
        return new R<>(Boolean.TRUE,page);
    }

    /**
     * 正在通话中的通话列表
     * @return
     */
    @GetMapping("/current/page")
    public R<Object> getCallsStatusPage(@RequestParam Map<String, Object> params){
        Page<CallDto> page = CallConverter.INSTANCE.map(acdCallsService.getCallCurrentPage(new Query<>(params)));
        return new R<>(Boolean.TRUE,page);
    }

    /**
     * 通话详情
     * @param callId 通话id
     * @return
     */
    @GetMapping("/info/{callId}")
    public R<Object> getCallInfo(@PathVariable  String callId){
        AcdCalls acdCalls = acdCallsService.getCallById(callId);
        return new R<>(Boolean.TRUE,acdCalls);
    }

    /**
     * 批量删除通话
     * @param callsIds 通话id
     * @return
     */
    @DeleteMapping("/{id}")
    public R<Boolean> callDelete(@PathVariable("id") String[] callsIds) {
        return new R<>(acdCallsService.batchDeleteCalls(callsIds));
    }

    /**
     * 获取所有的通话状态列表
     * @return
     */
    @GetMapping("/status")
    public R<Object> getCallStatus(){
        List<String> callStatusList=new ArrayList<>();
        for (CallStatus status :CallStatus.values()){
            callStatusList.add(status.name());
        }
        return new R<>(Boolean.TRUE,callStatusList);
    }

    /**
     * 将文件流输出到页面
     * @param callId 服务记录id
     * @param type 文件类型，audio:音频，video:视频
     * @param request 请求对象
     * @param response 请求返回
     * @return
     * @author hsjiang
     * @date 2019/6/25/025
     **/
    @GetMapping("/getFile")
    public void getFile(String callId, String type,HttpServletRequest request, HttpServletResponse response){
        OutputStream out = null;
        String fileUrl = "";
        try{
            if(StringUtils.isEmpty(callId)){
                return;
            }
            AcdCalls entity = acdCallsService.getCallById(callId);
            if("audio".equals(type)){
                fileUrl = entity.getRecorderAudioFilepath();
                response.setHeader("Content-Type", "audio/mpeg");
            }
            else if("video".equals(type)){
                fileUrl = entity.getRecorderVideoFilepath();
                response.setHeader("Content-Type", "video/mp4");
            }
            else if("image".equals(type)){
                fileUrl = entity.getRecorderPreviewFilepath();
                response.setHeader("Content-Type", "image/gif");
            }
            if(StringUtils.isEmpty(fileUrl)){
                return;
            }

            //设置播放进度
            String range = request.getHeader("Range");
            if(null != range){
                String[] rs = range.split("\\=");
                range = rs[1].split("\\-")[0];
            }
            else{
                range = "0";
            }
            byte[] fileBytes = MyFileUtils.fileToByteArray(fileUrl);
            long fileLength = fileBytes.length;
            long remainLength = fileLength - Long.parseLong(range);

            response.addHeader("Accept-Ranges", "bytes");
            response.addHeader("Content-Length", fileLength + "");
            response.addHeader("Content-Range", "bytes " + range + "-" + remainLength + "/" + fileLength);

            out = response.getOutputStream();
            out.write(fileBytes);
            out.flush();
        }catch (IOException e){
            log.error("输入流获取错误:[{}]", e);
            throw new GrgException(RespCode.FAIL);
        }finally {
            try {
                if(out != null){
                    out.close();
                }
            }catch (IOException e){
                log.error("输出流关闭错误:[{}]", e);
                throw new GrgException(RespCode.FAIL);
            }

        }
    }

    /**
     * 将聊天记录保存
     * @param chatRecord 聊天记录数据
     * @return
     * @author hsjiang
     * @date 2019/6/27/027
     **/
    @PostMapping("/saveChatRecord")
    public R saveChatRecord(@RequestBody ChatRecord chatRecord){
        boolean result = acdCallsService.saveChatRecordToCache(chatRecord);
        return new R(true,result);
    }

}

