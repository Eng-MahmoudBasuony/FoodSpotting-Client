package com.example.eng_mahnoud83coffey.embeatit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eng_mahnoud83coffey.embeatit.Common.Common;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.eng_mahnoud83coffey.embeatit.Model.User;



// Login Activity
public class SignIn extends AppCompatActivity {

    private TextView textHeader;
    private TextView textSignUpHere;
    private Button btnSignIn;
    private TextInputEditText editPhoneNumber;
    private TextInputEditText editPassword;
    //--------------------------------
    private Typeface typeface;
    private ProgressDialog progressDialog;
    //-----------Firebase-----------------
    private FirebaseDatabase database;
    private DatabaseReference table_User;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);


        //----------------------Id--------------------//
        textHeader=(TextView)findViewById(R.id.textHeader);
        btnSignIn=(Button)findViewById(R.id.Button_SignIn);
        editPhoneNumber=(TextInputEditText)findViewById(R.id.Phone_Number_SignIn);
        editPassword=(TextInputEditText)findViewById(R.id.Password_SignIn);
        textSignUpHere=(TextView)findViewById(R.id.Text_SignUp_Here);

        progressDialog=new ProgressDialog(SignIn.this);

        //----------------Firbase------------------------//
          database=FirebaseDatabase.getInstance();//Intial Firbase
          table_User=database.getReference("User");//Referance Table Name



        //---------------------Action Button-----------------//

        btnSignIn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                progressDialog.setMessage("Please Wating...");
                progressDialog.show();

                table_User.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        //Check if user not exist in database بمعنى هتأكد هل المستخدم موجود فى قاعده البيانات ولا لا الاول
                        if (dataSnapshot.child(editPhoneNumber.getText().toString()).exists()) {//لو موجود هجيب البيانات الخاصه بيه
                            //Get User Information
                             progressDialog.dismiss();

                                      //Get Data From Database by Key
                            User user = dataSnapshot.child(editPhoneNumber.getText().toString()).getValue(User.class);//Phone Number Is Primary Key


                            user.setPhone(editPhoneNumber.getText().toString()); //set Phone

                            if (user.getPassword().equals(editPassword.getText().toString()))//I am sure of the password
                            {

                                Toast.makeText(SignIn.this, "Sign iN Successfull", Toast.LENGTH_SHORT).show();

                                Intent homeIntent=new Intent(SignIn.this,Home.class);

                                Common.currentUser=user;
                               startActivity(homeIntent);
                                finish();

                            }

                        else
                            {
                                progressDialog.dismiss();
                                Toast.makeText(SignIn.this, "Sign In Failed !", Toast.LENGTH_SHORT).show();
                            }

                        }else
                            {
                              progressDialog.dismiss();
                                Toast.makeText(SignIn.this, "User Not Exists on Database !", Toast.LENGTH_SHORT).show();
                            }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                }
        });


        textSignUpHere.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intentActivitySignUp=new Intent(SignIn.this,SignUp.class);
                 startActivity(intentActivitySignUp);

            }
        });

       //--------------------------Custome font-----------------------//
       typeface=Typeface.createFromAsset(getAssets(),"fonts/nabila.ttf");

         textHeader.setTypeface(typeface);


    }












}
