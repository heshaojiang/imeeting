package grgfileserver.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * 返回JSON字符串模板类
 * @author 许映杰
 *
 * @param <T>
 */
@JsonInclude(Include.NON_NULL)	//如果字段为null，则不在json中返回
public class JsonResult<T>{
	private int status;	//返回的错误码，为0则没有错误
	private T data;		//返回的内容，泛型
	private String errmsg;		//返回的错误信息
	public JsonResult() {}
	public JsonResult(StatusCode statusCode) {
		setStatusCode(statusCode);
	}
	public JsonResult(int status, String msg) {
		this.status = status;
		this.errmsg = msg;
	}
	public JsonResult(StatusCode statusCode, T data) {
		this(statusCode);
		this.data = data;
	}
	public void setStatusCode(StatusCode statusCode) {
		this.status = statusCode.getCode();
		this.errmsg = statusCode.getMsg();
	}
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public String getErrmsg() {
		return errmsg;
	}

	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}

}
