package com.example.eng_mahnoud83coffey.embeatit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eng_mahnoud83coffey.embeatit.Common.Common;
import com.example.eng_mahnoud83coffey.embeatit.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private TextView textSlogn;
    private Button btnSignIn;
    private Button btnSignUp;
    private Typeface typeface;
    private ProgressDialog progressDialog;
    //-------------------------------------
    private FirebaseDatabase database;
    private DatabaseReference table_User;

    //شاشه الترحيب
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



             //Key Hashes or علامات تجزئة مفتاحية
             printKeyHASH();

        //---------------ID------------------------//
        textSlogn=(TextView)findViewById(R.id.txtslogn);
        btnSignUp=(Button)findViewById(R.id.btnMainSignUp);
        btnSignIn=(Button)findViewById(R.id.btnMainSignIn);
        progressDialog=new ProgressDialog(this);




        //Init Paper
        Paper.init(this);



         //-------------Action Button---------------------//
        btnSignIn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                Intent intentActivitySignIn=new Intent(MainActivity.this,SignIn.class);
                    startActivity(intentActivitySignIn);
            }
        });


        btnSignUp.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intentActivitySignUp=new Intent(MainActivity.this,SignUp.class);
                startActivity(intentActivitySignUp);

            }
        });


        //---------------------Custom Font---------------//
        typeface=Typeface.createFromAsset(getAssets(),"fonts/nabila.ttf");
        textSlogn.setTypeface(typeface);


        //-----Check Remember -------------//

        String user=Paper.book().read(Common.USER_KEY);
        String pwd=Paper.book().read(Common.PWD_KEY);


        if (user !=null && pwd != null)
        {
            if (!user.isEmpty() && !pwd.isEmpty())
            {
                 login(user,pwd);
            }

        }




    }


    private void printKeyHASH()
    {
        try {

            PackageInfo  info=getPackageManager().getPackageInfo("com.example.eng_mahnoud83coffey.embeatit", PackageManager.GET_SIGNATURES);

            for (Signature signature:info.signatures)
            {
                MessageDigest md=MessageDigest.getInstance("SHA");
                   md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(),Base64.DEFAULT));
                // f3xj4WWhBGbKJGytb5Qkqo0Ghx0=

            }

          } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }


    private void login(final String phone, final String pwd)
    {
        //----------------Firbase------------------------//
        database= FirebaseDatabase.getInstance();//Intial Firbase
        table_User=database.getReference("User");//Referance Table Name


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


                        user.setPhone(phone); //set Phone

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


}
