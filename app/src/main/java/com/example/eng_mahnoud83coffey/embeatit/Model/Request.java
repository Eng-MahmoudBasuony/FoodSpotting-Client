package com.example.eng_mahnoud83coffey.embeatit.Model;

import java.util.List;

 //مودل كلاس اللى بشيل فيه الطلبات عشان ابعتها للFirebase
//Cart and Submit Order to Firebase and get Firebase
public class Request {
     private String phone;
     private String name;
     private String address;
     private String total;
     private String status;
     private List<Order> foods; //List of food Order
     private String comment;


     public Request() {
     }


     public Request(String phone, String name, String address, String total, String status, List<Order> foods, String comment) {
         this.phone = phone;
         this.name = name;
         this.address = address;
         this.total = total;
         this.status = status;
         this.foods = foods;
         this.comment = comment;
     }


     public String getPhone() {
         return phone;
     }

     public void setPhone(String phone) {
         this.phone = phone;
     }

     public String getName() {
         return name;
     }

     public void setName(String name) {
         this.name = name;
     }

     public String getAddress() {
         return address;
     }

     public void setAddress(String address) {
         this.address = address;
     }

     public String getTotal() {
         return total;
     }

     public void setTotal(String total) {
         this.total = total;
     }

     public String getStatus() {
         return status;
     }

     public void setStatus(String status) {
         this.status = status;
     }

     public List<Order> getFoods() {
         return foods;
     }

     public void setFoods(List<Order> foods) {
         this.foods = foods;
     }

     public String getComment() {
         return comment;
     }

     public void setComment(String comment) {
         this.comment = comment;
     }
 }