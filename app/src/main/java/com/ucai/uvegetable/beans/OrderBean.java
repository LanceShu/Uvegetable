package com.ucai.uvegetable.beans;

/**
 * Created by Lance
 * on 2018/7/25.
 */

public class OrderBean {
    // these are params need to be posted;
    private String productId;
    private double price;
    private int num;
    private String note;
    // these are the product's information;
    private String name;
    private String imgfile;
    private String unit;
    private double totalPrice;
    private String pcode;

    public void setPcode(String pcode) {
        this.pcode = pcode;
    }

    public String getPcode() {
        return pcode;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setImgfile(String imgfile) {
        this.imgfile = imgfile;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getUnit() {
        return unit;
    }

    public String getImgfile() {
        return imgfile;
    }

    public String getName() {
        return name;
    }

    public String getNote() {
        return note;
    }

    public double getPrice() {
        return price;
    }

    public int getNum() {
        return num;
    }

    public String getProductId() {
        return productId;
    }

    public double getTotalPrice() {
        return totalPrice;
    }
}
