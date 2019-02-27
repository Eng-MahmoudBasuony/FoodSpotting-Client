package bfood.spotting.eng_mahnoud83coffey.embeatit.Model;

//مودل كلاس هشيل فيه الطلبات اللى المستخدم بيختارها عشان اخزنها فى الSQlite
public class Order
{
    private String UserPhone;
    private String ProudactID;
    private String ProudactName;
    private String Quentity;
    private String Price;
    private String Discount;
    private String Image;


    public Order() {
    }


    public Order(String userPhone, String proudactID, String proudactName, String quentity, String price, String discount, String image) {
        UserPhone = userPhone;
        ProudactID = proudactID;
        ProudactName = proudactName;
        Quentity = quentity;
        Price = price;
        Discount = discount;
        Image = image;
    }

    public String getUserPhone() {
        return UserPhone;
    }

    public void setUserPhone(String userPhone) {
        UserPhone = userPhone;
    }

    public String getProudactID() {
        return ProudactID;
    }

    public void setProudactID(String proudactID) {
        ProudactID = proudactID;
    }

    public String getProudactName() {
        return ProudactName;
    }

    public void setProudactName(String proudactName) {
        ProudactName = proudactName;
    }

    public String getQuentity() {
        return Quentity;
    }

    public void setQuentity(String quentity) {
        Quentity = quentity;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }
}
