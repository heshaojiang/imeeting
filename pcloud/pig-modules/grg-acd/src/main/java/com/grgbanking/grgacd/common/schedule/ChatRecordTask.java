package com.grgbanking.grgacd.common.schedule;

import com.alibaba.fastjson.JSONArray;
import com.grgbanking.grgacd.common.Constants;
import com.grgbanking.grgacd.common.RedisUtil;
import com.grgbanking.grgacd.dto.ChatRecord;
import com.grgbanking.grgacd.model.AcdCalls;
import com.grgbanking.grgacd.service.AcdCallsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 聊天记录任务类
 * @auther: hsjiang
 * @date: 2019/6/27/027
 * @version 1.0
 */
@Slf4j
@Component
public class ChatRecordTask {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private AcdCallsService acdCallsService;

    /**
     * 定时将缓存中的数据写入数据库
     * 间隔执行时间
     * @param
     * @return
     * @author hsjiang
     * @date 2019/6/27/027
     **/
    @Scheduled(fixedDelayString = "${acd.chat.writecycle}")
    public void saveToDb() {
        log.info("ChatRecordTask：start write chat date to database from chache-----");
        Map<String, List<ChatRecord>> callIdToListMap = resolve();
        List<AcdCalls> updateList = new ArrayList<>();
        for (Map.Entry<String, List<ChatRecord>> entry : callIdToListMap.entrySet()) {
            List<ChatRecord> records = entry.getValue();
            AcdCalls calls = acdCallsService.getCallById(entry.getKey());
            if(calls == null || records == null || records.size() == 0){
                continue;
            }
            String oldStr = calls.getRecorderChat();
            String newStr = JSONArray.toJSONString(records);
            AcdCalls updateCall = new AcdCalls();
            updateCall.setCallId(entry.getKey());
            updateCall.setRecorderChat(getNewChatStr(oldStr,newStr));
            updateList.add(updateCall);
        }
        if(updateList.size()>0){
//            acdCallsService.updateBatchById(updateList);
            updateList.forEach(acdCallsService::updateCallById);
        }
        log.info("ChatRecordTask：end write chat date to database from chache-----,sum:{}",updateList.size());
    }

    /**
     * 生成新的聊天记录
     * @param oldChatStr 旧的聊天记录
     * @param newChatStr 新增的聊天记录
     * @return
     * @author hsjiang
     * @date 2019/6/27/027
     **/
    private String getNewChatStr(String oldChatStr,String newChatStr){
        String result = "";
        if(StringUtils.isEmpty(oldChatStr)){
            result = newChatStr;
        }
        else{
            StringBuffer oldChatBf = new StringBuffer(oldChatStr);
            oldChatBf.deleteCharAt(oldChatBf.length()-1);
            oldChatBf.append(newChatStr.replaceFirst("\\[",","));
            result = oldChatBf.toString();
        }
        return result;
    }

    /**
     * 解析缓存中的聊天记录数据
     * @param
     * @return
     * @author hsjiang
     * @date 2019/6/27/027
     **/
    private Map<String, List<ChatRecord>> resolve(){
        Map<String, List<ChatRecord>> result = new HashMap<>();
        List<ChatRecord> list = null;
        RedisUtil<ChatRecord> redisUtil = new RedisUtil<ChatRecord>(redisTemplate, Constants.KEY_CHAT_RECORD);
        ChatRecord record = null;
        while ((record = redisUtil.removeFromHead()) != null){
            list = result.get(record.getCallId());
            if(list == null){
                list = new ArrayList<>();
                result.put(record.getCallId(),list);
            }
            record.setCallId(null);//去掉callId,防止冗余
            list.add(record);
        }
        return result;
    }


}
