package bfood.spotting.eng_mahnoud83coffey.embeatit.Model;

public class Rating
{

    private String userPhone;
    private String foodId;
    private String rating;
    private String comment;

    public Rating() {
    }


    public Rating(String userPhone, String foodId, String rating, String comment) {
        this.userPhone = userPhone;
        this.foodId = foodId;
        this.rating = rating;
        this.comment = comment;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
