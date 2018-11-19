package org.spring.learning.core.ioc.chapter1;


import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class BookDTO implements Serializable{

    private int id;
    private String name;
    //private String author;
    private BigDecimal price;
    private String publishingName;

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    /*public void setAuthor(String author){
        this.author = author;
    }

    //public String getAuthor(){
        return author;
    }*/

    public void setPublishingName(String publishingName){
        this.publishingName = publishingName;
    }

    public String getPublishingName(){
        return publishingName;
    }

    public void setPrice(float price){
        this.price = new BigDecimal(price);
    }

    public void setMoreAccuratePrice(BigDecimal price){

        try{
            this.price = price;
        }
        catch(NumberFormatException e){ }
    }

    public float getPrice(){
        return price != null
                ? price.setScale(2, RoundingMode.HALF_DOWN).floatValue()
                : 0.0f;
    }

    public BigDecimal getMoreAccuratePrice(){
        return price;
    }

    public String toString(){
        return String.format("[%d. %s, publishing: %s, price = %s]", id, name,
                publishingName != null ? publishingName : "-"
                /*author != null ? author : "-"*/,
                price != null ? getPrice()+"€" : "-");
    }
}
