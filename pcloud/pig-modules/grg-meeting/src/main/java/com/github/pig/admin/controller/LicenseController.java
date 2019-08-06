package com.github.pig.admin.controller;

import com.github.pig.admin.common.license.LicenseManage;
import com.github.pig.common.constant.MeetingConstant;
import com.github.pig.common.util.R;
import com.github.pig.common.util.RespCode;
import com.github.pig.common.util.UserUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * @author: wjqiu
 * @date: 2019-02-25
 * @description:
 */

@RestController
@RequestMapping("/license")
@Api
public class LicenseController {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(MeetingController.class);
    @Autowired
    LicenseManage licenseManage;

    @ApiOperation(value = "获取机器码")
    @GetMapping(value="/getmachinecode")
    @ResponseBody
    public R<String> getmachinecode(){
        RespCode respCode = RespCode.SUCCESS;
        R<String> r = new R<>();
        try {
            String machineCode =licenseManage.getMachineCode();
            r.setData(machineCode);
            respCode = RespCode.SUCCESS;
        }catch (Exception e){

            respCode = RespCode.CNSL_LICENSE_GET_MACHINE_CODE_ERR;

        }
        r.setRespCode(respCode);
        return r;
    }

//    @ApiOperation(value = "获取license信息")
    @GetMapping(value="/getlicenseinfo")
    @ResponseBody
    public R<Object> getlicenseinfo(){
        RespCode respCode = RespCode.SUCCESS;
        R<Object> r = new R<>();
        try {
            r.setData(licenseManage.getLicenseInfo());
            respCode = RespCode.SUCCESS;
        }catch (Exception e){

            respCode = RespCode.CNSL_LICENSE_GET_MACHINE_CODE_ERR;

        }
        r.setRespCode(respCode);
        return r;
    }


    @ApiOperation(value = "上传license文件")
    @RequestMapping(value="/uploadlicense",method= RequestMethod.POST)
    @ResponseBody
    public R<Boolean> uploadlicense(MultipartFile file){
        if (!UserUtils.isAdminUser()){
            return new R<>(RespCode.IME_UNAUTHORIZED);
        }
        RespCode respCode = RespCode.SUCCESS;
        try {
//            String filename = file.getOriginalFilename();
            InputStream in = file.getInputStream();
            if (licenseManage.licenseFile_verify(in)){
                respCode = RespCode.SUCCESS;

                File saveFile = new File(licenseManage.getLicenseFilePath());
                file.transferTo(saveFile);

            } else {
                respCode = RespCode.CNSL_LICENSE_WRONG;
            }

            in.close();

        }catch (Exception e){
            respCode = RespCode.CNSL_LICENSE_ERROR;
            e.printStackTrace();
        }

        return new R<>(respCode);
    }

}

