package com.example.eng_mahnoud83coffey.embeatit;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView textSlogn;
    private Button btnSignIn;
    private Button btnSignUp;
    private Typeface typeface;

    //شاشه الترحيب
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //---------------ID------------------------//
        textSlogn=(TextView)findViewById(R.id.txtslogn);
        btnSignUp=(Button)findViewById(R.id.btnMainSignUp);
        btnSignIn=(Button)findViewById(R.id.btnMainSignIn);



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



    }







}
