package bfood.spotting.eng_mahnoud83coffey.embeatit;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import bfood.spotting.eng_mahnoud83coffey.embeatit.Common.Common;
import bfood.spotting.eng_mahnoud83coffey.embeatit.Common.Config;
import bfood.spotting.eng_mahnoud83coffey.embeatit.Database.Database;
import bfood.spotting.eng_mahnoud83coffey.embeatit.Helper.GpsTracker;
import bfood.spotting.eng_mahnoud83coffey.embeatit.Helper.RecyclerItemTouchHeleper;
import bfood.spotting.eng_mahnoud83coffey.embeatit.Interface.RecyclerItemTouchHleperListiner;
import bfood.spotting.eng_mahnoud83coffey.embeatit.Model.DataMessage;
import bfood.spotting.eng_mahnoud83coffey.embeatit.Model.MyResponse;
import bfood.spotting.eng_mahnoud83coffey.embeatit.Model.Order;
import bfood.spotting.eng_mahnoud83coffey.embeatit.Model.Request;
import bfood.spotting.eng_mahnoud83coffey.embeatit.Model.Token;
import bfood.spotting.eng_mahnoud83coffey.embeatit.Model.User;



import bfood.spotting.eng_mahnoud83coffey.embeatit.Remote.ApiService;
import bfood.spotting.eng_mahnoud83coffey.embeatit.Remote.IGoogleServicecs;
import bfood.spotting.eng_mahnoud83coffey.embeatit.ViewHolder.CartAdabter;
import bfood.spotting.eng_mahnoud83coffey.embeatit.ViewHolder.CartViewHolder;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import android.widget.RadioButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


//الكلاس المسؤل عن عرض الطلبات قبل ارسالها للFirebase للمستخدم فى Recyclerview ومن ثم ارسالها للFirebase
public class Cart extends AppCompatActivity implements RecyclerItemTouchHleperListiner {

    private static final int PAYPAL_REQUEST_CODE=9999;

    private static final int MY_PERMISSIONS_REQUEST_LOCATION =144455 ;

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

    private Place shappingAddress;
    //---------my Location-----------


    private GpsTracker gpsTracker; //Class Get GPS  and open Location

    //Decleration
    private IGoogleServicecs mIGoogleServicecs;
    // Notification
    private ApiService mApiService;


    //paypal payment
    private static PayPalConfiguration config = new PayPalConfiguration()
                                            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
                                             .clientId(Config.Paypal_Client_ID);//YOUR CLIENT ID
   private String address=null;
   private String comment=null;

   private SharedPreferences sharedPreferences;
   private SharedPreferences.Editor editor;

   private RelativeLayout rootLayoutCart;
   //------------------------------------------------


  private static double latitude;
  private static double longitude;

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
        rootLayoutCart=(RelativeLayout)findViewById(R.id.rootLayoutCart);



        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED )
            {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e){
            e.printStackTrace();
        }








        //Init PayPal
        Intent intent=new Intent(this, PayPalService.class);
               intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
        startService(intent);

        //Init Current Name Address
        mIGoogleServicecs=Common.getGoogleMapsApi();
        //Init Service Notification
        mApiService=Common.getFCMClinet();

        //Save your Location when Move to Activity --> paypal
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor=sharedPreferences.edit();

        //----------Recycler View------------------//
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //--------------------Firebase----------------//
        database=FirebaseDatabase.getInstance();
        requestesToFirebase=database.getReference("Requests");// جدول الطلبات اللى المستخدم عاوزها

        //-------------------Event------------------//
         buttonPlaceOrder.setOnClickListener(new View.OnClickListener()
         {
             @Override
             public void onClick(View view)
             {

                 gpsTracker = new GpsTracker(Cart.this);
                 if(gpsTracker.canGetLocation())
                 {
                       latitude = gpsTracker.getLatitude();
                       longitude = gpsTracker.getLongitude();
                       //---------showAlertDialog---------------------

                     if (cartList.size()>0)
                     {
                         showAlertDialog();

                     }else
                     {
                         Toast.makeText(Cart.this, "Your Carts is empty", Toast.LENGTH_SHORT).show();
                     }
                     //----------------------------------


                 }else{
                     gpsTracker.showSettingsAlert();
                 }

             }
         });

         //Swipe to delete
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback=new RecyclerItemTouchHeleper(0,ItemTouchHelper.LEFT,this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);


         loadListFood();



    }


    //Method Load Order from sqlite to Recyclerview
    //هتقوم  بالوظيفه الاولى وهيا ارسال الطلبات للRecyclerView  لعرضها فقط--ثم حساب السعر حسب الكميه وعرضه للمستخدم
    private void loadListFood()
    {
         cartList=new Database(this).getCarts(Common.currentUser.getPhone());// get Data From Sqlite

        cartAdabter=new CartAdabter(cartList,Cart.this);//Adabter
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
        final Dialog dialog=new Dialog(this);
                     dialog.setCancelable(false);
/*
        AlertDialog.Builder alertDailog=new AlertDialog.Builder(Cart.this);
                            alertDailog.setTitle("One More Step !");
                            alertDailog.setMessage("Enter Your Address : ");

                            alertDailog.setIcon(R.drawable.shopping_cart_black_24dp);

        LayoutInflater layoutInflater=this.getLayoutInflater();
        View viewInflater=layoutInflater.inflate(R.layout.order_adress_comment_dialog,null);
*/

        dialog.setContentView(R.layout.order_adress_comment_dialog);


        // final TextInputEditText editAdress=(TextInputEditText)viewInflater.findViewById(R.id.adress_dialog_cart);
        final TextInputEditText editComment=(TextInputEditText)dialog.findViewById(R.id.comment_dialog_cart);

        //Radio
        final RadioButton radioShipToAddress=(RadioButton)dialog.findViewById(R.id.radio_shipaddress);
        final RadioButton radioHomeAddress=(RadioButton)dialog.findViewById(R.id.radio_home_address);
        final RadioButton radioPaymentCash=(RadioButton)dialog.findViewById(R.id.radio_payment_cashOnDelivery);
        final RadioButton radioPaymentPaypal=(RadioButton)dialog.findViewById(R.id.radio_payment_PAYPAL);
        final RadioButton radioPaymentBaalance=(RadioButton)dialog.findViewById(R.id.radio_payment_Balance);
        final RadioGroup  radioGroubPayment=(RadioGroup)dialog.findViewById(R.id.radioGroub_Payment);
              Button yesButton=(Button)dialog.findViewById(R.id.button_yes_dialog_cart);
              ImageView cancelButton=(ImageView)dialog.findViewById(R.id.button_cancel_dialog_cart);
        final TextInputEditText editAddress=(TextInputEditText)dialog.findViewById(R.id.address_dialog_cart);

        //Event Home Address
        radioHomeAddress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {

                if (b)
                {
                    if (!TextUtils.isEmpty(Common.currentUser.getHomeAddress())||Common.currentUser.getHomeAddress()!=null)
                    {
                        address=Common.currentUser.getHomeAddress();
                        editAddress.setText(address);
                    }else
                        {
                            Toast.makeText(Cart.this, "Please Update Home Address", Toast.LENGTH_SHORT).show();
                        }

                }

            }
        });


         //Event Your Location Address
        radioShipToAddress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {

                if (b)//b == true
                {


                    if (latitude!=0 &&longitude!=0) {

                        Geocoder geocoder;
                        List<Address> addressesList = null;
                        geocoder = new Geocoder(Cart.this, Locale.getDefault());
                        try {

                            addressesList = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        address = addressesList.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
             /*       String city = addressesList.get(0).getLocality();
                    String state = addressesList.get(0).getAdminArea();
                    String country = addressesList.get(0).getCountryName();
                    String postalCode = addressesList.get(0).getPostalCode();
                    String knownName = addressesList.get(0).getFeatureName(); // Only if available else return NULL*/


                        //set this address to edit
                        editAddress.setText(address);

                /*    Toast.makeText(getApplicationContext(), "address"+address, Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), "city"+city, Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), "state"+state, Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), "country"+country, Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), "postalCode"+postalCode, Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), "knownName"+knownName, Toast.LENGTH_SHORT).show();*/

                    }else
                        {
                            Toast.makeText(getApplicationContext(), " Please Try Again !", Toast.LENGTH_SHORT).show();
                        }



                }
            }
        });


        //choose Done
      yesButton.setOnClickListener(new View.OnClickListener()
      {
          @Override
          public void onClick(View view)
          {

              //Add Check condition here
              //If user select address from place fragment ,just use it
              //If user select ship to this address , get Address from Location and user it
              //If user select Home Address , get Home Address Profile and use it
              if (!radioShipToAddress.isChecked() && !radioHomeAddress.isChecked()) {
                  //If both radio is not selected
                  if (shappingAddress != null) {
                      address = shappingAddress.getAddress().toString();

                  } else
                      {
                      Toast.makeText(Cart.this, "Please Enter Address or select option address", Toast.LENGTH_SHORT).show();

                      return;
                  }
              }

              if (address.isEmpty()||address.equals(" ")) {
                  Toast.makeText(Cart.this, "Please Enter Address or select option address", Toast.LENGTH_SHORT).show();



                  return;
              }

              //Show PayPal to Payment
              //First get Address ,Comment from AlertDialog
              //   address=shappingAddress.getAddress().toString();
              comment = editComment.getText().toString();

              //Check payment
              if (!radioPaymentCash.isChecked()&&!radioPaymentPaypal.isChecked()&&!radioPaymentBaalance.isChecked()) //If both cash and Paypal and Balance is not Chicked
              {
                  Toast.makeText(Cart.this, "Please Select Payment Option !", Toast.LENGTH_LONG).show();


                  return;
              }
              else if (radioPaymentPaypal.isChecked())
              {

                  String formatAccount = textTotalPrice.getText().toString()
                          .replace("$", "")
                          .replace(",", "");
                  //Payment method
                  Intent serviceConfig = new Intent(getApplicationContext(), PayPalService.class);
                  serviceConfig.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
                  startService(serviceConfig);


                  PayPalPayment payment = new PayPalPayment(new BigDecimal(formatAccount),//Price
                          "USD",
                          "My Awesome Item",
                          PayPalPayment.PAYMENT_INTENT_SALE);

                  Intent paymentConfig = new Intent(getApplicationContext(), PaymentActivity.class);
                  paymentConfig.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
                  paymentConfig.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
                  startActivityForResult(paymentConfig, PAYPAL_REQUEST_CODE);


              }else if (radioPaymentCash.isChecked())
              {

                  //ده المودل اللى هشيل فيه الطلبات
                  Request request=new Request(Common.currentUser.getPhone(),
                          Common.currentUser.getName(),
                          address, //Name Place
                          textTotalPrice.getText().toString(),
                          "0",
                          cartList//الاطعمه
                          ,
                          String.format("%s,%s",latitude,longitude)// خطوط الطول والعرض لمكان العميل //shappingAddress.getLatLng().longitude
                          ,
                          comment
                          ,
                          "Cash",
                          "Unpaid" //State Pay
                          ,""
                  );
                  //we will using current.Millis to Key
                  String orderNumber=String.valueOf(System.currentTimeMillis());
                  //Submit to Firebase
                  requestesToFirebase.child(orderNumber).
                          setValue(request);
                  //بعد ما ابعت البيانات للفيربيز وتتخزن همسحها من SQlite
                  //Delete From Cart to SQlite
                  new Database(getBaseContext()).cleanCart(Common.currentUser.getPhone());
                  sendNotificationOrder(orderNumber);

                  Toast.makeText(Cart.this, "Thank you ,Order Place ", Toast.LENGTH_SHORT).show();
                  finish();
              }
              else if (radioPaymentBaalance.isChecked())
              {
                  double amount=0;

                  //First we  will get total price from textTotalprice
                  try
                  {
                      amount=Common.formatCurrency(textTotalPrice.getText().toString(),Locale.US).doubleValue();
                  } catch (ParseException e) {
                      e.printStackTrace();
                  }



                  //After receive total price of this order ,just compare with User balance
                  if (Common.currentUser.getBalance()>=amount)
                  {
                      //ده المودل اللى هشيل فيه الطلبات
                      Request request=new Request(Common.currentUser.getPhone(),
                              Common.currentUser.getName(),
                              address, //Name Place
                              textTotalPrice.getText().toString(),
                              "0",
                              cartList//الاطعمه
                              ,
                              String.format("%s,%s",latitude,longitude)// خطوط الطول والعرض لمكان العميل //shappingAddress.getLatLng().longitude
                              ,
                              comment
                              ,
                              "Food Spotting Balance",
                              "paid" //State Pay
                              ,""
                      );

                      //we will using current.Millis to Key
                      final String orderNumber=String.valueOf(System.currentTimeMillis());
                      //Submit to Firebase
                      requestesToFirebase.child(orderNumber).
                              setValue(request);
                      //بعد ما ابعت البيانات للفيربيز وتتخزن همسحها من SQlite
                      //Delete From Cart to SQlite
                      new Database(getBaseContext()).cleanCart(Common.currentUser.getPhone());

                      //Update Balance
                      double blance =Common.currentUser.getBalance()-amount;

                      Map<String,Object> update_balance=new HashMap<>();
                      update_balance.put("balance",blance);

                      FirebaseDatabase.getInstance()
                              .getReference("User")
                              .child(Common.currentUser.getPhone())
                              .updateChildren(update_balance)
                              .addOnCompleteListener(new OnCompleteListener<Void>() {
                                  @Override
                                  public void onComplete(@NonNull Task<Void> task)
                                  {
                                      if (task.isSuccessful())
                                      {
                                          //Refresh User
                                          FirebaseDatabase.getInstance()
                                                  .getReference("User")
                                                  .child(Common.currentUser.getPhone())
                                                  .addListenerForSingleValueEvent(new ValueEventListener() {
                                                      @Override
                                                      public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                                      {
                                                          Common.currentUser=dataSnapshot.getValue(User.class);

                                                          //send order server
                                                          sendNotificationOrder(orderNumber);
                                                      }

                                                      @Override
                                                      public void onCancelled(@NonNull DatabaseError databaseError) {

                                                      }
                                                  });
                                      }


                                  }
                              });


                      Toast.makeText(Cart.this, "Thank you ,Order Place ", Toast.LENGTH_SHORT).show();
                      finish();

                  }
              }

          }
      });

        //chose No
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                dialog.dismiss();



            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme; //style id
        dialog.show();
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


                   Map<String,String> dataSend = new HashMap<>();
                   dataSend.put("title","Food Spotting");
                   dataSend.put("body","New Order "+orderNumber);

                DataMessage dataMessage = new DataMessage(serverToken.getToken(),dataSend);


                   mApiService.sendNotification(dataMessage).enqueue(new Callback<MyResponse>()
                         {
                       @Override
                       public void onResponse(Call<MyResponse> call, Response<MyResponse> response)
                       {
                              //
                           if (response.code()==200) {
                               if (response.body().success == 1) {
                                   Toast.makeText(Cart.this, "Than k you ,Order Place ", Toast.LENGTH_SHORT).show();
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
        new Database(this).cleanCart(Common.currentUser.getPhone());

      //And final , we Will Update New Data From List<Order>CartList to SQlite
        for (Order item :cartList)
                    new Database(this).addToCarts(item);

        //Referesh
        loadListFood();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {


        if (resultCode == Activity.RESULT_OK&&requestCode==PAYPAL_REQUEST_CODE){

            PaymentConfirmation confirm = data.getParcelableExtra(
                    PaymentActivity.EXTRA_RESULT_CONFIRMATION);

            if (confirm != null){//approved
                try {
                    Log.i("sampleapp", confirm.toJSONObject().toString(4));



                    // TODO: send 'confirm' to your server for verification
                    String paymentDetail=confirm.toJSONObject().toString(4);

                    JSONObject jsonObject=new JSONObject(paymentDetail);

                    //ده المودل اللى هشيل فيه الطلبات
                    Request request=new Request(Common.currentUser.getPhone(),
                            Common.currentUser.getName(),
                            address,
                            textTotalPrice.getText().toString(),
                            "0",
                            cartList//الاطعمه
                            ,
                            String.format("%s,%s",shappingAddress.getLatLng().latitude,shappingAddress.getLatLng().longitude)
                             ,
                            comment
                            ,
                           "PAYPAL",
                            jsonObject.getJSONObject("response").getString("state") //State Pay
                            ,""
                    );
                    //we will using current.Millis to Key
                    String orderNumber=String.valueOf(System.currentTimeMillis());
                    //Submit to Firebase
                    requestesToFirebase.child(orderNumber).
                            setValue(request);
                    //بعد ما ابعت البيانات للفيربيز وتتخزن همسحها من SQlite
                    //Delete From Cart to SQlite
                    new Database(getBaseContext()).cleanCart(Common.currentUser.getPhone());
                    sendNotificationOrder(orderNumber);

                    Toast.makeText(Cart.this, "Thank you ,Order Place ", Toast.LENGTH_SHORT).show();
                    finish();



                } catch (JSONException e) {
                    Log.e("sampleapp", "no confirmation data: ", e);
                    Toast.makeText(this, "no confirmation data:", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.i("sampleapp", "The user canceled.");
            Toast.makeText(this, "canceled", Toast.LENGTH_SHORT).show();


        } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            Log.i("sampleapp", "Invalid payment / config set");
            Toast.makeText(this, "Invalid payment / config set", Toast.LENGTH_SHORT).show();
        }


    }


    //Our last step is to cleanup in our onDestroy Paypal
    @Override
    public void onDestroy(){
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }


    //Swipe Delete
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position)
    {
        if (viewHolder instanceof CartViewHolder)
        {

            String name=((CartAdabter)recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition()).getProudactName();

            final Order deleteOrder=((CartAdabter)recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition());
            final int deleteIndex=viewHolder.getAdapterPosition();

           //*****Remove Item*******//
           cartAdabter.removeItem(deleteIndex);
           new Database(getBaseContext()).removeFromCart(deleteOrder.getProudactID(),Common.currentUser.getPhone());

            //Update textTotalPrice
            //Calculate Total Price
            int totalPrice=0;

            List<Order> orders=new Database(getBaseContext()).getCarts(Common.currentUser.getPhone());

            for (Order item:orders)
            {
                totalPrice+=(Integer.parseInt(item.getPrice()))*(Integer.parseInt(item.getQuentity()));
            }

            Locale locale1=new Locale("en","US");
            NumberFormat numberFormat=NumberFormat.getCurrencyInstance(locale1);
             textTotalPrice.setText(numberFormat.format(totalPrice));

             //*****UNDO Item*******//
             //Make SnakeBar
            Snackbar snackbar=Snackbar.make(rootLayoutCart,name +"removed From Cart",Snackbar.LENGTH_LONG);

            snackbar.setAction("UNDO", new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    cartAdabter.restoreItem(deleteOrder,deleteIndex);
                    new Database(getBaseContext()).addToCarts(deleteOrder);

                    //Update textTotalPrice
                    //Calculate Total Price
                    int totalPrice=0;

                    List<Order> orders=new Database(getBaseContext()).getCarts(Common.currentUser.getPhone());

                    for (Order item:orders)
                    {
                        totalPrice+=(Integer.parseInt(item.getPrice()))*(Integer.parseInt(item.getQuentity()));
                    }

                    Locale locale1=new Locale("en","US");
                    NumberFormat numberFormat=NumberFormat.getCurrencyInstance(locale1);
                    textTotalPrice.setText(numberFormat.format(totalPrice));

                }
            });

            snackbar.setActionTextColor(Color.GREEN);
            snackbar.show();

        }



    }

    //--------------------------------------





}
