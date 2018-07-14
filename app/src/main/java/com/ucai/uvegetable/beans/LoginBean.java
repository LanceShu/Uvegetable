package com.ucai.uvegetable.beans;

/**
 * Created by Lance on 2018/7/14.
 */

public class LoginBean {
    private String id;
    private String phone;
    private String name;
    private String addr;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getPhone() {
        return phone;
    }

    public String getName() {
        return name;
    }

    public String getAddr() {
        return addr;
    }
}
