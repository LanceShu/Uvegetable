package com.ucai.uvegetable.beans;

/**
 * Created by Lance
 * on 2018/7/28.
 */

public class OrderBean {
    private String productId;
    private double price;
    private double num;
    private String note;

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setNum(double num) {
        this.num = num;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getProductId() {
        return productId;
    }

    public double getPrice() {
        return price;
    }

    public String getNote() {
        return note;
    }

    public double getNum() {
        return num;
    }
}
