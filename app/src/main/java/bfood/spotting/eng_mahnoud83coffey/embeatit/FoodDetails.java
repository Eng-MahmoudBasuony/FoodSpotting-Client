package bfood.spotting.eng_mahnoud83coffey.embeatit;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import bfood.spotting.eng_mahnoud83coffey.embeatit.Common.Common;
import bfood.spotting.eng_mahnoud83coffey.embeatit.Database.Database;
import bfood.spotting.eng_mahnoud83coffey.embeatit.Model.Food;
import bfood.spotting.eng_mahnoud83coffey.embeatit.Model.Order;
import bfood.spotting.eng_mahnoud83coffey.embeatit.Model.Rating;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import java.util.Arrays;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


//الكلاس المسؤل عن عرض وصف عن الطعام وامكانيه اضافه الطعام لسله المشتريات التى ستخزن فى SQlite
//1- عرض وصف للطعام وسعره وكذا
//2- اضافه لسله المشتريات وتخزينه فى قاعده البيانات الSQlite
public class FoodDetails extends AppCompatActivity implements RatingDialogListener {


    private TextView foodName,foodPrice,foodDescription;
    private ImageView foodImage;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private CounterFab btnCart;
    private FloatingActionButton btnRating;
    private ElegantNumberButton numberButton;
    private RatingBar ratingBar;
    private Button btnShowComment;
    //-----------------------------------------
    private String foodId="";
    private Food currentFood;
    //----------------------------------------
    private FirebaseDatabase database;
    private DatabaseReference table_Foods;
    private DatabaseReference table_Rating;


    //Library Custom font
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

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

        setContentView(R.layout.activity_food_details);

        //-----------------------Id-------------------------//
         foodName=(TextView)findViewById(R.id.food_name_FoodDetails);
         foodPrice=(TextView)findViewById(R.id.food_Price_FoodDetails);
         foodDescription=(TextView)findViewById(R.id.food_description_foodDetails);
         foodImage=(ImageView)findViewById(R.id.image_foodDetails);
         btnCart=(CounterFab)findViewById(R.id.btnCart_foodDetails);
         btnRating=(FloatingActionButton)findViewById(R.id.btnRating_foodDetails);
         ratingBar=(RatingBar)findViewById(R.id.rating_bar_foodDetails);
         collapsingToolbarLayout=(CollapsingToolbarLayout)findViewById(R.id.collapsing_FoodDetails);
        // numberButton=(ElegantNumberButton)findViewById(R.id.number_button_foodDetails);
         btnShowComment=(Button)findViewById(R.id.btn_show_comment_fooddetails);

        //--------------------Firebase---------------------//
        database=FirebaseDatabase.getInstance();
        table_Foods=database.getReference("Foods");
        table_Rating=database.getReference("Rating");

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
                    getRatingDetails(foodId); //also get Rating Data from Firebase use foodId
                }

            }

         //----------------------------Event---------------------------//

         //ارسال الطلبات المختاره "الاطعمه" لسه المشتريات اللى هيا قاعده البيانات 'SQlite'
        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                       //Send Data to Sqlite

                boolean isExists = new Database(getBaseContext()).checkFoodExists(foodId, Common.currentUser.getPhone());

                if (!isExists){
                    //Send Data to Sqlite
                    new Database(getBaseContext()).addToCarts(new Order(
                            Common.currentUser.getPhone(),
                            foodId,
                            currentFood.getName(),
                            "1", //numberButton.getNumber()
                            currentFood.getPrice(),
                            currentFood.getDiscount(),
                            currentFood.getImage())
                    );

                    Toast.makeText(FoodDetails.this, "Added To Cars", Toast.LENGTH_SHORT).show();

                }else
                {
                    new Database(getBaseContext()).increaseCart(foodId, Common.currentUser.getPhone());

                    Toast.makeText(FoodDetails.this, "The quantity of the same food was increased", Toast.LENGTH_SHORT).show();

                }





            }
        });

            btnCart.setCount(new Database(this).getCountCart(Common.currentUser.getPhone()));

            //Show Dialog Rating
        btnRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                showRatingDialog();

            }
        });


     btnShowComment.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view)
         {

             Intent intent=new Intent(FoodDetails.this,ShowComment.class);

                 intent.putExtra(Common.FOOD_ID,foodId);
                 startActivity(intent);

         }
     });


    }

    //get Rating Data from Firebase use foodId and set Rating Bar your App
    private void getRatingDetails(String foodId)
    {
        Query foodRating=table_Rating.child(foodId).orderByChild("foodId");//.equalTo(foodId)


        foodRating.addValueEventListener(new ValueEventListener() {

             int count=0,sum=0;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {

                for (DataSnapshot postSnapshot:dataSnapshot.getChildren())
                {
                    Rating item=postSnapshot.getValue(Rating.class);

                    sum+=Integer.parseInt(item.getRating());
                    count++;
                }


                if (count !=0)
                {
                    float average=sum/count;

                    ratingBar.setRating(average);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



/*
        //
        foodRating.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    int count=0,sum=0;
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {

                        for (DataSnapshot postSnapshot:dataSnapshot.getChildren())
                        {
                            Rating item=postSnapshot.getValue(Rating.class);

                            sum+=Integer.parseInt(item.getRating());
                            count++;
                        }


                        if (count !=0)
                        {
                            float average=sum/count;

                            ratingBar.setRating(average);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );
*/


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
                Picasso.with(getApplicationContext())
                        .load(currentFood.getImage())//Url
                       // .networkPolicy(NetworkPolicy.OFFLINE)//تحميل الصوره Offline
                        //.placeholder(R.drawable.d)//الصوره الافتراضه اللى هتظهر لحد لما الصوره تتحمل
                        .into(foodImage);//Image View


                collapsingToolbarLayout.setTitle(currentFood.getName());
                foodPrice.setText(String.format("$ %s",currentFood.getPrice()));
                foodName.setText(currentFood.getName());
                foodDescription.setText(currentFood.getDescription());



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    //Method Show Dialog Rating
    private void showRatingDialog()
    {
              //Flow library com.stepstone.apprating:app-rating
          new AppRatingDialog.Builder()
                             .setPositiveButtonText("Submit")
                             .setNegativeButtonText("Cancel")
                             .setNoteDescriptions(Arrays.asList("Very Bad", "Not good", "Quite ok", "Very Good", "Excellent !!!"))
                             .setDefaultRating(1)
                             .setTitle("Rating This Food")
                             .setDescription("Please select some stars and give your feedback")
                             .setStarColor(R.color.colorAccent)
                            .setNoteDescriptionTextColor(R.color.colorAccent)
                            .setTitleTextColor(R.color.colorPrimary)
                            .setDescriptionTextColor(R.color.colorAccent)
                            .setHint("Please write your comment here ...")
                            .setHintTextColor(R.color.white)
                            .setCommentTextColor(R.color.white)
                            .setCommentBackgroundColor(R.color.colorAccent)
                  .setWindowAnimation(R.style.RatingDialogFadAnim)
                  .create(FoodDetails.this)
                  .show();


          //Notes
                //first implements for RatingDialogListener
    }
     //this Method call when Click Submit
    @Override
    public void onPositiveButtonClicked(int valueRating, String comment)
    {


        final Rating rating=new Rating(Common.currentUser.getPhone(),foodId,String.valueOf(valueRating),comment);

       table_Rating.child(foodId).push().setValue(rating).addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                Toast.makeText(getApplicationContext(), "Thank You Your Rating", Toast.LENGTH_SHORT).show();

            }
        });
        getRatingDetails(foodId);

        // or other Solution
        // table_Rating.child(Common.currentUser.getPhoneClient()).child(foodId).setValue(rating);
        //  getRatingDetails(foodId); //also get Rating Data from Firebase use foodId
        //  Toast.makeText(this, "Thank You Your Rating", Toast.LENGTH_SHORT).show();


           /*
        table_Rating.child(Common.currentUser.getPhoneClient()).
                             addValueEventListener(new ValueEventListener() {
                                 @Override
                                 public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                 {

                                     if (dataSnapshot.child(Common.currentUser.getPhoneClient()).exists())
                                     {
                                         if (dataSnapshot.child(foodId).exists())
                                         {
                                         //Remove DATA
                                         table_Rating.child(Common.currentUser.getPhoneClient()).removeValue();
                                         //Update Data
                                         table_Rating.child(Common.currentUser.getPhoneClient()).setValue(rating);

                                         }else
                                             {
                                                 table_Rating.child(Common.currentUser.getPhoneClient()).push().setValue(rating);

                                             }

                                     }else
                                         {
                                             table_Rating.child(Common.currentUser.getPhoneClient()).setValue(rating);
                                             // table_Rating.child(Common.currentUser.getPhoneClient()).push().setValue(rating);
                                         }

                                 }

                                 @Override
                                 public void onCancelled(@NonNull DatabaseError databaseError) {

                                 }
                             });
          */


    }
    @Override
    public void onNegativeButtonClicked() {
    }
    @Override
    public void onNeutralButtonClicked() {
    }

    //--------------------------------------


}
