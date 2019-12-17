package com.hzf.entity;

public class GoodCollection {
    private Integer goodId;
    private String pictureUrl;
    private String description;
    private Float price;
    private String userName;

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public Integer getGoodId() {
        return goodId;
    }

    public void setGoodId(Integer goodId) {
        this.goodId = goodId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "GoodCollection{" +
                "pictureUrl='" + pictureUrl + '\'' +
                ", goodId=" + goodId +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", userName=" + userName +
                '}';
    }
}
