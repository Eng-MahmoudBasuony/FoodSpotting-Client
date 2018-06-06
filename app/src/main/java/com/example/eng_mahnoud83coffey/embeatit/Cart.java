package com.example.eng_mahnoud83coffey.embeatit;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.eng_mahnoud83coffey.embeatit.Common.Common;
import com.example.eng_mahnoud83coffey.embeatit.Database.Database;
import com.example.eng_mahnoud83coffey.embeatit.Model.Order;
import com.example.eng_mahnoud83coffey.embeatit.Model.Request;
import com.example.eng_mahnoud83coffey.embeatit.ViewHolder.CartAdabter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


//الكلاس المسؤل عن عرض الطلبات قبل ارسالها للFirebase للمستخدم فى Recyclerview ومن ثم ارسالها للFirebase
public class Cart extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private TextView textTotalPrice;
    private Button   buttonPlaceOrder;
    //-------------------------------
    private FirebaseDatabase database;
    private DatabaseReference requestesToFirebase;
    //-------------------------------
    private List<Order> cartList=new ArrayList<Order>();
    private CartAdabter cartAdabter;






  //First الكلاس ده فايدته هيجلب البيانات المتخزنه فى الاس كيولايت عشان يعرضها فى الريسيكلر فيو
  //Seconed تانى وظيفه هجلب البيانات من الاس كيولايت وهبعتها لل الفيربيز

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);


        //----------------Id-------------------------//
        recyclerView=(RecyclerView)findViewById(R.id.recyclerView_ListCart);
        textTotalPrice=(TextView)findViewById(R.id.textview_Cart_TotalPrice);
        buttonPlaceOrder=(Button)findViewById(R.id.btn_cart_place_order);




        //----------Recycler View------------------//
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);



        //--------------------Firebase----------------//
        database=FirebaseDatabase.getInstance();
        requestesToFirebase=database.getReference("Requests");// جدول الطلبات اللى المستخدم عاوزها


        //-------------------Action------------------//
         buttonPlaceOrder.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view)
             {

                 showAlertDialog();



             }
         });



         loadListFood();
    }



    //Method Load Order from sqlite to Recyclerview
    //هتقوم  بالوظيفه الاولى وهيا ارسال الطلبات للRecyclerView  لعرضها فقط--ثم حساب السعر حسب الكميه وعرضه للمستخدم
    private void loadListFood()
    {
         cartList=new Database(this).getCarts();// get Data From Sqlite

       cartAdabter=new CartAdabter(cartList,this);//Adabter
        recyclerView.setAdapter(cartAdabter);


       //Calculate Total Price
        int total=0;

        for (Order order:cartList)
        {
            total+=(Integer.parseInt(order.getPrice()))*(Integer.parseInt(order.getQuentity()));

            Locale locale=new Locale("en","US");
            NumberFormat fmt=NumberFormat.getInstance(locale);

            textTotalPrice.setText(fmt.format(total)); //Send Price

        }


    }


    //Method Show Dialog
    // هتظهر AlertDialog  تدخل فيها عنوانك ثم يتم تخزينها فى جدول الطلبات فى الFirebae مع الاطعمه المختاره
    private void showAlertDialog()
    {
        AlertDialog.Builder alertDailog=new AlertDialog.Builder(Cart.this);
                            alertDailog.setTitle("One More Step !");
                            alertDailog.setMessage("Enter Your Address : ");


        final EditText editTextAddress=new EditText(Cart.this);
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                                   LinearLayout.LayoutParams.MATCH_PARENT );
        editTextAddress.setLayoutParams(lp);
        alertDailog.setView(editTextAddress); //Add Edit text to Alert Dialog


        alertDailog.setIcon(R.drawable.shopping_cart_black_24dp);

            //choose Done
        alertDailog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                 //ده المودل اللى هشيل فيه الطلبات
                Request request=new Request(Common.currentUser.getPhone(),
                                            Common.currentUser.getName(),
                                            editTextAddress.getText().toString(),
                                            textTotalPrice.getText().toString(),
                                            cartList//الاطعمه
                                             );


                //Submit to Firebase
                //we will using current.Millis to Key
                 requestesToFirebase.child(String.valueOf(System.currentTimeMillis())).
                                     setValue(request);

                 //بعد ما ابعت البيانات للفيربيز وتتخزن همسحها من SQlite
                //Delete From Cart to SQlite
                new Database(getBaseContext()).cleanCart();

                Toast.makeText(Cart.this, "Thank you ,Order Place ", Toast.LENGTH_SHORT).show();

                finish();

            }
        });


        //chose No
        alertDailog.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {

                dialogInterface.dismiss();
            }
        });


        alertDailog.show();
    }




}
