package com.grgbanking.grgacd.controller;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.github.pig.common.util.R;
import com.github.pig.common.util.RespCode;
import com.github.pig.common.web.BaseController;
import com.grgbanking.grgacd.dto.FileDTO;
import com.grgbanking.grgacd.model.AcdEvaluationScore;
import com.grgbanking.grgacd.service.AcdEvaluationScoreService;
import com.grgbanking.grgacd.service.AcdQueueService;
import com.grgbanking.grgacd.service.AcdTerminalService;
import com.grgbanking.grgacd.service.UploadService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @author tjshan
 * @since 2019-5-17
 */
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController extends BaseController {

    @Autowired
    AcdQueueService acdQueueService;
    @Autowired
    AcdTerminalService terminalService;
    @Autowired
    private UploadService uploadService;
    @Autowired
    AcdEvaluationScoreService evaluationScoreService;



    /**
     * 检查给定的字符串是否已经在使用中
     * 返回 SUCCESS 则字符串未在使用
     * 返回 FAIL 则字符串已经在使用中
     */
    @RequestMapping("/validate")
    public R<Boolean> validate(@RequestParam Map<String, Object> params) {
        String str = (String) params.get("str");
        String query= (String) params.get("query");
        if (StringUtils.isEmpty(query) || StringUtils.isEmpty(str)){
            return new R<>(Boolean.FALSE, RespCode.IME_INVALIDPARAMETER);
        }
        boolean bValidate = false;
        if ("queueId".equals(query)){
            //队列Id
            EntityWrapper wrapper=new EntityWrapper();
            wrapper.eq("queue_id",str);
            if (acdQueueService.selectCount(wrapper)==0){
                bValidate = true;
            }

        }else if("queueName".equals(query)){
            EntityWrapper wrapper=new EntityWrapper();
            wrapper.eq("queue_name",str);
            if (acdQueueService.selectCount(wrapper)==0){
                bValidate = true;
            }
        }else if("terminalNo".equals(query)){
            EntityWrapper wrapper=new EntityWrapper();
            wrapper.eq("terminal_no",str);
            if (terminalService.selectCount(wrapper)==0){
                bValidate = true;
            }

        }else if("evaluateName".equals(query)){
            EntityWrapper wrapper=new EntityWrapper();
            wrapper.eq("name",str);
            if (evaluationScoreService.selectCount(wrapper)==0){
                bValidate = true;
            }

        }
        if (bValidate) {
            return new R<>(Boolean.TRUE, RespCode.SUCCESS);
        } else {
            return new R<>(Boolean.FALSE, RespCode.FAIL);
        }

    }
    /**
     * 文件上传请求接口
     * @param id  详细说明见，{@link FileDTO}
     * @param strFileName
     * @return
     * @author hsjiang
     * @date 2019/6/19/019
     **/
    @RequestMapping("/uploadFile")
    public R<Boolean> uploadFile(@RequestParam(required = false, value = "fileId") String id,
                                 @RequestParam(required = false, value = "fileName") String strFileName,
                                 @RequestParam(required = false, value = "fileType") String strFileType,
                                 @RequestParam(required = false, value = "file") MultipartFile file,
                                 @RequestParam(required = false, value = "chunk") String sChunk,
                                 @RequestParam(required = false, value = "chunks") String sChunks,
                                 @RequestParam(required = false, value = "md5") String strMd5
                                 ) {
        FileDTO dto = new FileDTO();
        dto.setFileId(id);
        dto.setFileName(strFileName);
        dto.setFileType(strFileType);
        dto.setFile(file);
        dto.setChunk(sChunk);
        dto.setChunks(sChunks);
        dto.setMd5(strMd5);
        return new R(uploadService.upload(dto));
    }

}
