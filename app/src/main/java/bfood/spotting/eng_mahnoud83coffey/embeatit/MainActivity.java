package bfood.spotting.eng_mahnoud83coffey.embeatit;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import bfood.spotting.eng_mahnoud83coffey.embeatit.Common.Common;
import bfood.spotting.eng_mahnoud83coffey.embeatit.Model.User;


import com.facebook.FacebookSdk;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.facebook.accountkit.ui.SkinManager;
import com.facebook.accountkit.ui.UIManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE =7888 ;
    private TextView textSlogn;
   // private Button btnSignIn;
    private Button btnContinue;
    private Typeface typeface;
    private ProgressDialog progressDialog;
    //-------------------------------------
    private FirebaseDatabase database;
    private DatabaseReference table_User;

    //Library Custom font
 /*   @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }*/
    //شاشه الترحيب
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    /*    //Library Custom font
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/restaurant_font.otf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );*/
        //printKeyHash();
        FacebookSdk.sdkInitialize(getApplicationContext());
        AccountKit.initialize(this);

        setContentView(R.layout.activity_main);




        //---------------ID------------------------//
        textSlogn=(TextView)findViewById(R.id.txtslogn);
        btnContinue=(Button)findViewById(R.id.btnContinue);
        progressDialog=new ProgressDialog(this);

       //----------------Firbase------------------------//
        database= FirebaseDatabase.getInstance();//Intial Firbase
        table_User=database.getReference("User");//Referance Table Name




         //-------------Action Button---------------------//

         btnContinue.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (Common.IsConnectedToInternet(MainActivity.this))
                {
                  startLoginSystem();

                }else
                    {
                        Toast.makeText(MainActivity.this, "Please  Check Internet Connection ", Toast.LENGTH_SHORT).show();
                      return;
                    }
         }
        });


        //---------------------Custom Font---------------//
        typeface=Typeface.createFromAsset(getAssets(),"fonts/nabila.ttf");
        textSlogn.setTypeface(typeface);


    /*    //-----Check Remember -------------//

        //Init Paper
        Paper.init(this);
        String user=Paper.book().read(Common.USER_KEY);
        String pwd=Paper.book().read(Common.PWD_KEY);

        if (user !=null && pwd != null)
        {
            if (!user.isEmpty() && !pwd.isEmpty())
            {
                 login(user,pwd);
            }
        }

*/

       if (Common.IsConnectedToInternet(MainActivity.this)) {
           //Check Facebook Account Kit
           if (AccountKit.getCurrentAccessToken() != null) {
               //show Dialog
               final AlertDialog watingDialog = new SpotsDialog.Builder().setContext(this).build();
               watingDialog.setMessage("Please Wait...");
               watingDialog.setCancelable(false);
               watingDialog.show();

               AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                   @Override
                   public void onSuccess(Account account) {
                       table_User.child(account.getPhoneNumber().toString())
                               .addListenerForSingleValueEvent(new ValueEventListener() {
                                   @Override
                                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                                       User localUser = dataSnapshot.getValue(User.class);

                                       Intent homeIntent = new Intent(MainActivity.this, Home.class);
                                       Common.currentUser = localUser;
                                       startActivity(homeIntent);
                                       watingDialog.dismiss();
                                       finish();

                                   }

                                   @Override
                                   public void onCancelled(@NonNull DatabaseError databaseError) {

                                   }
                               });


                   }

                   @Override
                   public void onError(AccountKitError accountKitError) {

                   }
               });
           }
       }else
           {
               Toast.makeText(this, "Please Check Internet Connection !", Toast.LENGTH_SHORT).show();
                return;
           }


   }



    private void startLoginSystem()
    {
        Intent intent=new Intent(MainActivity.this, AccountKitActivity.class);
       // AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder=new AccountKitConfiguration.AccountKitConfigurationBuilder(LoginType.PHONE,AccountKitActivity.ResponseType.TOKEN);
                                                                                                                // LoginType.EMAIL
         AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder = new AccountKitConfiguration.AccountKitConfigurationBuilder(  LoginType.PHONE,AccountKitActivity.ResponseType.TOKEN); // or .ResponseType.CODE

        UIManager uiManager = new SkinManager(
                LoginType.PHONE,
                SkinManager.Skin.TRANSLUCENT,
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? getResources().getColor(R.color.green,null):getResources().getColor(R.color.greenW)),
                R.drawable.m5,
                SkinManager.Tint.BLACK,
                0.55
        );
        configurationBuilder.setUIManager(uiManager);


        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,configurationBuilder.build());
        startActivityForResult(intent,REQUEST_CODE);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);


         if (requestCode==REQUEST_CODE)
         {
             AccountKitLoginResult result=data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);

             if (result.getError()!=null)
             {
                 Toast.makeText(this, ""+result.getError().getErrorType().getMessage(), Toast.LENGTH_SHORT).show();
                 return;
             }
             else if (result.wasCancelled())
             {
                 Toast.makeText(this, "Cancel ", Toast.LENGTH_SHORT).show();
                 return;
             }
             else
                 {
                     if (result.getAccessToken() !=null)
                     {
                         //show Dialog
                         final AlertDialog watingDialog=new  SpotsDialog.Builder().setContext(this).build();
                                     watingDialog.setMessage("Please Wait...");
                                     watingDialog.setCancelable(false);
                                     watingDialog.show();

                         //Get Current Phone
                         AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                             @Override
                             public void onSuccess(Account account)
                             {

                                 final String userPhone=account.getPhoneNumber().toString();

                                 //Check if Exists on firebase User
                                 table_User.orderByKey().equalTo(userPhone)
                                                     .addListenerForSingleValueEvent(new ValueEventListener() {
                                                         @Override
                                                         public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                                         {
                                                                    if (!dataSnapshot.child(userPhone).exists()) //if not Exists
                                                                    {
                                                                        //we will Create new User and Login
                                                                        User newUser=new User();

                                                                             newUser.setPhone(userPhone);
                                                                             newUser.setName(" ");
                                                                             newUser.setBalance(0.0);

                                                                        //Add to Firebase
                                                                        table_User.child(userPhone)
                                                                                   .setValue(newUser)
                                                                                   .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                       @Override
                                                                                       public void onComplete(@NonNull Task<Void> task)
                                                                                       {
                                                                                                 if (task.isSuccessful())
                                                                                                     Toast.makeText(MainActivity.this, "User Register Successful", Toast.LENGTH_SHORT).show();


                                                                                                 //Login
                                                                                                 table_User.child(userPhone)
                                                                                                           .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                               @Override
                                                                                                               public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                                                                                               {


                                                                                                                    User localUser=dataSnapshot.getValue(User.class);



                                                                                                                   Intent nextIntent=new Intent(MainActivity.this,NextActivity.class);
                                                                                                                          nextIntent.putExtra("PhoneNumber",userPhone);
                                                                                                                   Common.currentUser=localUser;
                                                                                                                   startActivity(nextIntent);
                                                                                                                   watingDialog.dismiss();
                                                                                                                   finish();

                                                                                                               }

                                                                                                               @Override
                                                                                                               public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                                                               }
                                                                                                           });

                                                                                       }
                                                                                   });

                                                                    }
                                                                    else //if Exists
                                                                        { //we will Just Login


                                                                            //Login
                                                                            table_User.child(userPhone)
                                                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                        @Override
                                                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                                                                        {


                                                                                            User localUser=dataSnapshot.getValue(User.class);



                                                                                            Intent homeIntent=new Intent(MainActivity.this,Home.class);
                                                                                            Common.currentUser=localUser;
                                                                                            startActivity(homeIntent);
                                                                                            watingDialog.dismiss();
                                                                                            finish();

                                                                                        }

                                                                                        @Override
                                                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

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
                             public void onError(AccountKitError accountKitError)
                             {

                                 Toast.makeText(MainActivity.this, ""+accountKitError.getErrorType().getMessage(), Toast.LENGTH_SHORT).show();
                             }
                         });




                     }

                 }


         }

    }

 /* private void login(final String phone, final String pwd)
    {
        if (Common.IsConnectedToInternet(getBaseContext()))
        {
            progressDialog.setMessage("Please Wating...");
            progressDialog.show();
            table_User.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    //Check if user not exist in database بمعنى هتأكد هل المستخدم موجود فى قاعده البيانات ولا لا الاول
                    if (dataSnapshot.child(phone).exists()) {//لو موجود هجيب البيانات الخاصه بيه
                        //Get User Information
                        progressDialog.dismiss();
                        //Get Data From Database by Key
                        User user = dataSnapshot.child(phone).getValue(User.class);//Phone Number Is Primary Key
                        user.setPhoneClient(phone); //set Phone
                        if (user.getPassword().equals(pwd))//I am sure of the password
                        {
                           Toast.makeText(MainActivity.this, "Sign iN Successfull", Toast.LENGTH_SHORT).show();
                            Intent homeIntent=new Intent(MainActivity.this,Home.class);
                            Common.currentUser=user;
                            startActivity(homeIntent);
                           finish();
                        }
                        else
                        {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Sign In Failed !", Toast.LENGTH_SHORT).show();
                        }
                    }else
                    {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "User Not Exists on Database !", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        } else
        {
            Toast.makeText(MainActivity.this, "Please Check Your Connection", Toast.LENGTH_SHORT).show();
        }
    }
*/


/*

    private void printKeyHash() {

        try {
            PackageInfo info = getPackageManager().getPackageInfo("bfood.spotting.eng_mahnoud83coffey.embeatit", PackageManager.GET_SIGNATURES);

            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KEYHASH", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
*/

}
