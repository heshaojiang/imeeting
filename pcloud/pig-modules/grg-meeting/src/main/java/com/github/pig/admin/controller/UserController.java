package com.github.pig.admin.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;
import com.github.pig.common.util.exception.GrgException;
import com.github.pig.admin.mapper.SysDeptMapper;
import com.github.pig.admin.mapper.SysRoleMapper;
import com.github.pig.admin.model.dto.UserDto;
import com.github.pig.admin.model.dto.UserInfo;
import com.github.pig.admin.model.entity.SysUser;
import com.github.pig.admin.model.entity.SysUserRole;
import com.github.pig.admin.service.BizRoomService;
import com.github.pig.admin.service.SysRoleService;
import com.github.pig.admin.service.SysUserService;
import com.github.pig.admin.service.impl.ImportServiceImpl;
import com.github.pig.common.constant.CommonConstant;
import com.github.pig.common.constant.MeetingConstant;
import com.github.pig.common.util.Query;
import com.github.pig.common.util.R;
import com.github.pig.common.util.RespCode;
import com.github.pig.common.util.UserUtils;
import com.github.pig.common.util.exception.UnloginException;
import com.github.pig.common.vo.UserVo;
import com.github.pig.common.web.BaseController;
import com.xiaoleilu.hutool.io.FileUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lengleng
 * @date 2017/10/28
 */
@RestController
@RequestMapping("/user")
@Api
public class UserController extends BaseController {

//    @Autowired
//    private FastFileStorageClient fastFileStorageClient;

    @Autowired
    private SysUserService userService;

//    @Autowired
//    private FdfsPropertiesConfig fdfsPropertiesConfig;

    @Autowired
    private ImportServiceImpl importServiceImpl;

    @Autowired
    private SysDeptMapper sysDeptMapper;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private BizRoomService bizRoomService;

    private static final PasswordEncoder ENCODER = new BCryptPasswordEncoder();

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    /**
     * 获取当前用户信息（角色、权限）
     * 并且异步初始化用户部门信息
     *
     * @param userVo 当前用户信息
     * @return 用户名
     */
    @ApiOperation(value = "获取当前用户信息（角色、权限）")
    @GetMapping("/info")
    public R user(UserVo userVo) {
        //UserInfo userInfo = userService.findUserInfo(userVo);
        //return new R<>(Boolean.TRUE, userInfo);
        return new R<>(Boolean.TRUE,userService.findUserByUsername(userVo.getUsername()).maskPassword());
    }

    @ApiOperation(value = "获取用户信息")
    @GetMapping("/getUserInfo")
    public R<UserInfo> getUserInfo() {
        String username = UserUtils.getUser();
        if (username.isEmpty()) {
            throw new UnloginException("验证过期，请重新登录！");
        }
        UserVo userVo = new UserVo();
        userVo.setUsername(username);
        UserInfo userInfo = userService.findUserInfo(userVo);
        return new R<>(Boolean.TRUE, userInfo);
    }

    /**
     * 通过ID查询当前用户信息
     *
     * @param id ID
     * @return 用户信息
     */
    @ApiOperation(value = "通过ID查询当前用户信息")
    @GetMapping("/{id}")
    public R<UserVo> user(@PathVariable Integer id) {
        return new R<>(Boolean.TRUE,userService.selectUserVoById(id).maskPassword());
    }

    /**
     * 通过customerId查询当前用户信息
     *
     * @param customerId
     * @return 用户信息
     */
    @ApiOperation(value = "通过customerId查询当前用户信息")
    @GetMapping("/customerChange/{customerId}")
    public R<List<UserVo>> customerChangeObtainUser(@PathVariable Integer customerId) {
//        String currentCustomerId = userService.getCustomerIdByUserId().getData();
//        if("0".equals(currentCustomerId)){
//           customerId = 0;
//        }
        return new R<>(Boolean.TRUE,userService.selectUserVoByCustomerId(customerId));
    }

    /**
     * @author fmsheng
     * @param userDto
     * @description
     * 1.admin用户添加
     * 2.原有基础信息上添加客户ID
     * 3.原有基础信息上自动生成默认会议号
     * @return success/false
     * @date 2018/11/6 12:00
     */
    @Transactional
    @PostMapping
    public R<Boolean> user(@RequestBody UserDto userDto) {
        //检查用户信息是否可以插入
        RespCode respCode = userService.addUser(userDto);
        return new R<>(respCode);
    }


    /**
     * 1.imeeting_dashboard用户添加
     * 2.因暂未引入权限控制，此处定好只允许用户管理员可以添加用户
     *
     * @param userDto 用户信息
     * @return success/false
     */
    @PostMapping(value = "/imeetingUser")
    public R<Boolean> imeetingUser(@RequestBody UserDto userDto) {

        String username = UserUtils.getUser();

        UserVo userVo = userService.findUserByUsername(username);

        if ((!StringUtils.contains(userVo.getRoleList().toString(), "role_manager"))) {
            return new R<>(Boolean.FALSE, RespCode.IME_UNAUTHORIZED);
        }
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(userDto, sysUser);
        sysUser.setDelFlag(CommonConstant.STATUS_NORMAL);
        sysUser.setPassword(ENCODER.encode(userDto.getPassword()));
        userService.insert(sysUser);
        SysUserRole userRole = new SysUserRole();
        userRole.setUserId(sysUser.getUserId());
        userRole.setRoleId(userDto.getRoleId());
        return new R<>(userRole.insert());
    }

    /**
     * @param userDtoList
     * @author fmsheng
     * @description 批量添加用户
     * @date 2018/10/22 10:57
     */
    @ApiOperation(value = "批量添加用户")
    @PostMapping("/batchAddUser")
    public R<Boolean> batchAddUser(@RequestBody List<UserDto> userDtoList) {
        try {
            for (UserDto userDto : userDtoList) {

                SysUser sysUser = new SysUser();
                BeanUtils.copyProperties(userDto, sysUser);
                sysUser.setDelFlag(CommonConstant.STATUS_NORMAL);
                sysUser.setPassword(ENCODER.encode(userDto.getPassword()));
                userService.insert(sysUser);
                SysUserRole userRole = new SysUserRole();
                userRole.setUserId(sysUser.getUserId());
                userRole.setRoleId(userDto.getRoleId());
                userRole.insert();
                // System.out.println(1 / 0);
            }
        } catch (Exception e) {
            logger.info("batch Add User fail" + e.getMessage());
            return new R<>(e);
        }
        return new R<>(true);
    }

    /**
     * @param userIds
     * @author fmsheng
     * @description 删除用户
     * @date 2018/10/22 10:57
     */
    @ApiOperation(value = "删除用户")
    @DeleteMapping("/{id}")
    public R<Boolean> userDel(@PathVariable("id") String[] userIds) {
        return new R<>(userService.batchDeleteUser(userIds));
    }

    /**
     * 更新用户信息
     *
     * @param userDto 用户信息
     * @return R
     */
    @ApiOperation(value = "更新用户信息")
    @PutMapping
    public R<Boolean> userUpdate(@RequestBody UserDto userDto) {

        SysUser user = userService.selectById(userDto.getUserId());
        if (user == null) {
            return new R<>(Boolean.FALSE, RespCode.CNSL_OBJ_NOT_FOUND);
        }
        //检查用户信息是否可以修改
        RespCode respCode = userService.checkUser(userDto,user);
        if (respCode != RespCode.SUCCESS){
            logger.warn("userService.checkUser fail");
            return new R<>(Boolean.FALSE, respCode);
        }
        boolean updateResult = userService.updateUser(userDto, user.getUsername());
        if (!updateResult) {
            return new R<>(Boolean.FALSE, RespCode.IME_DB_FAIL);
        }

        return new R<>(Boolean.TRUE, RespCode.SUCCESS);
    }

    /**
     * 通过用户名查询用户及其角色信息
     *
     * @param username 用户名
     * @return UseVo 对象
     */
    @ApiOperation(value = "通过用户名查询用户及其角色信息")
    @GetMapping("/findUserByUsername/{username}")
    public UserVo findUserByUsername(@PathVariable String username) {
        return userService.findUserByUsername(username);
    }

    /**
     * 通过手机号查询用户及其角色信息
     *
     * @param mobile 手机号
     * @return UseVo 对象
     */
    @ApiOperation(value = "通过手机号查询用户及其角色信息")
    @GetMapping("/findUserByMobile/{mobile}")
    public UserVo findUserByMobile(@PathVariable String mobile) {
        return userService.findUserByMobile(mobile);
    }

    /**
     * 通过OpenId查询
     *
     * @param openId openid
     * @return 对象
     */
    @ApiOperation(value = "通过OpenId查询")
    @GetMapping("/findUserByOpenId/{openId}")
    public UserVo findUserByOpenId(@PathVariable String openId) {
        return userService.findUserByOpenId(openId);
    }

    /**
     * 分页查询用户(imeeting)
     *
     * @param params 参数集
     * @return 用户集合
     */
    @ApiOperation(value = "分页查询用户（imeeting）")
    @GetMapping("/imeetingUserPage")
    public R<Page> imeetingUserPage(@RequestParam Map<String, Object> params) {

        Integer currentCustomerId = userService.getCustomerIdByUserId();
        return new R<>(Boolean.TRUE, userService.selectUserVoByCustomerId(currentCustomerId));

    }

    /**
     * 分页查询用户(admin)
     *
     * @param params 参数集
     * @return 用户集合
     */
    @ApiOperation(value = "分页查询用户（admin）")
    @GetMapping("/userPage")
    public R<Page> userPage(@RequestParam Map<String, Object> params) {

        //当前用户
        String userName = UserUtils.getUser();

        //所属客户
        Integer customerId = userService.getCustomerIdByUserId();

        //当前用户级别
        String level = sysRoleService.getSysRoleLevel(userName);
        
        if(StringUtils.isEmpty(level)){
            level=null;
        }
        params.put("roleLevel", level);

        params.put("customerId", customerId);

        params.put("delFlag", 0);

        return new R<>(Boolean.TRUE, userService.selectUserVoPageForAdmin(new Query(params)));
    }
    /**
     * 上传用户头像
     * (多机部署有问题，建议使用独立的文件服务器)
     *
     * @param file 资源
     * @return filename map
     */
    @ApiOperation(value = "上传用户头像")
    @PostMapping("/upload")
    public Map<String, String> upload(@RequestParam("file") MultipartFile file) {
        String fileExt = FileUtil.extName(file.getOriginalFilename());
        Map<String, String> resultMap = new HashMap<>(1);
//        try {
//            StorePath storePath = fastFileStorageClient.uploadFile(file.getBytes(), fileExt);
//            resultMap.put("filename", fdfsPropertiesConfig.getFileHost() + storePath.getFullPath());
//        } catch (IOException e) {
//            logger.error("文件上传异常", e);
//            throw new RuntimeException(e);
//        }
        return resultMap;
    }
//
//    /**
//     * @param
//     * @author fmsheng
//     * @description dashboard导入Excel文件批量增加用户
//     * @date 2018/10/23 16:46
//     */
//    @Transactional
//    @PostMapping(value = "/imeetingUploadExcel")
//    public R<Boolean> imeetingUploadExcel(@RequestParam("excelFile") MultipartFile excelFile, @RequestParam("parentId") Integer parentId) throws Exception {
//
//        List<SysRole> sysRoleList = sysRoleService.selectListByUserId(parentId);
//
//        if (!StringUtils.contains(sysRoleList.toString(), "role_manager")) {
//            return new R<>(Boolean.FALSE, RespCode.IME_UNAUTHORIZED);
//        }
//
//        if (excelFile.isEmpty()) {
//            return new R<>(Boolean.FALSE);
//        }
//
//        try {
//            InputStream inputStream = excelFile.getInputStream();
//            List<List<Object>> list = importServiceImpl.getBankListByExcel(inputStream, excelFile.getOriginalFilename());
//            inputStream.close();
//
//            for (int i = 0; i < list.size(); i++) {
//
//                List<Object> lo = list.get(i);
//                SysUser sysUser = new SysUser();
//                SysUserRole sysUserRole = new SysUserRole();
//
//                if (lo.get(0) == null) {
//                    continue;
//                } else {
//                    String userName = lo.get(0).toString();
//                    sysUser.setUsername(userName);
//                }
//
//                if (lo.get(1) == null) {
//                    sysUser.setPassword(ENCODER.encode(MeetingConstant.USER_DEF_PWD));
//                } else {
//                    String passWord = lo.get(1).toString();
//                    sysUser.setPassword(ENCODER.encode(passWord));
//                }
//
//                if (lo.get(3) != null) {
//                    String userState = lo.get(3).toString();
//                    if ("有效".equals(userState)) {
//                        sysUser.setDelFlag(CommonConstant.STATUS_NORMAL);
//                    } else if ("无效".equals(userState)) {
//                        sysUser.setDelFlag(CommonConstant.STATUS_DEL);
//                    } else {
//                        sysUser.setDelFlag(CommonConstant.STATUS_NORMAL);
//                    }
//                }
//
//                if (lo.get(4) != null) {
//                    String userDept = lo.get(4).toString();
//                    SysDept sysDept = new SysDept();
//                    sysDept.setName(userDept);
//                    SysDept sysDept1 = sysDeptMapper.selectOne(sysDept);
//                    if (sysDept1 != null) {
//                        sysUser.setDeptId(sysDept1.getDeptId());
//                    }
//                }
//
//                sysUser.setCreateTime(new Date());
//                sysUser.setParentId(parentId);
//                userService.insert(sysUser);
//
//                if (lo.get(2) != null) {
//                    String userRole = lo.get(2).toString();
//                    SysRole sysRole = new SysRole();
//                    sysRole.setRoleDesc(userRole);
//                    SysRole sysRole1 = sysRoleMapper.selectOne(sysRole);
//                    if (sysRole1 != null) {
//                        sysUserRole.setRoleId(sysRole1.getRoleId());
//                        sysUserRole.setUserId(sysUser.getUserId());
//                        sysUserRole.insert();
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            logger.info("导入Excel文件批量增加用户" + e.getMessage());
//            return new R<>(e);
//        }
//
//        return new R<>(true);
//    }

    /**
     * @param
     * @author fmsheng
     * @description admin导入Excel文件批量增加用户
     * @date 2018/10/23 16:46
     */
    @ApiOperation(value = "admin导入Excel文件批量增加用户")
    @Transactional
    @PostMapping(value = "/uploadExcel")
    public R<Boolean> uploadExcel(@RequestParam("excelFile") MultipartFile excelFile) throws Exception {

        if (excelFile.isEmpty()) {
            return new R<>(false);
        }
        RespCode respCodeReturn = RespCode.FAIL;
        JSONObject respData = new JSONObject();
        JSONObject paramsError = new JSONObject();
        int nCountLine = 0;
        int nCountInsert = 0;
        int nCountError = 0;
        try {
            InputStream inputStream = excelFile.getInputStream();
            List<List<Object>> list = importServiceImpl.getBankListByExcel(inputStream, excelFile.getOriginalFilename());
            inputStream.close();
            //读取的EXCEL数据已经忽略了第一行的标题数据
            for (int i = 0; i < list.size(); i++) {

                List<Object> lo = list.get(i);
                String userName = importServiceImpl.getFieldString(lo,0);
                String password = importServiceImpl.getFieldString(lo,1);
                String nickname = importServiceImpl.getFieldString(lo,2);
                String phonenum = importServiceImpl.getFieldString(lo,3);
                String email = importServiceImpl.getFieldString(lo,4);

                if (StringUtils.isNotEmpty(userName)) {
                    nCountLine ++;
                    UserDto userDto = new UserDto();
                    userDto.setUsername(userName);
                    userDto.setNickname(nickname);

                    if (StringUtils.isEmpty(password)) {
                        userDto.setPassword(MeetingConstant.USER_DEF_PWD);
                    } else {
                        userDto.setPassword(password);
                    }

                    userDto.setDelFlag(CommonConstant.STATUS_NORMAL);

                    if (StringUtils.isNotEmpty(phonenum)){
                        userDto.setCall(phonenum);
                    }
                    if (StringUtils.isNotEmpty(email)){
                        userDto.setEmail(email);
                    }
//                    if (lo.size() > 4 && lo.get(4) != null) {
//                        String userDept = lo.get(4).toString();
//                        SysDept sysDept = new SysDept();
//                        sysDept.setName(userDept);
//                        SysDept sysDept1 = sysDeptMapper.selectOne(sysDept);
//                        if (sysDept1 != null) {
//                            sysUser.setDeptId(sysDept1.getDeptId());
//                        }
//                    }

                    userDto.setCreateTime(new Date());
                    RespCode respCode = userService.addUser(userDto);
                    if (respCode == RespCode.SUCCESS){
                        nCountInsert ++;
                    } else {
                        nCountError ++;
                        JSONObject respCodeJson = new JSONObject();
                        respCodeJson.put("code",respCode.getCode());
                        respCodeJson.put("msg",respCode.getMsg());
                        paramsError.put(String.valueOf(i),respCodeJson);
                    }

                }

            }
        } catch (GrgException e) {
            respCodeReturn = e.getStatusCode();
            logger.info("import Excel get GrgException:" + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("import Excel get Exception:" + e.getMessage());
            respCodeReturn = RespCode.ERROR;
//            return new R<>(e);
        }
        if (nCountLine > 0) {
            respData.put("totalCount", nCountLine);
            respData.put("successCount", nCountInsert);
            respData.put("errorCount", nCountError);
            respData.put("errorMsg", paramsError);
            respCodeReturn = RespCode.SUCCESS;

        }

        return new R<>(respCodeReturn,respData);
    }

    /**
     * 修改个人信息
     *
     * @param userDto userDto
     * @return success/false
     */
    @ApiOperation(value = "修改个人信息")
    @PutMapping("/editInfo")
    public R<Boolean> editInfo(@RequestBody UserDto userDto) {

        String username = UserUtils.getUser();

        return userService.updateUserInfo(userDto, username);
    }

    /**
     * 个人密码重置
     *
     * @return success/false
     */
    @ApiOperation(value = "个人密码重置")
    @PutMapping("/passwordReset")
    public R<Boolean> passwordReset() {

        String username = UserUtils.getUser();
        boolean result = userService.passwordReset(username);
        if (result) {
            return new R<>(Boolean.TRUE, RespCode.SUCCESS);
        }
        return new R<>(Boolean.FALSE, RespCode.FAIL);
    }


    /**
     * 管理员密码重置
     *
     * @return success/false
     */
    @ApiOperation(value = "管理员密码重置")
    @PutMapping("/passwordResetByManager/{id}")
    public R<Boolean> passwordResetByManager(@PathVariable("id") String[] userIds) {

        if (userIds.length == 0 || "".equals(userIds) || userIds == null) {
            return new R<>(Boolean.TRUE, RespCode.SUCCESS);
        }
        for (String id : userIds) {
            SysUser sysUser = userService.selectById(id);
            String username = sysUser.getUsername();
            boolean result = userService.passwordReset(username);
            if (!result) {
                return new R<>(Boolean.FALSE, RespCode.FAIL);
            }

        }
        return new R<>(Boolean.TRUE, RespCode.SUCCESS);
}

    /**
     * @author fmsheng
     * @param
     * @description 通过userName更新登录时间
     * @date 2019/1/4 10:44
     */
    @ApiOperation(value = "通过userName更新登录时间")
    @GetMapping("/updateLoginTimeByUserId/{userId}")
    public boolean updateLoginTimeByUserId(@PathVariable Integer userId) {
        SysUser sysUser = new SysUser();
        sysUser.setUserId(userId);
        sysUser.setLoginTime(new Date());
        return userService.updateById(sysUser);
    }
}
