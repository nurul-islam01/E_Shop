package com.nurul.e_shop.model;

public class Product {

    private String id;
    private String title;
    private String description;
    private float recentPrice;
    private float lastPrice;
    private String category;
    private int rating = 0;
    private String adsImageUrl, cardImageUrl;

    public Product() {
    }

    public Product(String id, String title, String description, float recentPrice, float lastPrice, String category, int rating, String adsImageUrl, String cardImageUrl) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.recentPrice = recentPrice;
        this.lastPrice = lastPrice;
        this.category = category;
        this.rating = rating;
        this.adsImageUrl = adsImageUrl;
        this.cardImageUrl = cardImageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getRecentPrice() {
        return recentPrice;
    }

    public void setRecentPrice(float recentPrice) {
        this.recentPrice = recentPrice;
    }

    public float getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(float lastPrice) {
        this.lastPrice = lastPrice;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getAdsImageUrl() {
        return adsImageUrl;
    }

    public void setAdsImageUrl(String adsImageUrl) {
        this.adsImageUrl = adsImageUrl;
    }

    public String getCardImageUrl() {
        return cardImageUrl;
    }

    public void setCardImageUrl(String cardImageUrl) {
        this.cardImageUrl = cardImageUrl;
    }
}
