package com.example.eng_mahnoud83coffey.embeatit.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.example.eng_mahnoud83coffey.embeatit.Model.Order;
import com.google.firebase.database.Query;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

// كلاس قاعده البيانات تخزين الطلبات مؤقتا ل (عرضها للمستخدم ,و ثم ارسالها بعد ذالك للFirebase )
public class Database extends SQLiteAssetHelper
{

    private static final String DB_NAME="BEatDB.db";
    private static final int    DB_VER=1;






    public Database(Context context)
    {
        super(context, DB_NAME,null, DB_VER);


    }




    public List<Order> getCarts() {

        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelect = {"ProudactID", "ProudactName", "Quantity", "Price", "Discount"};

        qb.setTables("OrderDetails");
        Cursor c = qb.query(db, sqlSelect, null, null, null, null,null);

        List<Order> result = new ArrayList<>();

        if (c.moveToFirst()) {
            do {
                result.add(new Order(c.getString(c.getColumnIndex("ProudactID")),
                        c.getString(c.getColumnIndex("ProudactName")),
                        c.getString(c.getColumnIndex("Quantity")),
                        c.getString(c.getColumnIndex("Price")),
                        c.getString(c.getColumnIndex("Discount"))
                ));
            } while (c.moveToNext());
        }

        return result;
    }




    //Clear Data order From Sqlite
    public void cleanCart()
    {
        SQLiteDatabase db=getReadableDatabase();

        String Query=String.format("DELETE FROM OrderDetails");

        db.execSQL(Query);

    }





    //Storage Data order in Sqlite
    public void addToCarts(Order order)
    {
        SQLiteDatabase db=getReadableDatabase();

        String Query=String.format("INSERT INTO OrderDetails (ProudactID,ProudactName,Quantity,Price,Discount) VALUES ('%s','%s','%s','%s','%s');",
                                                              order.getProudactID(),
                                                              order.getProudactName(),
                                                              order.getQuentity(),
                                                              order.getPrice(),
                                                              order.getDiscount()
                                                                   );

        db.execSQL(Query);



    }




    //Add Food Favorites
    public void addToFavorites(String foodId)
    {

        SQLiteDatabase db=getReadableDatabase();

        String query=String.format("INSERT INTO Favorites(foodId)VALUES('%s');",foodId);

        db.execSQL(query);
    }


    //Remove Food Favorites
    public void removeFavorites(String foodId)
    {

        SQLiteDatabase db=getReadableDatabase();

        String query=String.format("DELETE FROM Favorites WHERE foodId='%s';",foodId);

        db.execSQL(query);

    }


    //Check Food DO add Favorites OR Not
    public boolean isFoodFavorites(String foodId)
    {

        SQLiteDatabase db=getReadableDatabase();

         String query=String.format("SELECT * FROM Favorites WHERE foodId='%s' ;",foodId);

       // String q = "SELECT * FROM Favorites WHERE _id = " + foodId ;

        Cursor cursor=db.rawQuery(query,null);

        if (cursor.getCount()<=0)
        {
            cursor.close();
            return false;
        }

      return true;
    }


    public int getCountCart() {

        int count=0;

        SQLiteDatabase db=getReadableDatabase();

        String query=String.format("SELECT COUNT(*) FROM OrderDetails");

        Cursor cursor=db.rawQuery(query,null);

        if (cursor.moveToFirst())
        {
            do {

                count=cursor.getInt(0);

            }while (cursor.moveToNext());
        }
        return count;
    }
}
