package com.hc360.score.utils;

import java.io.Serializable;
import java.util.List;

/**
 * @Auther: Administrator
 * @Date: 2019/2/18 10:10
 * @Description: 商机算分
 */
public class BusinChance implements Serializable {

    private int hasSupCat;              //是否有类目(1 有 0 没有)

    private int paramAmount;            //参数个数

    private int repetitorAmount;        //重复参数概率编码

    private String params;                  //商品参数

    private int hasOtherTitleDetail;    //标题是否包含关键词 (0 不包含 1 包含)

    private String isSupportTrade;          //是否支持在线交易（0 否 1 是）

    private int priceType;              //报价方式(0-电话报价 1–直接报价)

    private int priceItemCount;         //价格条数

    private int priceTypeNew;           //报价方式算分标识

    private int imageAmount;            //图片数量

    private int firstInmageSize;        //第一张图片宽高是否大于 300*300(0-否 1-是)

    private int detailWordAmount;       //详情内容字数

    private int detailWordAmountType;   //详情内容字数类型 (1  字数 < 150 、 2   150<= 字数 <300、 3  字数 >= 300)

    private String detailImageType;      //详细内容图片宽度分布 (0,0,0,0 --- 700以上,500至700,300至500,300以下 -- 张数)

    private int thanSevenHundred;       //详细内容图片宽度 700以上的图片范围标识

    private int fiveBetweenSeven;       //详细内容图片宽度 500至700的范围标识

    private int threeBetweenFive;       //详细内容图片宽度 300至500的范围标识

    private int lessThreeHundred;       //详细内容图片宽度 300以下的范围标识

    private int score;                  //分数

    private int start;                  //星级


    private int hasSupCatScore;         //类目得分

    private int hasnoParam;             //参数得分

    private int haslongtitle;           //标题得分

    private int hasPrice;               //报价得分

    private int photoCount;             //图片数量得分

    private int firstPhoto;             //第一张图片得分

    private int detailWordScore;        //详细内容字数得分

    private int detailImages;           //详细内容图片得分

    public int getHasSupCat() {
        return hasSupCat;
    }

    public void setHasSupCat(int hasSupCat) {
        this.hasSupCat = hasSupCat;
    }

    public int getParamAmount() {
        return paramAmount;
    }

    public void setParamAmount(int paramAmount) {
        this.paramAmount = paramAmount;
    }

    public int getRepetitorAmount() {
        return repetitorAmount;
    }

    public void setRepetitorAmount(int repetitorAmount) {
        this.repetitorAmount = repetitorAmount;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public int getHasOtherTitleDetail() {
        return hasOtherTitleDetail;
    }

    public void setHasOtherTitleDetail(int hasOtherTitleDetail) {
        this.hasOtherTitleDetail = hasOtherTitleDetail;
    }

    public String getIsSupportTrade() {
        return isSupportTrade;
    }

    public void setIsSupportTrade(String isSupportTrade) {
        this.isSupportTrade = isSupportTrade;
    }

    public int getPriceType() {
        return priceType;
    }

    public void setPriceType(int priceType) {
        this.priceType = priceType;
    }

    public int getPriceItemCount() {
        return priceItemCount;
    }

    public void setPriceItemCount(int priceItemCount) {
        this.priceItemCount = priceItemCount;
    }

    public int getPriceTypeNew() {
        return priceTypeNew;
    }

    public void setPriceTypeNew(int priceTypeNew) {
        this.priceTypeNew = priceTypeNew;
    }

    public int getImageAmount() {
        return imageAmount;
    }

    public void setImageAmount(int imageAmount) {
        this.imageAmount = imageAmount;
    }

    public int getFirstInmageSize() {
        return firstInmageSize;
    }

    public void setFirstInmageSize(int firstInmageSize) {
        this.firstInmageSize = firstInmageSize;
    }

    public int getDetailWordAmount() {
        return detailWordAmount;
    }

    public void setDetailWordAmount(int detailWordAmount) {
        this.detailWordAmount = detailWordAmount;
    }

    public int getDetailWordAmountType() {
        return detailWordAmountType;
    }

    public void setDetailWordAmountType(int detailWordAmountType) {
        this.detailWordAmountType = detailWordAmountType;
    }

    public String getDetailImageType() {
        return detailImageType;
    }

    public void setDetailImageType(String detailImageType) {
        this.detailImageType = detailImageType;
    }

    public int getThanSevenHundred() {
        return thanSevenHundred;
    }

    public void setThanSevenHundred(int thanSevenHundred) {
        this.thanSevenHundred = thanSevenHundred;
    }

    public int getFiveBetweenSeven() {
        return fiveBetweenSeven;
    }

    public void setFiveBetweenSeven(int fiveBetweenSeven) {
        this.fiveBetweenSeven = fiveBetweenSeven;
    }

    public int getThreeBetweenFive() {
        return threeBetweenFive;
    }

    public void setThreeBetweenFive(int threeBetweenFive) {
        this.threeBetweenFive = threeBetweenFive;
    }

    public int getLessThreeHundred() {
        return lessThreeHundred;
    }

    public void setLessThreeHundred(int lessThreeHundred) {
        this.lessThreeHundred = lessThreeHundred;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getHasSupCatScore() {
        return hasSupCatScore;
    }

    public void setHasSupCatScore(int hasSupCatScore) {
        this.hasSupCatScore = hasSupCatScore;
    }

    public int getHasnoParam() {
        return hasnoParam;
    }

    public void setHasnoParam(int hasnoParam) {
        this.hasnoParam = hasnoParam;
    }

    public int getHaslongtitle() {
        return haslongtitle;
    }

    public void setHaslongtitle(int haslongtitle) {
        this.haslongtitle = haslongtitle;
    }

    public int getHasPrice() {
        return hasPrice;
    }

    public void setHasPrice(int hasPrice) {
        this.hasPrice = hasPrice;
    }

    public int getPhotoCount() {
        return photoCount;
    }

    public void setPhotoCount(int photoCount) {
        this.photoCount = photoCount;
    }

    public int getFirstPhoto() {
        return firstPhoto;
    }

    public void setFirstPhoto(int firstPhoto) {
        this.firstPhoto = firstPhoto;
    }

    public int getDetailWordScore() {
        return detailWordScore;
    }

    public void setDetailWordScore(int detailWordScore) {
        this.detailWordScore = detailWordScore;
    }

    public int getDetailImages() {
        return detailImages;
    }

    public void setDetailImages(int detailImages) {
        this.detailImages = detailImages;
    }
}
