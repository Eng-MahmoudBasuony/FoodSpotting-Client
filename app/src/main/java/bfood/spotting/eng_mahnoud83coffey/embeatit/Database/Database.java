package bfood.spotting.eng_mahnoud83coffey.embeatit.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import bfood.spotting.eng_mahnoud83coffey.embeatit.Model.Favorites;
import bfood.spotting.eng_mahnoud83coffey.embeatit.Model.Order;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

// كلاس قاعده البيانات تخزين الطلبات مؤقتا ل (عرضها للمستخدم ,و ثم ارسالها بعد ذالك للFirebase )
public class Database extends SQLiteAssetHelper
{

    private static final String DB_NAME="BEatDB.db";
    private static final int    DB_VER=2;


    public Database(Context context)
    {
        super(context, DB_NAME,null, DB_VER);
    }

    public List<Order> getCarts(String UserPhone) {

        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelect = {"UserPhone","ProudactID", "ProudactName", "Quantity", "Price", "Discount","Image"};

        qb.setTables("OrderDetails");
        Cursor c = qb.query(db, sqlSelect, "UserPhone=?", new String[]{UserPhone}, null, null,null);

        List<Order> result = new ArrayList<>();

        if (c.moveToFirst()) {
            do {
                result.add(new Order(
                        c.getString(c.getColumnIndex("UserPhone")),
                        c.getString(c.getColumnIndex("ProudactID")),
                        c.getString(c.getColumnIndex("ProudactName")),
                        c.getString(c.getColumnIndex("Quantity")),
                        c.getString(c.getColumnIndex("Price")),
                        c.getString(c.getColumnIndex("Discount")),
                        c.getString(c.getColumnIndex("Image"))

                ));
            } while (c.moveToNext());
        }

        return result;
    }


     //فائده الميثود لو الطلب اللى اضفته موجود يبقى مش هضيفه تانى هزود فى كميه بس
     //This Function Check If Exist ProductId in Database ,Just increase
    public boolean checkFoodExists(String foodId,String UserPhone)
    {
        boolean flag=false;

        SQLiteDatabase db=getReadableDatabase();

        Cursor cursor=null;
        String sqlQuery=String.format("SELECT * FROM OrderDetails WHERE ProudactID='%s' and UserPhone='%s'",foodId,UserPhone);

        cursor=db.rawQuery(sqlQuery,null);

        if (cursor.getCount()>0)
            flag=true;
        else
            flag=false;

        cursor.close();

        return flag;
    }

    //Clear Data order From Sqlite
    public void cleanCart(String UserPhone)
    {
        SQLiteDatabase db=getReadableDatabase();

        String Query=String.format("DELETE FROM OrderDetails WHERE UserPhone='%s'",UserPhone);

        db.execSQL(Query);

    }

    //Storage Data order in Sqlite
    public void addToCarts(Order order)
    {
        SQLiteDatabase db=getReadableDatabase();

        String Query=String.format("INSERT OR REPLACE INTO OrderDetails (UserPhone,ProudactID,ProudactName,Quantity,Price,Discount,Image) VALUES ('%s','%s','%s','%s','%s','%s','%s');",
                                                              order.getUserPhone(),
                                                              order.getProudactID(),
                                                              order.getProudactName(),
                                                              order.getQuentity(),
                                                              order.getPrice(),
                                                              order.getDiscount() ,
                                                              order.getImage()
                                                                   );
        db.execSQL(Query);
    }


    public int getCountCart(String UserPhone) {

        int count=0;

        SQLiteDatabase db=getReadableDatabase();

        String query=String.format("SELECT COUNT(*) FROM OrderDetails WHERE UserPhone='%s'",UserPhone);

        Cursor cursor=db.rawQuery(query,null);

        if (cursor.moveToFirst())
        {
            do {

                count=cursor.getInt(0);

            }while (cursor.moveToNext());
        }
        return count;
    }

    //Update Quentity from Button Number
    public void updateCart(Order order)
    {
        SQLiteDatabase db=getReadableDatabase();

        String query=String.format("UPDATE OrderDetails SET Quantity='%s' WHERE UserPhone='%s' AND ProudactID='%s' ",order.getQuentity(),order.getUserPhone(),order.getProudactID());

        db.execSQL(query);
    }


    //  increase Quentity
    public void increaseCart(String foodId,String UserPhone)
    {
        SQLiteDatabase db=getReadableDatabase();

        String query=String.format("UPDATE OrderDetails SET Quantity= Quantity+1  WHERE UserPhone='%s' AND ProudactID='%s' ",UserPhone,foodId);

        db.execSQL(query);
    }



    public void removeFromCart(String proudactID, String phone)
    {

        SQLiteDatabase db=getReadableDatabase();

        String Query=String.format("DELETE FROM OrderDetails WHERE UserPhone='%s' AND ProudactID='%s' ",phone,proudactID);

        db.execSQL(Query);
    }

    //Remove Food Favorites
    public void removeFavorites(String foodId,String userPhone)
    {

        SQLiteDatabase db=getReadableDatabase();

        String query=String.format("DELETE FROM Favorites WHERE foodId='%s' and userPhone='%s';",foodId,userPhone);

        db.execSQL(query);

    }

    //Check Food DO add Favorites OR Not
    public boolean isFoodFavorites(String foodId,String userPhone)
    {

        SQLiteDatabase db=getReadableDatabase();

         String query=String.format("SELECT * FROM Favorites WHERE foodId='%s' and userPhone='%s';",foodId,userPhone);

       // String q = "SELECT * FROM Favorites WHERE _id = " + foodId ;

        Cursor cursor=db.rawQuery(query,null);

        if (cursor.getCount()<=0)
        {
            cursor.close();
            return false;
        }

      return true;
    }
    //Add Food Favorites
    public void addToFavorites(Favorites food)
    {

        SQLiteDatabase db=getReadableDatabase();

        String query=String.format("INSERT INTO Favorites(foodId,foodName,foodPrice,foodMenuId,foodImage,foodDiscount,foodDescription,userPhone)VALUES('%s','%s','%s','%s','%s','%s','%s','%s');"
                ,food.getFoodId(),food.getFoodName(),food.getFoodPrice(),food.getFoodMenuId(),food.getFoodImage(),food.getFoodDiscount(),food.getFoodDescription(),food.getUserPhone());

        db.execSQL(query);
    }

    public List<Favorites> getAllFavorites(String UserPhone)
    {

        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelect = {"foodId","foodName", "foodPrice", "foodMenuId", "foodImage", "foodDiscount","foodDescription","userPhone"};

        qb.setTables("Favorites");
        Cursor c = qb.query(db, sqlSelect, "UserPhone=?", new String[]{UserPhone}, null, null,null);

        List<Favorites> result = new ArrayList<>();

        if (c.moveToFirst()) {
            do {
                result.add(new Favorites(
                        c.getString(c.getColumnIndex("foodId")),
                        c.getString(c.getColumnIndex("foodName")),
                        c.getString(c.getColumnIndex("foodPrice")),
                        c.getString(c.getColumnIndex("foodMenuId")),
                        c.getString(c.getColumnIndex("foodImage")),
                        c.getString(c.getColumnIndex("foodDiscount")),
                        c.getString(c.getColumnIndex("foodDescription")),
                        c.getString(c.getColumnIndex("userPhone"))


                ));
            } while (c.moveToNext());
        }

        return result;
    }




}
