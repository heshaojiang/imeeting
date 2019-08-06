package com.github.pig.admin.TokenVerify;

public class TokenBean {
    private String user;
    private String pwd;
    private String createTime;
    private String validTime;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getValidTime() {
        return validTime;
    }

    public void setValidTime(String validTime) {
        this.validTime = validTime;
    }

    @Override

    public String toString() {
        return "TokenBean{" +
                "user='" + user + '\'' +
                ", pwd='" + pwd + '\'' +
                ", createTime=" + createTime +
                ", validTime=" + validTime +
                '}';
    }
}
