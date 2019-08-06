package com.grgbanking.grgacd.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @Description 附件上传参数封装类
 * @author hsjiang
 * @date 2019/6/18
 * @version 1.0
 */
@Data
public class FileDTO {
    /** 业务id */
    private String fileId;
    /** 文件名称 */
    private String fileName;
    /** 文件类型 */
    private String fileType;
    /** 文件流 */
    private MultipartFile file;
    /** 当前文件块的序号 */
    private String chunk;
    /** 总共文件块数量 */
    private String chunks;
    /** 文件签名 */
    private String md5;
}
