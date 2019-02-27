package bfood.spotting.eng_mahnoud83coffey.embeatit;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import bfood.spotting.eng_mahnoud83coffey.embeatit.Common.Common;
import bfood.spotting.eng_mahnoud83coffey.embeatit.Model.User;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


//Register Activity
public class SignUp extends AppCompatActivity
{


    private TextInputEditText editUserName;
    private TextInputEditText editPhoneNumber;
    private TextInputEditText editPassword;
    private TextInputEditText editSecureCode;
    private Button btnSignUp;
    private TextView textSignInHere;
    private TextView textHeaderSignUp;
    //------------------------------------
    private Typeface typeface;
    private ProgressDialog progressDialog;
   //-------------------------------------
    private FirebaseDatabase database;
    private DatabaseReference table_User;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_sign_up);


        //----------------------------Id-----------------------------//
        editUserName=(TextInputEditText)findViewById(R.id.Name_SignUp);
        editPassword=(TextInputEditText)findViewById(R.id.Password_SignUp);
        editPhoneNumber=(TextInputEditText)findViewById(R.id.Phone_Number_SignUp);
        editSecureCode=(TextInputEditText)findViewById(R.id.secureCode_SignUp);
        textSignInHere=(TextView)findViewById(R.id.Text_SignIn_Here);
        textHeaderSignUp=(TextView)findViewById(R.id.textHeaderSignUp);
        btnSignUp=(Button)findViewById(R.id.Button_SignUp);



        progressDialog=new ProgressDialog(SignUp.this);

        //-----------------------Firebase----------------------------//
        database=FirebaseDatabase.getInstance();
        table_User=database.getReference("User");




        //-------------------------Action Button-------------------------//
        btnSignUp.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {


                if (Common.IsConnectedToInternet(getBaseContext())) {

                    progressDialog.setMessage("Please Wait....");
                    progressDialog.show();

                    table_User.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            //Check IF Alrady  User Phone
                            if (dataSnapshot.child(editPhoneNumber.getText().toString()).exists()) {
                                progressDialog.dismiss();
                                Toast.makeText(SignUp.this, "Phone Number Already Register !", Toast.LENGTH_SHORT).show();

                            } else {
                                //Storage Data On Model
                                User user = new User(editUserName.getText().toString(), editPassword.getText().toString(),editSecureCode.getText().toString());

                                //Storage Data on FirebaseDataBase
                                table_User.child(editPhoneNumber.getText().toString()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        Toast.makeText(SignUp.this, "Successfull Register ", Toast.LENGTH_SHORT).show();

                                    }
                                });

                                finish();

                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

            }else
                {
                    Toast.makeText(SignUp.this, "Please Check Your Connection", Toast.LENGTH_SHORT).show();
                }


            }
        });


        //-----------------Custom Font---------------------//
        typeface=Typeface.createFromAsset(getAssets(),"fonts/nabila.ttf");
        textHeaderSignUp.setTypeface(typeface);


    }





}
