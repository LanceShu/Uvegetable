package com.ucai.uvegetable.beans;

/**
 * Created by Lance on 2018/7/14.
 */

public class RegisterBean {
    private String phone;
    private String pwd;
    private String name;
    private String addr;

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getAddr() {
        return addr;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getPwd() {
        return pwd;
    }
}
