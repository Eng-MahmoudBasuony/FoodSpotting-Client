package com.example.eng_mahnoud83coffey.embeatit;

import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.eng_mahnoud83coffey.embeatit.Database.Database;
import com.example.eng_mahnoud83coffey.embeatit.Model.Food;
import com.example.eng_mahnoud83coffey.embeatit.Model.Order;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;


//الكلاس المسؤل عن عرض وصف عن الطعام وامكانيه اضافه الطعام لسله المشتريات التى ستخزن فى SQlite
//1- عرض وصف للطعام وسعره وكذا
//2- اضافه لسله المشتريات وتخزينه فى قاعده البيانات الSQlite
public class FoodDetails extends AppCompatActivity {


    private TextView foodName,foodPrice,foodDescription;
    private ImageView foodImage;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private FloatingActionButton btnCart;
    private ElegantNumberButton numberButton;
    //-----------------------------------------
    private String foodId="";
    private  Food currentFood;
    //----------------------------------------
    private FirebaseDatabase database;
    private DatabaseReference table_Foods;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_details);

        //-----------------------Id-------------------------//
         foodName=(TextView)findViewById(R.id.food_name_FoodDetails);
         foodPrice=(TextView)findViewById(R.id.food_Price_FoodDetails);
         foodDescription=(TextView)findViewById(R.id.food_description_foodDetails);
         foodImage=(ImageView)findViewById(R.id.image_foodDetails);
         btnCart=(FloatingActionButton)findViewById(R.id.btnCart_foodDetails);
         collapsingToolbarLayout=(CollapsingToolbarLayout)findViewById(R.id.collapsing_FoodDetails);
         numberButton=(ElegantNumberButton)findViewById(R.id.number_button_foodDetails);




        //--------------------Firebase---------------------//
        database=FirebaseDatabase.getInstance();
        table_Foods=database.getReference("Foods");

       //--------------------Collapsing Toolbar--------------//
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.EpandedAppBar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsdAppBar);



        //----------Get Food Id From Intent--------------//

            if (getIntent() !=null)// Get Id Food OnClick "اللى ضغط عليها عشان ابعت الصوره وبياناتها لصفحه الوصف "
            {

                foodId=getIntent().getStringExtra("FoodId");

                if (!foodId.isEmpty())
                {
                    getFoodDetails(foodId);// ارسال الKey لجلب بيانات الitem من الFirebase لعرضها فى اكتيفيتى ديه"الوصف"
                }

            }



         //----------------------------Action Button---------------------------//

                //ارسال الطلبات المختاره "الاطعمه" لسه المشتريات اللى هيا قاعده البيانات 'SQlite'
        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                       //Send Data to Sqlite
                  new Database(getBaseContext()).addToCarts(new Order(foodId,
                                                                      currentFood.getName(),
                                                                      numberButton.getNumber(),
                                                                      currentFood.getPrice(),
                                                                      currentFood.getDiscount())
                                                           );
                Toast.makeText(FoodDetails.this, "Added To Cars", Toast.LENGTH_SHORT).show();
            }
        });




    }


     //جلب البيانات item اللى ضغط عليه  عن طريق الKey الخاص بيه من الFirebase
    //get Data item  From Firebase
    private void getFoodDetails(final String foodId)
    {

        table_Foods.child(foodId).addValueEventListener(new ValueEventListener()
          {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {

                currentFood=dataSnapshot.getValue(Food.class);

                //set image
                Picasso.get()
                        .load(currentFood.getImage())//Url
                       // .networkPolicy(NetworkPolicy.OFFLINE)//تحميل الصوره Offline
                        //.placeholder(R.drawable.d)//الصوره الافتراضه اللى هتظهر لحد لما الصوره تتحمل
                        .into(foodImage);//Image View


                collapsingToolbarLayout.setTitle(currentFood.getName());
                foodPrice.setText(currentFood.getPrice());
                foodName.setText(currentFood.getName());
                foodDescription.setText(currentFood.getDescription());



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }






}
