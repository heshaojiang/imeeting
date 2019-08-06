package com.github.pig.admin.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.github.pig.admin.common.meeting.CacheFunctions;
import com.github.pig.admin.mapper.BizRoomMapper;
import com.github.pig.admin.mapper.SysUserMapper;
import com.github.pig.admin.model.dto.UserDto;
import com.github.pig.admin.model.dto.UserInfo;
import com.github.pig.admin.model.entity.BizRoom;
import com.github.pig.admin.model.entity.SysDeptRelation;
import com.github.pig.admin.model.entity.SysUser;
import com.github.pig.admin.model.entity.SysUserRole;
import com.github.pig.admin.service.*;
import com.github.pig.common.bean.interceptor.DataScope;
import com.github.pig.common.constant.CommonConstant;
import com.github.pig.common.constant.MeetingConstant;
import com.github.pig.common.constant.SecurityConstants;
import com.github.pig.common.util.Query;
import com.github.pig.common.util.R;
import com.github.pig.common.util.RespCode;
import com.github.pig.common.util.UserUtils;
import com.github.pig.common.vo.SysRole;
import com.github.pig.common.vo.UserVo;
import com.xiaoleilu.hutool.collection.CollectionUtil;
import com.xiaoleilu.hutool.util.RandomUtil;
import com.xiaoleilu.hutool.util.StrUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author lengleng
 * @date 2017/10/31
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private static final Logger log = LoggerFactory.getLogger(SysUserServiceImpl.class);

    //显示创建密码解析器
    private static final PasswordEncoder ENCODER = new BCryptPasswordEncoder();

    @Autowired
    private SysMenuService sysMenuService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SysUserMapper sysUserMapper;

   /* @Autowired
    private RabbitTemplate rabbitTemplate;*/

    @Autowired
    private SysUserRoleService sysUserRoleService;

    @Autowired
    private SysDeptRelationService sysDeptRelationService;

    @Autowired
    private BizRoomService bizRoomService;

    @Autowired
    private BizRoomMapper bizRoomMapper;

    @Autowired
    private CacheFunctions cacheFunctions;
    @Override
    public UserInfo findUserInfo(UserVo userVo) {
        SysUser condition = new SysUser();
        condition.setUsername(userVo.getUsername());
        SysUser sysUser = this.selectOne(new EntityWrapper<>(condition));

        UserInfo userInfo = new UserInfo();
        userInfo.setSysUser(sysUser);
        //设置角色列表
        List<SysRole> roleList = userVo.getRoleList();
        List<String> roleNames = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(roleList)) {
            for (SysRole sysRole : roleList) {
                if (!StrUtil.equals(SecurityConstants.BASE_ROLE, sysRole.getRoleName())) {
                    roleNames.add(sysRole.getRoleName());
                }
            }
        }
        String[] roles = roleNames.toArray(new String[roleNames.size()]);
        userInfo.setRoles(roles);
        //设置权限列表（menu.permission）
        String[] permissions = sysMenuService.findPermission(roles);
        userInfo.setPermissions(permissions);
        return userInfo;
    }

    @Override
    @Cacheable(value = "user_details", key = "#username")
    public UserVo findUserByUsername(String username) {
        return sysUserMapper.selectUserVoByUsername(username);
    }

    /**
     * 通过手机号查询用户信息
     *
     * @param mobile 手机号
     * @return 用户信息
     */
    @Override
    @Cacheable(value = "user_details_mobile", key = "#mobile")
    public UserVo findUserByMobile(String mobile) {
        return sysUserMapper.selectUserVoByMobile(mobile);
    }

    /**
     * 通过openId查询用户
     *
     * @param openId openId
     * @return 用户信息
     */
    @Override
    @Cacheable(value = "user_details_openid", key = "#openId")
    public UserVo findUserByOpenId(String openId) {
        return sysUserMapper.selectUserVoByOpenId(openId);
    }

    @Override
    public Page selectWithRolePage(Query query) {
        DataScope dataScope = new DataScope();
        dataScope.setScopeName("deptId");
        dataScope.setIsOnly(true);
        dataScope.setDeptIds(getChildDepts());
        dataScope.putAll(query.getCondition());
        query.setRecords(sysUserMapper.selectUserVoPageDataScope(query, dataScope));
        return query;
    }

    @Override
    public Page selectUserVoPageForAdmin(Query query) {
        DataScope dataScope = new DataScope();
        dataScope.setScopeName("deptId");
        dataScope.setIsOnly(true);
        dataScope.setDeptIds(getChildDepts());
        dataScope.putAll(query.getCondition());
        query.setRecords(sysUserMapper.selectUserVoPageForAdmin(query, dataScope));
        return query;
    }


    /**
     * 通过ID查询用户信息
     *
     * @param id 用户ID
     * @return 用户信息
     */
    @Override
    public UserVo selectUserVoById(Integer id) {
        return sysUserMapper.selectUserVoById(id);
    }

    @Override
    public List<UserVo> selectUserVoByCustomerId(Integer customerId) { return sysUserMapper.selectUserVoByCustomerId(customerId); }

    /**
     * @author fmsheng
     * @param
     * @description 通过用户名获取会议室
     * @date 2018/11/28 14:50
     */
    @Override
    public UserVo selectUserVoAndRoomResult(String username) {
        return sysUserMapper.selectUserVoAndRoomResult(username);
    }


    /**
     * @author fmsheng
     * @param
     * @description 批量删除用户
     * @date 2018/10/23 11:06
     */
    @Override
    @Transactional
    public Boolean batchDeleteUser(String[] userIds) {

//        List<SysRoleMenu> roleMenuList = new ArrayList<>();
//        List<String> userList = Arrays.asList(userIds.split(","));
//        if (CollUtil.isEmpty(userIds)) {
        if(userIds.length==0||userIds.equals("")||userIds==null){
            return Boolean.TRUE;
        }

        try
        {
            for (String  userId: userIds) {

                //会议室userId设置为空
                List<BizRoom> bizRoomList = bizRoomService.selectList(new EntityWrapper<BizRoom>().eq("user_id", userId));
                for (BizRoom bizRoom : bizRoomList) {
                    bizRoomMapper.updateUserIdById(bizRoom.getRoomId());
                }

                //直接删除(清空)用户数据
                SysUser sysUser = selectById(userId);
                sysUser.setDelFlag(CommonConstant.STATUS_DEL);
                deleteUserById(sysUser);
                //updateById(sysUser);
            }
        }catch (Exception e)
        {
            e.printStackTrace();
            log.info("删除用户失败"+e.getMessage());
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }

    /**
     * 保存用户验证码，和randomStr绑定
     *
     * @param randomStr 客户端生成
     * @param imageCode 验证码信息
     */
    @Override
    public void saveImageCode(String randomStr, String imageCode) {
        redisTemplate.opsForValue().set(SecurityConstants.DEFAULT_CODE_KEY + randomStr, imageCode, SecurityConstants.DEFAULT_IMAGE_EXPIRE, TimeUnit.SECONDS);
    }

    /**
     * 发送验证码
     * <p>
     * 1. 先去redis 查询是否 60S内已经发送
     * 2. 未发送： 判断手机号是否存 ? false :产生4位数字  手机号-验证码
     * 3. 发往消息中心-》发送信息
     * 4. 保存redis
     *
     * @param mobile 手机号
     * @return true、false
     */
    @Override
    public R<Boolean> sendSmsCode(String mobile) {
        Object tempCode = redisTemplate.opsForValue().get(SecurityConstants.DEFAULT_CODE_KEY + mobile);
        if (tempCode != null) {
            log.error("用户:{}验证码未失效{}", mobile, tempCode);
            return new R<Boolean>(false, "验证码未失效，请失效后再次申请");
        }

        SysUser params = new SysUser();
        params.setIntroduction(mobile);
        List<SysUser> userList = this.selectList(new EntityWrapper<>(params));

        if (CollectionUtil.isEmpty(userList)) {
            log.error("根据用户手机号{}查询用户为空", mobile);
            return new R<Boolean>(false, "手机号不存在");
        }

        String code = RandomUtil.randomNumbers(4);
        log.info("短信发送请求消息中心 -> 手机号:{} -> 验证码：{}", mobile, code);
        //rabbitTemplate.convertAndSend(MqQueueConstant.MOBILE_CODE_QUEUE, new MobileMsgTemplate(mobile, code, CommonConstant.ALIYUN_SMS));
        redisTemplate.opsForValue().set(SecurityConstants.DEFAULT_CODE_KEY + mobile, code, SecurityConstants.DEFAULT_IMAGE_EXPIRE, TimeUnit.SECONDS);
        return new R<>(true);
    }

    /**
     * 删除用户
     *
     * @param sysUser 用户
     * @return Boolean
     */
    @Override
    public Boolean deleteUserById(SysUser sysUser) {
        sysUserRoleService.deleteByUserId(sysUser.getUserId());
        this.deleteById(sysUser.getUserId());
        cacheFunctions.deleteUserDetails(sysUser.getUsername());
        return Boolean.TRUE;
    }

    @Override
    @CacheEvict(value = "user_details", key = "#username")
    public R<Boolean> updateUserInfo(UserDto userDto, String username) {
        UserVo userVo = this.findUserByUsername(username);

        SysUser sysUser = new SysUser();
        if (ENCODER.matches(userDto.getPassword(), userVo.getPassword())) {
            sysUser.setPassword(ENCODER.encode(userDto.getNewpassword1()));
        }else {
            return new R<>(RespCode.CNSL_OLD_PASSWORD_WRONG);
        }

        sysUser.setUserId(userVo.getUserId());
        sysUser.setAvatar(userDto.getAvatar());
        if (this.updateById(sysUser)) {
            return new R<>(RespCode.SUCCESS);
        } else {
            return new R<>(RespCode.IME_DB_FAIL);

        }
    }

    @Override
    @CacheEvict(value = "user_details", key = "#username")
    public Boolean passwordReset(String username) {
        SysUser sysUser = new SysUser();
        sysUser.setPassword(ENCODER.encode(MeetingConstant.USER_DEF_PWD));
        return update(sysUser, new EntityWrapper<SysUser>().eq("username", username));
    }

    @Override
    @CacheEvict(value = "user_details", key = "#username")
    public Boolean updateUser(UserDto userDto, String username) {
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(userDto, sysUser);
        sysUser.setPassword(null);
        this.updateById(sysUser);

        SysUserRole condition = new SysUserRole();
        condition.setUserId(userDto.getUserId());
        SysUserRole sysUserRole = sysUserRoleService.selectOne(new EntityWrapper<>(condition));
        sysUserRole.setRoleId(userDto.getRoleId());
        return sysUserRoleService.update(sysUserRole, new EntityWrapper<>(condition));
    }

    /**
     * 获取当前用户的子部门信息
     *
     * @return 子部门列表
     */
    private List<Integer> getChildDepts() {
        //获取当前用户的部门
        String username = UserUtils.getUser();
        UserVo userVo = findUserByUsername(username);
        Integer deptId = userVo.getDeptId();

        //获取当前部门的子部门
        SysDeptRelation deptRelation = new SysDeptRelation();
        deptRelation.setAncestor(deptId);
        List<SysDeptRelation> deptRelationList = sysDeptRelationService.selectList(new EntityWrapper<>(deptRelation));
        List<Integer> deptIds = new ArrayList<>();
        for (SysDeptRelation sysDeptRelation : deptRelationList) {
            deptIds.add(sysDeptRelation.getDescendant());
        }
        return deptIds;
    }

    @Override
    public SysUser getCurSysUser() {
        String username = UserUtils.getUser();
        return sysUserMapper.selectSysUserByUsername(username);
    }

    @Override
    public Integer getCustomerIdByUserId() {
        SysUser sysUser = getCurSysUser();
        if (sysUser != null && sysUser.getCustomerId() != null) {
            return sysUser.getCustomerId();
        }
        return null;
    }
    @Override
    public Integer getUserId() {
        SysUser sysUser = getCurSysUser();
        if (sysUser != null && sysUser.getUserId() != null) {
            return sysUser.getUserId();
        }
        return null;
    }
    @Override
    public Boolean checkNameExist(String name) {
        Boolean exist = false;
        if (sysUserMapper.selectUserVoByUsername(name) != null)
        {
            exist = true;
        }
        return exist;
    }
    @Override
    public Boolean checkNickaameExist(String name){
        Boolean exist = false;
        if (selectOne(new EntityWrapper<SysUser>().eq("nickname", name)) != null){
            exist = true;
        }
        return exist;
    }

    @Override
    public Boolean checkPhonenumExist(String phonenum) {
        Boolean exist = false;
        if (selectOne(new EntityWrapper<SysUser>().eq("telephone", phonenum)) != null){
            exist = true;
        }
        return exist;
    }

    @Override
    public RespCode checkUser(UserDto userDtoNew, SysUser sysUserOri) {
        if (sysUserOri == null){
            sysUserOri = new SysUser();
        }
        //检查账号是否存在
        String usernameNew = userDtoNew.getUsername();
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
        String phoneNumNew = userDtoNew.getCall();
        log.info("phoneNumNew: {}, oldcall: {}", phoneNumNew,sysUserOri.getCall());
        if (StringUtils.isEmpty(phoneNumNew)) {
            return RespCode.CNSL_PHONE_NUM_EMPTY;
        } else if (!phoneNumNew.matches("^((\\+\\d{1,3}(-| )?\\(?\\d\\)?(-| )?\\d{1,5})|(\\(?\\d{2,6}\\)?))(-| )?(\\d{3,4})(-| )?(\\d{4})(( x| ext)\\d{1,5}){0,1}$")){
            //"([0-9\\s\\-]{7,})(?:\\s*(?:#|x\\.?|ext\\.?|extension)\\s*(\\d+))?$"
            return RespCode.CNSL_PHONE_FORMAT_ERROR;
        } else if (!phoneNumNew.equals(sysUserOri.getCall())){
            if (checkPhonenumExist(phoneNumNew)){
                return RespCode.CNSL_PHONE_NUM_EXIST;
            }
        }

        //检查昵称是否存在
        String nickname = userDtoNew.getNickname();
        log.info("nicknameNew: {}, oldname: {}", nickname,sysUserOri.getNickname());
        if (StringUtils.isEmpty(nickname)) {
            return RespCode.CNSL_NICKNAME_EMPTY;
        } else if (nickname.length() > 64 || nickname.length() < 1 ){
            return RespCode.CNSL_NICKNAME_FORMAT_ERROR;
        } else if (!nickname.equals(sysUserOri.getNickname())){
//            if (checkNickaameExist(nickname)){
//                return RespCode.CNSL_NICKNAME_EXIST;
//            }
        }

//        //检查密码格式
//        String passwordNew = userDtoNew.getPassword();
//        log.info("passwordNew len: {}", passwordNew.length());
//        if (StringUtils.isEmpty(passwordNew)) {
//            return RespCode.CNSL_PASSWORD_EMPTY;
//        } else if (passwordNew.length() > 32 || passwordNew.length() < 6/* || !passwordNew.matches("^\\w+$")*/){
//            return RespCode.CNSL_PASSWORD_FORMAT_ERROR;
//        }

        return RespCode.SUCCESS;
    }

    @Override
    public RespCode addUser(UserDto userDto) {
        //检查用户信息是否可以插入
        RespCode respCode = checkUser(userDto,null);
        if (respCode != RespCode.SUCCESS){
            log.warn("userService.checkUser fail");
            return respCode;
        }
        //创建用户
        SysUser sysUser = new SysUser();

        BeanUtils.copyProperties(userDto, sysUser);

        sysUser.setDelFlag(CommonConstant.STATUS_NORMAL);

        sysUser.setPassword(ENCODER.encode(userDto.getPassword()));
        Integer customerId = getCustomerIdByUserId();
        if (!UserUtils.isAdminUserWithCstmId(customerId)){
            //非管理员只能给自己所属客户添加用户
            sysUser.setCustomerId(customerId);
        }
        boolean sysUserResult = insert(sysUser);

        if (!sysUserResult) {
            log.warn("userService.insert fail");
            return RespCode.IME_DB_FAIL;
        }
        //给每个用户创建一个会议室
        boolean bizRoomResult = bizRoomService.newRoom(null,sysUser);
        if (!bizRoomResult) {
            return RespCode.CNSL_ADD_ROOM_FAIL;
        }
        if (userDto.getRoleId() == null){
            userDto.setRoleId(MeetingConstant.USER_ROLE_User_ID);
        }
        //创建用户角色
        SysUserRole userRole = new SysUserRole();
        userRole.setUserId(sysUser.getUserId());
        userRole.setRoleId(userDto.getRoleId());
        boolean userRoleResult = userRole.insert();
        if (!userRoleResult) {
            log.warn("userRoleResult.insert fail");
            return RespCode.IME_DB_FAIL;
        }

        return RespCode.SUCCESS;
    }
}
