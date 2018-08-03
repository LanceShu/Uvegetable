package com.ucai.uvegetable.beans;

/**
 * Created by Lance
 * on 2018/8/2.
 */

public class DriverBean {
    private String deliverDate;
    private String orderDate;
    private int id;
    private String name;
    private String cardid;
    private String mobile;
    private String note;

    public void setDeliverDate(String deliverDate) {
        this.deliverDate = deliverDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getDeliverDate() {
        return deliverDate;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCardid(String cardid) {
        this.cardid = cardid;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getNote() {
        return note;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getCardid() {
        return cardid;
    }

    public String getMobile() {
        return mobile;
    }
}
