package com.tem.gettogether.view;

public class RollTextItem {

    public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    private int imgId;

    private String msg;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;
    private int textColor;
    private String imagePath;

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public RollTextItem(String msg, int imgId , String imagePath, String name){
        this(msg,imgId,imagePath,name, android.R.color.holo_blue_light);
    }

    public RollTextItem(String msg, int imgId, String imagePath, String name,int textColor){
        this.msg = msg;
        this.imgId = imgId;
        this.name = name;
        this.textColor = textColor;
        this.imagePath = imagePath;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }
}
