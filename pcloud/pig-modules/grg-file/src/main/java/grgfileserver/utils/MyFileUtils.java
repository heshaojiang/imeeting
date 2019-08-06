package grgfileserver.utils;

import grgfileserver.entity.GrgMethod;
import grgfileserver.entity.MethodCode;
import grgfileserver.entity.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class MyFileUtils {
	//记录已经上传成功的分片文件数量，线程安全
	private static ConcurrentHashMap<String,Set<Integer>> successChunksCount = new ConcurrentHashMap<>();
	private static MyFileUtils instance = new MyFileUtils();
	
	public static MyFileUtils getInstance(){
		return instance;
	}

	/**
	 * 所有分片上传完后。检查是否所有分片序号都存在
	 * @param fileName
	 * @param chunks
	 * @return
	 */
	public boolean CheckChunks(String fileName,int chunks){
		boolean bReturn = false;
		Set<Integer> setChunks = successChunksCount.get(fileName);
		if (setChunks != null){
			boolean bAllChecksOk = true;
			for(int i=0; i<chunks; i++){
				if (setChunks.contains(i) == false){
					//缺少此序号的记录，上传不完整
					bAllChecksOk = false;
					break;
				}
			}
			bReturn = bAllChecksOk;
			log.debug("bAllChecksOk:{}",bAllChecksOk);
		}
		return bReturn;
	}
	/**
	 * 每上传成功一个文件的分片，就在文件名对应的Set中插入分块序号
	 * 对比分片总数，如果上传成功分片数等于分片总数则调用合并文件方法
	 * @param fileName
	 * @param chunks
	 * @return
	 */
	public boolean successChunks(String fileName,int chunkIndex,int chunks){
		boolean bReturn = false;
		log.debug("fileName:{},chunkIndex:{},chunks:{}",fileName,chunkIndex,chunks);

//		Set<Integer> chunksNow = successChunksCount.getOrDefault(fileName, new HashSet<>());
        Set<Integer> chunksNow = successChunksCount.get(fileName);
        if (chunksNow == null){
			log.debug("chunksNow is null,Create New HashSet");
            chunksNow = new HashSet<>();
        }
		chunksNow.add(chunkIndex);
		int chunkCount = chunksNow.size();
		log.debug("chunkCount:{}",chunkCount);
		successChunksCount.put(fileName, chunksNow);
		if(chunkCount >= chunks){
			log.debug("Start CheckChunks");
			bReturn = CheckChunks(fileName,chunks);
		}
		return bReturn;
	}
	
	/**
	 * 上传完成后合并分片文件
	 * @param srcFile 分片的目录地址
	 * @param destFile	合成后存放的目录地址
	 * @param chunks	分片总数
	 * @return
	 * @throws FileNotFoundException 
	 */
	public String mergeChunks(File srcFile,
							  File destFile,int chunks) throws Exception{
		String sResultFile = "";
		try {

			//创建目标目录
			File destDir = destFile.getParentFile();
			if(!destDir.exists()){
				destDir.mkdirs();
			}
			//创建目标文件流
			BufferedOutputStream destOutputStream =
					new BufferedOutputStream(new FileOutputStream(destFile));

			//循环将每个分片的数据写入目标文件
			byte[] fileBuffer = new byte[1024];//文件读写缓存
			int readBytesLength = 0;//每次读取字节数
			log.info("Start Merge File="+destFile.getAbsolutePath());
			for(int i=0; i<chunks; i++){
				File sourceFile = new File(getFilePartName(srcFile.getAbsolutePath(),i));
				log.info("Start Merge File Chunk ="+sourceFile.getAbsolutePath());
				BufferedInputStream sourceInputStream =
						new BufferedInputStream(new FileInputStream(sourceFile));
				while((readBytesLength=sourceInputStream.read(fileBuffer))!=-1){
					destOutputStream.write(fileBuffer, 0, readBytesLength);
				}
				sourceInputStream.close();
				//分片用完后删除
				sourceFile.delete();
			}
			//文件合并完成后删除临时文件夹
			File sourceDir = new File(srcFile.getParent());
			sourceDir.delete();
			successChunksCount.remove(destFile.getAbsolutePath());

			destOutputStream.flush();
			destOutputStream.close();
			log.info("Merge File Finished ="+destFile.getAbsolutePath());
			if (destFile.exists() && destFile.canRead()){
				sResultFile = destFile.getCanonicalPath();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new GrgException(StatusCode.MERGE_FILE_ERR);
		}
		return sResultFile;
	}

	/**
	 * 返回一个文件的MD5值
	 *
	 * @param saveFile
	 *            文件路径
	 * @return MD5值，出错时返回null
	 */
	public String getFileDigest(File saveFile) {
		try {
			return DigestUtils.md5Hex(new FileInputStream(saveFile));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	static public String getFilePartName(String fileName,int nChunkIndex){
		return fileName+".part"+nChunkIndex;
	}
	public static ConcurrentHashMap<String,Set<Integer>> GetMap(){
		return successChunksCount;
	}

	/**
	 * 生成文件别名
	 * @param fileName
	 * @return 获得的随机数
	 * @author hsjiang
	 * @date 2019/6/19/019
	 **/
	public static String getFileNameAlias(String fileName) {
		final String numberChar = "0123456789";
		Long seed = System.currentTimeMillis();// 获得系统时间，作为生成随机数的种子
		StringBuffer sb = new StringBuffer();// 装载生成的随机数
		Random random = new Random(seed);// 调用种子生成随机数
		for (int i = 0; i < 20; i++) {
			sb.append(numberChar.charAt(random.nextInt(numberChar.length())));
		}
		sb.append(fileName);
		return sb.toString();
	}

	/**
	 * 将文件内容转换为字节数组
	 * @param fileUrl 文件路径
	 * @return byte[] 字节数组
	 * @author hsjiang
	 * @date 2019/6/26/026
	 **/
	public static byte[] fileToByteArray(String fileUrl){
		byte[] result = null;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024 * 4];
		InputStream in = null;
		try {
			in = new FileInputStream(fileUrl);
			int n = 0;
			while ((n = in.read(buffer)) != -1) {
				out.write(buffer, 0, n);
			}
			result = out.toByteArray();
		}catch (FileNotFoundException e){
			log.error("文件不存在:[{}]", e);
		}catch (IOException e){

		}
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				log.error("输入输出流关闭错误:[{}]", e);
			}
		}
		return result;
	}

}
