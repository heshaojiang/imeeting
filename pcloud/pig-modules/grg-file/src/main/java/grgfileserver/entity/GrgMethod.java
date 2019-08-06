package grgfileserver.entity;

public class GrgMethod {
    private int method;
//    private int status;	//返回的状态值
    public GrgMethod(MethodCode method) {
        this.method = method.getCode();
    }

    public int getMethod() {
        return method;
    }

    public void setMethod(int method) {
        this.method = method;
    }
}
