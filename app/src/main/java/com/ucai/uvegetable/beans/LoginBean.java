package com.ucai.uvegetable.beans;

/**
 * Created by Lance
 * on 2018/7/14.
 */

public class LoginBean {
    private String id;
    private String phone;
    private String name;
    private String addr;

    private String mobile1;
    private String leader1;
    private String mobile2;
    private String leader2;
    private String note;

    public void setLeader1(String leader1) {
        this.leader1 = leader1;
    }

    public void setLeader2(String leader2) {
        this.leader2 = leader2;
    }

    public void setMobile1(String mobile1) {
        this.mobile1 = mobile1;
    }

    public void setMobile2(String mobile2) {
        this.mobile2 = mobile2;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getLeader1() {
        return leader1;
    }

    public String getLeader2() {
        return leader2;
    }

    public String getMobile1() {
        return mobile1;
    }

    public String getMobile2() {
        return mobile2;
    }

    public String getNote() {
        return note;
    }

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
