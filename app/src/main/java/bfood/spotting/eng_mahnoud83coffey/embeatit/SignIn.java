package bfood.spotting.eng_mahnoud83coffey.embeatit;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import bfood.spotting.eng_mahnoud83coffey.embeatit.Common.Common;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import bfood.spotting.eng_mahnoud83coffey.embeatit.Model.User;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;


// Login Activity
public class SignIn extends AppCompatActivity {

    private TextView textHeader;
    private TextView textSignUpHere;
    private TextView forgotPasswordSignIn;

    private Button btnSignIn;
    private TextInputEditText editPhoneNumber;
    private TextInputEditText editPassword;
    private CheckBox checkBoxRememberMe;

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
        forgotPasswordSignIn=(TextView)findViewById(R.id.Forgot_Password_SignIn);
        checkBoxRememberMe=(CheckBox)findViewById(R.id.checkbox_Remember_me);


        progressDialog=new ProgressDialog(SignIn.this);

        //----------------Firbase------------------------//
          database=FirebaseDatabase.getInstance();//Intial Firbase
          table_User=database.getReference("User");//Referance Table Name



         //Paper Library Save User & password
        //Init Paper
        Paper.init(this);


        //---------------------Event-----------------//


        forgotPasswordSignIn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                if (Common.IsConnectedToInternet(getBaseContext()))
                {
                   showDialogForgetPassword();
                } else
                {
                    Toast.makeText(SignIn.this, "Please Check Your Connection Internet", Toast.LENGTH_SHORT).show();
                }

            }
        });



        btnSignIn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {


                if (Common.IsConnectedToInternet(getBaseContext()))
                {

                progressDialog.setMessage("Please Wating...");
                progressDialog.show();

                table_User.addListenerForSingleValueEvent(new ValueEventListener() {
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

                                //Save User & Password
                                if (checkBoxRememberMe.isChecked())
                                {
                                    Paper.book().write(Common.USER_KEY,editPhoneNumber.getText().toString());
                                    Paper.book().write(Common.PWD_KEY,editPassword.getText().toString());

                                }

                                Toast.makeText(SignIn.this, "Sign iN Successfull", Toast.LENGTH_SHORT).show();

                                Intent homeIntent=new Intent(SignIn.this,Home.class);

                                 Common.currentUser=user;
                                 startActivity(homeIntent);
                                 finish();

                                 table_User.removeEventListener(this);

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

                } else
                   {
                       Toast.makeText(SignIn.this, "Please Check Your Connection", Toast.LENGTH_SHORT).show();
                   }
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


     //Method Show Dialog Forget Password
    private void showDialogForgetPassword()
    {

        AlertDialog.Builder alertDialog= new AlertDialog.Builder(this);
                            alertDialog.setTitle("Forget Password");
                            alertDialog.setMessage("Enter Your Secure Code");
                            alertDialog.setIcon(R.drawable.ic_security_black_24dp);

        LayoutInflater layoutInflater=this.getLayoutInflater();
        View viewInflater=layoutInflater.inflate(R.layout.forget_password_dialog,null);

        final TextInputEditText editGetPhone=(TextInputEditText)viewInflater.findViewById(R.id.phone_number_dialogforgetpwd);
        final TextInputEditText editGetSecureCode=(TextInputEditText)viewInflater.findViewById(R.id.secureCode_DialogForgetpwd);

        alertDialog.setView(viewInflater);

        // Event
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                   //check User
                table_User.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {

                             //Check Find User
                        if (dataSnapshot.child(editGetPhone.getText().toString()).exists())
                        {

                            User user=dataSnapshot.child(editGetPhone.getText().toString()).getValue(User.class);

                               //Compare Secure Code
                            if (user.getSecureCode().equals(editGetSecureCode.getText().toString()))
                            {
                                Toast.makeText(SignIn.this, "Password = "+user.getPassword(), Toast.LENGTH_LONG).show();
                            }else
                                {
                                    Toast.makeText(SignIn.this, "Wrong Secure Code", Toast.LENGTH_SHORT).show();
                                }


                        }else
                            {
                                Toast.makeText(SignIn.this, "This User not Found", Toast.LENGTH_SHORT).show();
                            }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });



        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                dialogInterface.dismiss();
            }
        });


        alertDialog.show();
    }


}
