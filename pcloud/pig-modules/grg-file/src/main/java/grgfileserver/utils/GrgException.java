package grgfileserver.utils;


import grgfileserver.entity.StatusCode;

public class GrgException extends Exception {
    private StatusCode statusCode;
    //无参构造
    public GrgException() {}
    public GrgException(String msg) {
        super(msg);
    }
    public GrgException(StatusCode statusCode) {
        setStatusCode(statusCode);
    }
    public StatusCode getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }
}
