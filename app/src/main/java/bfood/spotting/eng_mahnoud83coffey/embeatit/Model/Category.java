package bfood.spotting.eng_mahnoud83coffey.embeatit.Model;


//مودل كلاس لفئات الاطعمه اللى هيا عناوين رئيسيه
public class Category {
    private String Name;
    private String Image;
    private String MenuId;

    public Category() {
    }


    public Category(String name, String image) {
        Name = name;
        Image = image;
    }

    public Category(String name, String image, String menuId) {
        Name = name;
        Image = image;
        MenuId = menuId;
    }

    public String getMenuId() {
        return MenuId;
    }

    public void setMenuId(String menuId) {
        MenuId = menuId;
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


}