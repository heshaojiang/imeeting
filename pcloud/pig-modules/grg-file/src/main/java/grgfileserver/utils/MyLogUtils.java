package grgfileserver.utils;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 *
 */
@Slf4j
public class MyLogUtils {
	private static MyLogUtils instance = new MyLogUtils();
	private SimpleDateFormat simpleDateFormat;
	public static MyLogUtils getInstance(){
		return instance;
	}
	
	public MyLogUtils() {
		// TODO Auto-generated constructor stub
		simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	}
	
	/**
	 * 后台打印信息
	 * @param message
	 */
	public void log(String message){
		String time = simpleDateFormat.format(new Date());
		System.out.println("GRGUpload::"+time+"::"+message);
	}
	static public void Log(String message){
		instance.log(message);
	}

	public static Logger getLogger(){
		return log;
	}

	public static void t(String msg){
		instance.getLogger().trace(msg);
	}

	public static void d(String msg){
		instance.getLogger().debug(msg);
	}

	public static void i(String msg){
		instance.getLogger().info(msg);
	}

	public static void w(String msg){
		instance.getLogger().warn(msg);
	}

	public static void e(String msg){
		instance.getLogger().error(msg);
	}
}
