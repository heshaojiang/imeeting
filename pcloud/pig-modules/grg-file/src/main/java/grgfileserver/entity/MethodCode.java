package grgfileserver.entity;


public enum MethodCode {
    LOGIN(1040),
    LOGOUT(1041),
    UPDATE(1042);
    private int code;
    MethodCode(int code) {
        this.code = code;
    }
    public int getCode() {
        return code;
    }
}

