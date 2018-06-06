package com.example.eng_mahnoud83coffey.embeatit.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.example.eng_mahnoud83coffey.embeatit.Model.Order;
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



}
