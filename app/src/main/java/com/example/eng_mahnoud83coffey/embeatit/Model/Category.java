package com.example.eng_mahnoud83coffey.embeatit.Model;


//مودل كلاس لفئات الاطعمه اللى هيا عناوين رئيسيه
public class Category {

    private String Name;
    private String Image;
    private String Id;


    public Category() {
    }


    public Category(String name, String image, String id) {
        Name = name;
        Image = image;
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }


}