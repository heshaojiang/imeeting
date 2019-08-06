package grgfileserver.entity;

public enum StatusCode {
    SUCCESS(0, "File Upload Succ! MD5 Check OK!"),
    UPLOADING(1,"Chunk Upload OK!"),
    MD5_FAIL(2,"File Merge Succ! MD5 Check FAIL!"),
    SERVER_ERR(-1, "Server Error Please Retry..."),
    MERGE_FILE_ERR(-1, "File Merge Fail!"),
    WRITE_FILE_ERR(-2, "File Write Fail!"),
    PARAM_ERR(-3, "Param Error!"),
    GET_FILE_ERR(-4, "File Upload Is Empty!");

    private int code;
    private String msg;

    StatusCode(int code, String msg) {
        this.msg = msg;
        this.code = code;
    }

    public int getCode() {
        return code;
    }
    public String getMsg() {
        return msg;
    }
}
