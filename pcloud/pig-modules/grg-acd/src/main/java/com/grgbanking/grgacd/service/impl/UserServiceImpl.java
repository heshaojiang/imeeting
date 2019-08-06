package com.grgbanking.grgacd.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.github.pig.common.constant.CommonConstant;
import com.github.pig.common.util.Query;
import com.github.pig.common.util.RespCode;
import com.github.pig.common.util.UserUtils;
import com.grgbanking.grgacd.mapper.SysUserMapper;
import com.grgbanking.grgacd.mapper.SysUserRoleMapper;
import com.grgbanking.grgacd.model.SysUser;
import com.grgbanking.grgacd.model.SysUserRole;
import com.grgbanking.grgacd.service.AgentService;
import com.grgbanking.grgacd.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author tjshan
 * @since 2019/5/17 14:13
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements UserService {

    private final SysUserMapper userMapper;
    private static final PasswordEncoder ENCODER = new BCryptPasswordEncoder();
    private final AgentService agentService;
    private final SysUserRoleMapper userRoleMapper;
    @Autowired
    public UserServiceImpl(SysUserMapper userMapper, AgentService agentService, SysUserRoleMapper userRoleMapper) {
        this.userMapper = userMapper;
        this.agentService = agentService;
        this.userRoleMapper = userRoleMapper;
    }

    /**
     *初始化所有的客服到缓存中
     */
    @PostConstruct
    private void initAgent() {

//        List<SysUser> users = userMapper.selectAgentNoPageList(null);

//        for (SysUser user : users) {
//            if (user!=null){
//                agentService.cacheAdd(user);
//            }
//        }
    }
    @Override
    public Page getAgentPage(Query<SysUser> page) {
        List<SysUser> user = userMapper.selectAgentList(page, page.getCondition());
        page.setRecords(user);
        return page;
    }

    @Override
    public List<SysUser> getAgentList(Map<String,Object> params) {
        List<SysUser> users = userMapper.selectAgentNoPageList(params);
        return users;
    }

    @Override
    public SysUser getByName(String username) {
//        return selectOne(new EntityWrapper<SysUser>()
//                .eq("username", username));
        return userMapper.getByName(username);
    }

    @Override
    public SysUser getById(Integer userId) {
        return userMapper.getById(userId);
    }

    public SysUser getCurSysUser() {
        String username = UserUtils.getUser();
        return userMapper.getByName(username);
    }
    public Integer getCustomerIdByUserId() {
        SysUser sysUser = getCurSysUser();
        if (sysUser != null && sysUser.getCustomerId() != null) {
            return sysUser.getCustomerId();
        }
        return null;
    }
    @Override
    @Transactional
    public RespCode addAgent(SysUser userVo) {
        //检查用户信息是否可以插入
        RespCode respCode = checkAgent(userVo,null);
        if (respCode != RespCode.SUCCESS){
            log.warn("userService.checkUser fail");
            return respCode;
        }
        //创建用户
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(userVo, sysUser);
        sysUser.setDelFlag(CommonConstant.STATUS_NORMAL);
        sysUser.setPassword(ENCODER.encode(userVo.getPassword()));
        //插入当前用户的CustomerId
        sysUser.setCustomerId(getCustomerIdByUserId());
        boolean sysUserResult = insert(sysUser);
        userVo.setUserId(sysUser.getUserId());
        if (!sysUserResult) {
            log.warn("userService.insert fail");
            return RespCode.IME_DB_FAIL;
        }
        //创建用户角色
        SysUserRole userRole = new SysUserRole();
        userRole.setUserId(sysUser.getUserId());
        boolean userRoleResult = userRole.insert();
        if (!userRoleResult) {
            log.warn("userRoleResult.insert fail");
            return RespCode.IME_DB_FAIL;
        }

        return RespCode.SUCCESS;
    }

    @Override
    public Boolean updateAgent(SysUser userVo, String username) {
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(userVo, sysUser);
        sysUser.setUpdateTime(new Date());
        boolean flag = this.updateById(sysUser);
        return flag;
    }

    @Override
    public Boolean deleteAgent(Integer agentId) {
        EntityWrapper wrapper=new EntityWrapper();
        Integer count1 = userMapper.deleteById(agentId);
        wrapper.eq("user_id",agentId);
        Integer count2 = userRoleMapper.delete(wrapper);
        return count1==1&&count2==1;
    }

    @Transactional
    @Override
    public Boolean batchDeleteUser(String[] agentIds) {
        List<String> agentIdList = Arrays.asList(agentIds);
        EntityWrapper wrapper=new EntityWrapper();
        wrapper.in("user_id",agentIdList);
        userRoleMapper.delete(wrapper);

        Integer count = userMapper.deleteBatchIds(agentIdList);
        return count==agentIds.length;
    }

    @Override
    public List<String> checkAgentInUse(String[] agentIds) {
        List<String> agentIdList = Arrays.asList(agentIds);
        EntityWrapper<SysUser> wrapper=new EntityWrapper<>();
        wrapper.in("user_id",agentIdList);
        List<SysUser> agentList = userMapper.selectList(wrapper);
        List<String> collect = agentList.stream().map(SysUser::getUsername).collect(Collectors.toList());
        for (String username :collect){
            if (agentService.checkUse(username)){
                log.warn("agent { "+username+" } is in use");
                return null;
            }
        }
        return collect;
    }

    @Override
    public RespCode checkAgent(SysUser userVo, SysUser sysUserOri) {
        if (sysUserOri == null){
            sysUserOri = new SysUser();
        }
        //检查账号是否存在
        String usernameNew = userVo.getUsername();
        log.info("usernameNew: {}, oldname: {}", usernameNew,sysUserOri.getUsername());
        if (StringUtils.isEmpty(usernameNew)) {
            return RespCode.CNSL_NAME_EMPTY;
        } else if (usernameNew.length() > 64 || usernameNew.length() < 4 || !usernameNew.matches("^\\w+$")){
            return RespCode.CNSL_NAME_FORMAT_ERROR;
        } else if (!usernameNew.equals(sysUserOri.getUsername())){
            if (checkNameExist(usernameNew)){
                return RespCode.CNSL_NAME_EXIST;
            }
        }
        //检查手机号是否存在
        String phoneNumNew = userVo.getCall();
        log.info("phoneNumberNew: {}, oldCall: {}", phoneNumNew,sysUserOri.getCall());
        if (StringUtils.isEmpty(phoneNumNew)) {
            return RespCode.CNSL_PHONE_NUM_EMPTY;
        } else if (!phoneNumNew.matches("^((\\+\\d{1,3}(-| )?\\(?\\d\\)?(-| )?\\d{1,5})|(\\(?\\d{2,6}\\)?))(-| )?(\\d{3,4})(-| )?(\\d{4})(( x| ext)\\d{1,5}){0,1}$")){
            //"([0-9\\s\\-]{7,})(?:\\s*(?:#|x\\.?|ext\\.?|extension)\\s*(\\d+))?$"
            return RespCode.CNSL_PHONE_FORMAT_ERROR;
        } else if (!phoneNumNew.equals(sysUserOri.getCall())){
            if (checkPhoneExist(phoneNumNew)){
                return RespCode.CNSL_PHONE_NUM_EXIST;
            }
        }

        //检查昵称是否存在
        String nickname = userVo.getNickname();
        log.info("nicknameNew: {}, oldUsername: {}", nickname,sysUserOri.getNickname());
        if (StringUtils.isEmpty(nickname)) {
            return RespCode.CNSL_NICKNAME_EMPTY;
        } else if (nickname.length() > 64 || nickname.length() < 1 ){
            return RespCode.CNSL_NICKNAME_FORMAT_ERROR;
        }

        return RespCode.SUCCESS;
    }



    public Boolean checkNameExist(String username) {
        Boolean exist = false;
        EntityWrapper<SysUser> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("username",username);
        if (userMapper.selectCount(entityWrapper)!=0)
        {
            exist = true;
        }
        return exist;
    }



    private Boolean checkPhoneExist(String phoneNumber) {
        Boolean exist = false;
        EntityWrapper<SysUser> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("telephone",phoneNumber);
        if (userMapper.selectCount(entityWrapper)!=0){
            exist = true;
        }
        return exist;
    }
}
