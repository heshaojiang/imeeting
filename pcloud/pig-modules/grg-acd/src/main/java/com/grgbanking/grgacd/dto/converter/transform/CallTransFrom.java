package com.grgbanking.grgacd.dto.converter.transform;

import com.grgbanking.grgacd.common.CallStatus;
import com.grgbanking.grgacd.common.SpringContextUtils;
import com.grgbanking.grgacd.mapper.AcdQueueMapper;
import com.grgbanking.grgacd.model.AcdQueue;

/**
 * @author tjshan
 * @since 2019/6/6 16:07
 */
public class CallTransFrom {

    private static final String SUCCESS="success";
    private static final String FAIL="fail";

    public String CallStatusToStatus(String callStatus){
        if (callStatus.equals(CallStatus.HANGUP.name())){
            return SUCCESS;
        }else{
            return FAIL;
        }
    }

    public AcdQueue getQueue(String queueId){
        AcdQueueMapper queueMapper = SpringContextUtils.getBean(AcdQueueMapper.class);
        AcdQueue acdQueue = queueMapper.selectById(queueId);
        return acdQueue;
    }
}
