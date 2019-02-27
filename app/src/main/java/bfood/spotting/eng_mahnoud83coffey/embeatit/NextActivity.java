package bfood.spotting.eng_mahnoud83coffey.embeatit;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import bfood.spotting.eng_mahnoud83coffey.embeatit.Common.Common;
import bfood.spotting.eng_mahnoud83coffey.embeatit.Model.User;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NextActivity extends AppCompatActivity
{


    private Button btnFinish;
    private TextInputEditText editName;
    private TextInputEditText editHomeAddress;
    //-----------------------------------
    private DatabaseReference userTable;
    private FirebaseDatabase firebaseDatabase;
    private String userPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);


        //---------------Id-----------------//
        btnFinish=(Button)findViewById(R.id.btnfinish_nextActivity);
        editName=(TextInputEditText)findViewById(R.id.edittext_name_nextActivity);
        editHomeAddress=(TextInputEditText)findViewById(R.id.edittext_HomeAddress_nextActivity);

        //--------------Init---------------//
        firebaseDatabase=FirebaseDatabase.getInstance();
        userTable=firebaseDatabase.getReference(Common.USER);



        if (getIntent()!=null)
        {
             userPhone=getIntent().getStringExtra("PhoneNumber");

        }


        btnFinish.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                completeTheData(editName.getText().toString(),editHomeAddress.getText().toString());

            }
        });


    }

    private void completeTheData(String name, String homeAddress)
    {
        if (name.isEmpty()||name==null||name==" ")
        {
            editName.setError("");
            //return;
        }
        else if(homeAddress.isEmpty()||homeAddress==null||homeAddress==" ")
        {
            editHomeAddress.setError("");
            //return;
        }
        else
            {
                //we will Create new User and Login
                User newUser=new User();

                newUser.setPhone(userPhone);
                newUser.setName(name);
                newUser.setHomeAddress(homeAddress);
                newUser.setIsStaff("false");
                newUser.setBalance(0.0);

                userTable.child(userPhone).setValue(newUser).addOnSuccessListener(new OnSuccessListener<Void>()
                {
                    @Override
                    public void onSuccess(Void aVoid)
                    {
                        Toast.makeText(NextActivity.this, "Thank You", Toast.LENGTH_SHORT).show();
                        Intent home=new Intent(NextActivity.this,Home.class);
                        startActivity(home);
                        finish();
                    }
                });


            }




    }


}
