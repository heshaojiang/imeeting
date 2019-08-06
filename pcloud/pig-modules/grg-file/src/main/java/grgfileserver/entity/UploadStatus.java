package grgfileserver.entity;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 上传文件的状态类
 * @author 许映杰
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)	//如果字段为null，则不在json中返回
public class UploadStatus {
	private String result;
    private String md5;
    private boolean md5CheckOk;

	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

	public boolean isMd5CheckOk() {
		return md5CheckOk;
	}

	public void setMd5CheckOk(boolean md5CheckOk) {
		this.md5CheckOk = md5CheckOk;
	}

}
