package com.example.eng_mahnoud83coffey.embeatit;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.eng_mahnoud83coffey.embeatit.Common.Common;
import com.example.eng_mahnoud83coffey.embeatit.Common.Config;
import com.example.eng_mahnoud83coffey.embeatit.Database.Database;
import com.example.eng_mahnoud83coffey.embeatit.Model.MyResponse;
import com.example.eng_mahnoud83coffey.embeatit.Model.Notification;
import com.example.eng_mahnoud83coffey.embeatit.Model.Order;
import com.example.eng_mahnoud83coffey.embeatit.Model.Request;
import com.example.eng_mahnoud83coffey.embeatit.Model.Sender;
import com.example.eng_mahnoud83coffey.embeatit.Model.Token;
import com.example.eng_mahnoud83coffey.embeatit.Remote.ApiService;
import com.example.eng_mahnoud83coffey.embeatit.ViewHolder.CartAdabter;
import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


//الكلاس المسؤل عن عرض الطلبات قبل ارسالها للFirebase للمستخدم فى Recyclerview ومن ثم ارسالها للFirebase
public class Cart extends AppCompatActivity {

    private static final int PAYPAL_REQUEST_CODE=9999;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    public TextView textTotalPrice;
    private Button   buttonPlaceOrder;
    //-------------------------------
    private FirebaseDatabase database;
    private DatabaseReference requestesToFirebase;
    //-------------------------------
    private List<Order> cartList=new ArrayList<Order>();
    private CartAdabter cartAdabter;
    //-----------------------------
    private ApiService mApiService;

    //paypal payment
    static PayPalConfiguration config=new PayPalConfiguration()
                               .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX) //use sandbox becaue we change it late if you to production
                               .clientId(Config.Paypal_Client_ID);

    String address,comment;


    //Library Custom font
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

  //First الكلاس ده فايدته هيجلب البيانات المتخزنه فى الاس كيولايت عشان يعرضها فى الريسيكلر فيو
  //Seconed تانى وظيفه هجلب البيانات من الاس كيولايت وهبعتها لل الفيربيز
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //Library Custom font
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/restaurant_font.otf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );

        setContentView(R.layout.activity_cart);

        //----------------Id-------------------------//
        recyclerView=(RecyclerView)findViewById(R.id.recyclerView_ListCart);
        textTotalPrice=(TextView)findViewById(R.id.textview_Cart_TotalPrice);
        buttonPlaceOrder=(Button)findViewById(R.id.btn_cart_place_order);


        //Init PayPal
        Intent intent=new Intent(this, PayPalService.class);
               intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
        startService(intent);



        //Init Service
        mApiService=Common.getFCMClinet();

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

                 if (cartList.size()>0)
                 {
                     showAlertDialog();

                 }else
                     {
                         Toast.makeText(Cart.this, "Your Carts is empty", Toast.LENGTH_SHORT).show();
                     }
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
        cartAdabter.notifyDataSetChanged();
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

                            alertDailog.setIcon(R.drawable.shopping_cart_black_24dp);

                            /*
        // Add Edit text to Alert Dialog programing
        final EditText editTextAddress=new EditText(Cart.this);
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                                   LinearLayout.LayoutParams.MATCH_PARENT );
        editTextAddress.setLayoutParams(lp);
        alertDailog.setView(editTextAddress); //Add Edit text to Alert Dialog
                       */

        LayoutInflater layoutInflater=this.getLayoutInflater();
        View viewInflater=layoutInflater.inflate(R.layout.order_adress_comment_dialog,null);


        final TextInputEditText editAdress=(TextInputEditText)viewInflater.findViewById(R.id.adress_dialog_cart);
        final TextInputEditText editComment=(TextInputEditText)viewInflater.findViewById(R.id.comment_dialog_cart);

        alertDailog.setView(viewInflater);

        //choose Done
        alertDailog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {


                //Show PayPal to Payment


                //First get Address ,Comment from AlertDialog
               address=editAdress.getText().toString();
               comment=editComment.getText().toString();

               String formatAccount=textTotalPrice.getText().toString()
                                                                     .replace("$","")
                                                                     .replace(",","");

                PayPalPayment payPalPayment= new PayPalPayment(new BigDecimal(formatAccount),
                                                                           "USD",
                                                                           "Basuony Order Food",
                                                                            PayPalPayment.PAYMENT_INTENT_SALE);

                Intent intent=new Intent(getApplicationContext(), PaymentActivity.class);

                      intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
                      intent.putExtra(PaymentActivity.EXTRA_PAYMENT,payPalPayment);

                startActivityForResult(intent,PAYPAL_REQUEST_CODE);


               /*   //ده المودل اللى هشيل فيه الطلبات
                Request request=new Request(Common.currentUser.getPhone(),
                                            Common.currentUser.getName(),
                                            editAdress.getText().toString(),
                                            textTotalPrice.getText().toString(),
                                               "0",
                                            cartList//الاطعمه
                                               ,
                                            editComment.getText().toString()
                                             );
                //we will using current.Millis to Key
                String orderNumber=String.valueOf(System.currentTimeMillis());
                 //Submit to Firebase
                 requestesToFirebase.child(orderNumber).
                                     setValue(request);
                 //بعد ما ابعت البيانات للفيربيز وتتخزن همسحها من SQlite
                //Delete From Cart to SQlite
                new Database(getBaseContext()).cleanCart();
                sendNotificationOrder(orderNumber);
               Toast.makeText(Cart.this, "Thank you ,Order Place ", Toast.LENGTH_SHORT).show();
                finish();
*/



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

    private void sendNotificationOrder(final String orderNumber)
    {
      final DatabaseReference referenceTokens=FirebaseDatabase.getInstance().getReference("Tokens");

          Query queryData =referenceTokens.orderByChild("serverToken").equalTo(true);// get All node isServerToken is True


       queryData.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot)
           {

               for (DataSnapshot snapshot:dataSnapshot.getChildren())
               {
                   Token serverToken=snapshot.getValue(Token.class);

                    //Create Raw payload to send
                   Notification notification=new Notification("EMB","You have new order"+orderNumber);

                   Sender content=new Sender(serverToken.getToken(),notification);

                   mApiService.sendNotification(content).enqueue(new Callback<MyResponse>()
                         {
                       @Override
                       public void onResponse(Call<MyResponse> call, Response<MyResponse> response)
                       {

                              //
                           if (response.code()==200) {
                               if (response.body().success == 1) {
                                   Toast.makeText(Cart.this, "Thank you ,Order Place ", Toast.LENGTH_SHORT).show();
                                   finish();
                               } else {
                                   Toast.makeText(Cart.this, "Failed !!! ", Toast.LENGTH_SHORT).show();

                               }

                           }

                           }

                       @Override
                       public void onFailure(Call<MyResponse> call, Throwable t)
                       {
                           Log.e("Error ", t.getMessage() );
                       }
                   });
               }


           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });

    }


    @Override
    public boolean onContextItemSelected(MenuItem item)
    {


        if (item.getTitle().equals(Common.DELETE))
        {
            deleteCart(item.getOrder());
        }


        return true;
    }

    private void deleteCart(int position)
    {
      //We Will Remove item List<Order> by Position
        cartList.remove(position);

      //After That , we will DELETE ALL old data from Sqlite
        new Database(this).cleanCart();

      //And final , we Will Update New Data From List<Order>CartList to SQlite
        for (Order item :cartList)
                    new Database(this).addToCarts(item);

        //Referesh
        loadListFood();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {

          //Buy using paypal
        if (requestCode==PAYPAL_REQUEST_CODE)
        {
            if (resultCode==RESULT_OK)
            {

           PaymentConfirmation paymentConfirmation=data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

                     if (paymentConfirmation != null)
                     {
                         try {
                             String paymentDetail=paymentConfirmation.toJSONObject().toString(4);

                             JSONObject jsonObject=new JSONObject(paymentDetail);

                                //ده المودل اللى هشيل فيه الطلبات
                Request request=new Request(Common.currentUser.getPhone(),
                                            Common.currentUser.getName(),
                                            address,
                                            textTotalPrice.getText().toString(),
                                               "0",
                                            cartList//الاطعمه
                                               ,
                                            comment,
                                            jsonObject.getJSONObject("response").getString("state") //State Pay
                                             );
                //we will using current.Millis to Key
                String orderNumber=String.valueOf(System.currentTimeMillis());
                 //Submit to Firebase
                 requestesToFirebase.child(orderNumber).
                                     setValue(request);
                 //بعد ما ابعت البيانات للفيربيز وتتخزن همسحها من SQlite
                //Delete From Cart to SQlite
                new Database(getBaseContext()).cleanCart();
                sendNotificationOrder(orderNumber);

                Toast.makeText(Cart.this, "Thank you ,Order Place ", Toast.LENGTH_SHORT).show();
                finish();

                         } catch (JSONException e) {
                             e.printStackTrace();
                         }

                     }

            }else if (resultCode== Activity.RESULT_CANCELED)
            {
                Toast.makeText(this, "payment Cancel", Toast.LENGTH_SHORT).show();
            }else if (resultCode==PaymentActivity.RESULT_EXTRAS_INVALID)
            {
                Toast.makeText(this, "Invalid Payment", Toast.LENGTH_SHORT).show();
            }
        }





    }
}
