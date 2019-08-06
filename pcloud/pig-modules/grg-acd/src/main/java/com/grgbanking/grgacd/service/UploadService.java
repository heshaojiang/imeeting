package com.grgbanking.grgacd.service;

import com.github.pig.common.util.RespCode;
import com.github.pig.common.util.exception.GrgException;
import com.grgbanking.grgacd.dto.FileDTO;
import com.grgbanking.grgacd.model.AcdCalls;
import grgfileserver.entity.JsonResult;
import grgfileserver.entity.StatusCode;
import grgfileserver.entity.UploadStatus;
import grgfileserver.service.FileService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 附件上传服务层
 * @auther: hsjiang
 * @date: 2019/6/19/019
 * @version 1.0
 */
@AllArgsConstructor
@Service
@Slf4j
public class UploadService {
    private FileService fileService;
    private AcdCallsService acdCallsService;

    private final static Map<Integer,RespCode> fileCodeToRespCode = new HashMap<>();
    static {
        init();
    }

    /**
     * 附件上传
     * @param dto
     * @return boolean
     * @author hsjiang
     * @date 2019/6/19/019
     **/
    public RespCode upload(FileDTO dto){
        boolean result = false;
        JsonResult<UploadStatus> uploadStatus = fileService.upload(dto.getFileId(), dto.getFileName(),
                dto.getFileType(), dto.getFile(), dto.getChunk(), dto.getChunks(), dto.getMd5());
        //处理业务逻辑
        result = updateCall(dto,uploadStatus);
        return fileCodeToRespCode.get(uploadStatus.getStatus());
    }

    /**
     * 更新通话记录
     * @param dto
     * @param uploadStatus 文件上传结果
     * @return true:更新成功，false：更新失败
     * @author hsjiang
     * @date 2019/6/19
     **/
    private boolean updateCall(FileDTO dto,JsonResult<UploadStatus> uploadStatus){
        String callId = dto.getFileId();
        AcdCalls upateRecord = new AcdCalls();
        RespCode respCode = fileCodeToRespCode.get(uploadStatus.getStatus());
        if(respCode == null || StringUtils.isEmpty(callId)){
            throw new GrgException(RespCode.FILE_PARAM_ERR);
        }
        //不管上传是否成功，都要记录上传状态
        int status = respCode.getCode();
        String fileType = dto.getFileType();

        String filePath = null;
        if (uploadStatus.getData() != null) {
            filePath = uploadStatus.getData().getResult();
        }
        log.info("UploadFile End! callId:{},respCode:{},fileType:{},filePath:{}",callId,respCode.name(),fileType,filePath);
        switch (fileType) {
            case "video":
                upateRecord.setRecorderVideoStatus(status);
                if(respCode == RespCode.SUCCESS)
                    upateRecord.setRecorderVideoFilepath(filePath);
                break;
            case "audio":
                upateRecord.setRecorderAudioStatus(status);
                if(respCode == RespCode.SUCCESS)
                    upateRecord.setRecorderAudioFilepath(filePath);
                break;
            case "image":
                upateRecord.setRecorderPreviewFilepath(filePath);
                break;
        }

        upateRecord.setCallId(callId);
        upateRecord.setUpdateTime(new Date());
        boolean result = acdCallsService.updateById(upateRecord);
        if(result){
            acdCallsService.updateCache(upateRecord);
            log.info("UploadFile updateCalls ok !");
        } else {
            throw new GrgException(RespCode.IME_DB_FAIL);
        }
        return result;
    }

    /**
     * 初始化
     * 关联返回状态code
     * @param
     * @return
     * @author hsjiang
     * @date 2019/6/23/023
     **/
    private static void init(){
        fileCodeToRespCode.put(StatusCode.SUCCESS.getCode(),RespCode.SUCCESS);
        fileCodeToRespCode.put(StatusCode.UPLOADING.getCode(),RespCode.FILE_UPLOADING);
        fileCodeToRespCode.put(StatusCode.MD5_FAIL.getCode(),RespCode.FILE_MD5_FAIL);
        fileCodeToRespCode.put(StatusCode.SERVER_ERR.getCode(),RespCode.FILE_SERVER_ERR);
        fileCodeToRespCode.put(StatusCode.MERGE_FILE_ERR.getCode(),RespCode.FILE_MERGE_FILE_ERR);
        fileCodeToRespCode.put(StatusCode.WRITE_FILE_ERR.getCode(),RespCode.FILE_WRITE_FILE_ERR);
        fileCodeToRespCode.put(StatusCode.PARAM_ERR.getCode(),RespCode.FILE_PARAM_ERR);
        fileCodeToRespCode.put(StatusCode.GET_FILE_ERR.getCode(),RespCode.FILE_GET_FILE_ERR);
    }


}
