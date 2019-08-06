package grgfileserver.service;

import grgfileserver.entity.JsonResult;
import grgfileserver.entity.StatusCode;
import grgfileserver.entity.UploadStatus;
import grgfileserver.utils.GrgException;
import grgfileserver.utils.MyFileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
/**
 * @Description: 文件处理服务层，（未完成）
 * @auther: hsjiang
 * @date: 2019/6/19/019
 * @version 1.0
 */
@Slf4j
@Service
public class FileService {
	@Value("${config.fileSavePath}")
	private String fileSavePath;
	@Value("${config.fileTempPath}")
	private String fileTempPath;
	/**
	 * 上传文件
	 * @param filePathAndName
	 * @param file
	 * @param sChunk
	 * @param sChunks
	 * @param strClientMD5
	 * @return
	 */
	public JsonResult<UploadStatus> upload(String id, String filePathAndName, String fileType, MultipartFile file, String sChunk, String sChunks, String strClientMD5){
		JsonResult<UploadStatus> jsonResult = new JsonResult<>();
		try{
			log.info("id:{},fileType:{},FileName:{},chunk:{},chunks:{},md5:{}",id,fileType,filePathAndName,sChunk,sChunks,strClientMD5);
			int iChunk = 0;
			int iChunks = 0;
			try {
				if (!StringUtils.isEmpty(sChunk)){
					iChunk = Integer.parseInt(sChunk);
				}
				if (!StringUtils.isEmpty(sChunks)){
					iChunks = Integer.parseInt(sChunks);
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
				log.error("Param Error!");
				throw new GrgException(StatusCode.PARAM_ERR);
			}
			if (StringUtils.isEmpty(filePathAndName)){
				filePathAndName = file.getOriginalFilename();
			}
			if (StringUtils.isEmpty(fileType)){
				fileType = "file";
			}
			if (file == null || file.getSize() == 0){
				log.error("Upload File Is Empty!");
				throw new GrgException(StatusCode.GET_FILE_ERR);
			}
			if (iChunks <= 1) {
				//没有分块的单个文件上传
				jsonResult = uploadOneFile(jsonResult,filePathAndName,file,strClientMD5);
			} else {
				if (iChunk >= iChunks){
					log.error("chunk >= chunks Error!");
					throw new GrgException(StatusCode.PARAM_ERR);
				}
				//分块文件上传
				jsonResult = uploadPartFile(jsonResult,filePathAndName,file,iChunk,iChunks,strClientMD5);

			}
		} catch (GrgException e) {
			StatusCode statusCode = e.getStatusCode();
			if (statusCode != null){
				log.error("Upload Get Error! Code:{},msg:{}",e.getStatusCode().getCode(),e.getStatusCode().getMsg());
				jsonResult.setStatusCode(statusCode);
			}
			e.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
			log.error("Upload Get Exception! msg:{}",e.getMessage());
			jsonResult.setStatusCode(StatusCode.SERVER_ERR);
			e.printStackTrace();
		}
		return jsonResult;
	}
	/**
	 * 从文件路径中获取文件名
	 * @param filePathAndName 文件路径
	 * @return
	 * @author hsjiang
	 * @date 2019/6/19/019
	 **/
	private String getFileNameFromPath(String filePathAndName) {
		String fileName = new String();
		int nIdx = filePathAndName.lastIndexOf("/");
		if (nIdx < 0){
			nIdx = filePathAndName.lastIndexOf("\\");
		}
		if (nIdx >= 0){
			fileName = filePathAndName.substring(nIdx+1);
		} else {
			fileName = filePathAndName;
		}
		return fileName;
	}

	/**
	 * 保存文件
	 * @param file
     * @param saveFile
	 * @return
	 * @author hsjiang
	 * @date 2019/6/19/019
	 **/
	private void saveUploadFile(MultipartFile file,File saveFile) throws GrgException {
		try {
			File fileParent = saveFile.getParentFile();
			if(!fileParent.exists()){
				fileParent.mkdirs();
			}
			log.debug("saveFile:{}",saveFile.getAbsolutePath());

			file.transferTo(saveFile);
		} catch (Exception e) {
			e.printStackTrace();
			throw new GrgException(StatusCode.WRITE_FILE_ERR);
		}

		if (saveFile.exists() == false || saveFile.canRead() == false){
			throw new GrgException(StatusCode.WRITE_FILE_ERR);
		}
		log.debug("saveFile End!");
	}
	/**
	 * 没有分块的文件上传
	 * @param
	 * @return
	 * @author hsjiang
	 * @date 2019/6/19/019
	 **/
	public JsonResult<UploadStatus> uploadOneFile(JsonResult<UploadStatus> jsonResult, String filePathAndName, MultipartFile file, String strClientMD5) throws Exception {
		File saveFile = new File(fileSavePath+filePathAndName);
		log.info("Upload File With No Chunk! {}",filePathAndName);
		//储存为临时文件
		saveUploadFile(file,saveFile.getAbsoluteFile());//需要调用getAbsoluteFile才能生成有效的路径
		UploadStatus uploadStatus = GetUploadResult(saveFile.getCanonicalPath(),strClientMD5);
		jsonResult.setData(uploadStatus);
		return jsonResult;
	}
	//有多个分块的文件上传
	public JsonResult<UploadStatus> uploadPartFile(JsonResult<UploadStatus> jsonResult, String filePathAndName, MultipartFile file, int chunk, int chunks, String strClientMD5) throws Exception {

		String fileName = getFileNameFromPath(filePathAndName);
		log.info("Upload File Chunk! fileName:{},filePathAndName:{}",fileName,filePathAndName);

//		MyLogUtils.Log("文件名="+filePathAndName+",分片总数="+chunks+",当前分片索引="+chunk);
		//创建临时文件储存目录，需要创建以文件名为名字的文件夹
		File tempsDir = new File(fileTempPath+filePathAndName);
		if(!tempsDir.exists()){
			tempsDir.mkdirs();
		}
		File tempFile = new File(tempsDir.getAbsolutePath(),fileName);

		File saveFile = new File(fileSavePath+filePathAndName);
		log.debug("tempFile:{},saveFile:{}",tempFile.getAbsolutePath(),saveFile.getAbsolutePath());

		//储存为临时文件
		File tempFilePart = new File(tempsDir.getAbsolutePath(), MyFileUtils.getFilePartName(fileName,chunk));

		saveUploadFile(file,tempFilePart);

		//检查分片是否下载完成
		boolean bAllChunksSuccess = MyFileUtils.getInstance().successChunks(saveFile.getAbsolutePath(),chunk, chunks);
		//构建返回状态信息
		log.debug("successChunks:{}",bAllChunksSuccess);
		if(bAllChunksSuccess){
			//分片下载完成后合并分片为文件
			String sResultFile = MyFileUtils.getInstance().mergeChunks(tempFile,
					saveFile, chunks);
			log.debug("mergeChunks:{}",sResultFile);
			UploadStatus uploadStatus = GetUploadResult(sResultFile,strClientMD5);

			if (uploadStatus.isMd5CheckOk() == true){
				log.debug("Md5 Check Ok");
				jsonResult.setStatus(StatusCode.SUCCESS.getCode());
			} else {
				log.debug("Md5 Check fail");
				jsonResult.setStatus(StatusCode.MD5_FAIL.getCode());
			}

			jsonResult.setData(uploadStatus);
		}else{
			log.info("Upload One Chunk Succ!");

			jsonResult.setStatus(StatusCode.UPLOADING.getCode());
			jsonResult.setData(null);
		}
		return jsonResult;
	}
	UploadStatus GetUploadResult(String sResultFile, String strClientMD5){
		UploadStatus uploadStatus = new UploadStatus();

		//返回文件地址
		uploadStatus.setResult(sResultFile);
		String sMD5 = MyFileUtils.getInstance().getFileDigest(new File(sResultFile));
		uploadStatus.setMd5(sMD5);
		log.debug("sResultFile:{},sMD5:{},strClientMD5:{}",sResultFile,sMD5,strClientMD5);
		boolean bCheckMd5Ok = false;
		if (strClientMD5 != null && sMD5 != null && strClientMD5.length() > 0 && sMD5.length() > 0)
		{
			String sClientMd6 = strClientMD5.toLowerCase();
			log.info("Server file " + sResultFile +"  CHECK MD5 ClientMD5:"+sClientMd6+" MD5: " + sMD5);

			bCheckMd5Ok = sClientMd6.equals(sMD5);
		}

		uploadStatus.setMd5CheckOk(bCheckMd5Ok);
		return uploadStatus;
	}

	public static ConcurrentHashMap<String,Set<Integer>> GetMap() {
		return MyFileUtils.GetMap();
	}

    /**
     * 获取文件的相对路径
     * @param path 文件绝对路径
     * @return
     * @author hsjiang
     * @date 2019/6/19/019
     **/
	public String getRelativePath(String path){
		return path.replace(fileSavePath,"").replace(fileSavePath.replaceAll("/","\\"),"");
	}


}
