package com.ucai.uvegetable.beans;

/**
 * Created by Lance
 * on 2018/7/19.
 */

public class ProductBean {
    private String id;
    private String name;
    private String unit;
    private double price;
    private double user_price;
    private String imgfile;
    private String note;
    private String pcode;
    private double num;

    public void setNum(double num) {
        this.num = num;
    }

    public double getNum() {
        return num;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImgfile(String imgfile) {
        this.imgfile = imgfile;
    }

    public void setPcode(String pcode) {
        this.pcode = pcode;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setUser_price(double user_price) {
        this.user_price = user_price;
    }

    public String getNote() {
        return note;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImgfile() {
        return imgfile;
    }

    public String getPcode() {
        return pcode;
    }

    public double getPrice() {
        return price;
    }

    public String getUnit() {
        return unit;
    }

    public double getUser_price() {
        return user_price;
    }
}
