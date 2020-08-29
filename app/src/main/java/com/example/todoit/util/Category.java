package com.example.todoit.util;

public class Category {

//    private int title;
    private String titleStr;
    private int bgColor;
    private Integer icon;

    public Category(String titleStr, int bgColor, Integer icon) {
        this.titleStr = titleStr;
        this.bgColor = bgColor;
        this.icon = icon;
    }

//    public int getTitle() {
//        return title;
//    }

    public String getTitleStr(){
        return titleStr;
    }

    public int getBgColor() {
        return bgColor;
    }

    public int getIcon() {
        return icon;
    }
}
