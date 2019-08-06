package com.github.pig.admin.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.github.pig.admin.common.meeting.CacheFunctions;
import com.github.pig.admin.common.meeting.MeetingInfo;
import com.github.pig.admin.common.meeting.MeetingSession;
import com.github.pig.admin.common.meeting.MeetingSessionCache;
import com.github.pig.admin.common.push.Push;
import com.github.pig.common.util.*;
import com.github.pig.common.util.exception.GrgException;
import com.github.pig.admin.common.util.MD5;
import com.github.pig.admin.model.entity.BizMeeting;
import com.github.pig.admin.model.entity.BizMeetingParticipant;
import com.github.pig.admin.model.entity.BizRoom;
import com.github.pig.admin.model.entity.SysUser;
import com.github.pig.admin.service.*;
import com.github.pig.common.constant.CommonConstant;
import com.github.pig.common.constant.MeetingConstant;
import com.github.pig.common.vo.MeetingVo;
import com.github.pig.common.vo.UserVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * <p>
 * 会议服务 前端控制器
 * </p>
 *
 * @author xuesen
 * @since 2018-03-26
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/v1/meeting")
@Api
public class MeetingController {


}
