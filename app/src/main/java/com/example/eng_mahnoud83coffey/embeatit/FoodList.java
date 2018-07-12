package com.example.eng_mahnoud83coffey.embeatit;

import android.content.Context;
import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Toast;


import com.example.eng_mahnoud83coffey.embeatit.Common.Common;
import com.example.eng_mahnoud83coffey.embeatit.Database.Database;
import com.example.eng_mahnoud83coffey.embeatit.Interface.ItemClickListener;
import com.example.eng_mahnoud83coffey.embeatit.Model.Food;
import com.example.eng_mahnoud83coffey.embeatit.ViewHolder.FoodViewHolder;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.internal.Util;

public class FoodList extends AppCompatActivity {

    //----------------------------------
    private FirebaseDatabase database;
    private DatabaseReference foodList; //using index
    //----------------------------------
    private RecyclerView recyclerViewFoods;
    private RecyclerView.LayoutManager layoutManager;
    //--------Firebase UI--------//
    private Query query;
    private FirebaseRecyclerOptions<Food> options;
    private FirebaseRecyclerAdapter<Food,FoodViewHolder> adapter;
    //--------------------------------------
    private String categoryId;
    //-----------Search Functionality-------
    List<String> suggestList =new ArrayList<>();
    MaterialSearchBar materialSearchBar;
    private FirebaseRecyclerAdapter<Food,FoodViewHolder> searchAdapter;

    //----------------------------------
     Database localDatabe;
    //------------------------
    //private SwipeRefreshLayout swipeRefresh;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);


          //-------------------Id----------------------------//
         recyclerViewFoods=(RecyclerView)findViewById(R.id.recyclerView_food);
         materialSearchBar=(MaterialSearchBar)findViewById(R.id.search_Bar_FoodList);//---Search*/
        //swipeRefresh=(SwipeRefreshLayout)findViewById(R.id.swipe_foodlist_refresh);



            //------------------Firebase-------------------------//
          database=FirebaseDatabase.getInstance();
          foodList =database.getReference("Foods");

        //------------------Recycler View------------------//
        recyclerViewFoods.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerViewFoods.setLayoutManager(layoutManager);

        //-------------------------------------
        localDatabe=new Database(this);

        //-----------------Get Intent Here-----------------//
         if (getIntent() != null)
         {
             categoryId=getIntent().getStringExtra("CategoryName");


              if (! categoryId.isEmpty() && categoryId !=null)
              {
                  if (Common.IsConnectedToInternet(this))
                  {
                      //Load Data on RecyclerView
                      loadListFoods(categoryId);


                  }else
                      {
                          Toast.makeText(this, "Please Check Your Connection", Toast.LENGTH_SHORT).show();
                      }

                 }
       }

        //------------------ Search ----------//
         materialSearchBar.setHint("Enter your Food");
        // materialSearchBar.setSpeechMode(false);//if set to true, microphone icon will be displayed instead of search icon

        loadSuggest();//load suggest from firebase

        materialSearchBar.setLastSuggestions(suggestList);
        materialSearchBar.setCardViewElevation(10);

        //SEARCHBAR TEXT CHANGE LISTENER
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

                //Then user Type their text ,we will change Suggest list

                List<String> suggest =new ArrayList<String>();

                for (String search: suggestList) //Loop in suggest List
                {
                    if (search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                        suggest.add(search);
                }

                materialSearchBar.setLastSuggestions(suggest);



            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled)
            {  //When Search Bar is Close
                //Restore Original Adapter

                if (!enabled)
                    recyclerViewFoods.setAdapter(adapter);

            }

            @Override
            public void onSearchConfirmed(CharSequence text)
            { //When Search Finish
              //Show Resulet of Search Adabter
                //startSearch(text);
                startSearch(text);

            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });



        //-------------------Event--------------------------//
        /*swipeRefresh.setColorSchemeResources(R.color.colorPrimaryDark,
                                             android.R.color.holo_green_dark,
                                             android.R.color.holo_orange_dark,
                                             android.R.color.holo_blue_dark);
*/
        /*swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh()
            {
                //-----------------Get Intent Here-----------------//
                if (getIntent() != null)
                {
                    categoryId=getIntent().getStringExtra("CategoryId");

                    if (! categoryId.isEmpty() && categoryId !=null)
                    {
                        if (Common.IsConnectedToInternet(getApplicationContext()))
                        {
                            //Load Data on RecyclerView
                            loadListFoods(categoryId);

                        }else
                        {
                            Toast.makeText(getApplicationContext(), "Please Check Your Connection", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });


        swipeRefresh.post(new Runnable() {
            @Override
            public void run()
            {
                //-----------------Get Intent Here-----------------//
                if (getIntent() != null)
                {
                    categoryId=getIntent().getStringExtra("CategoryId");

                    if (! categoryId.isEmpty() && categoryId !=null)
                    {
                        if (Common.IsConnectedToInternet(getApplicationContext()))
                        {
                            //Load Data on RecyclerView
                            loadListFoods(categoryId);
                        }else
                        {
                            Toast.makeText(getApplicationContext(), "Please Check Your Connection", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });


*/

    }

    private void startSearch(CharSequence text) {
        //---Using Firebase UI to populate a RecyclerView--------//
       // query = foodList.orderByChild("Name").equalTo(text.toString()); //Compare Name

        //---Using Firebase UI to populate a RecyclerView--------//
        query= FirebaseDatabase.getInstance()
                .getReference()
                .child("Foods").orderByChild("Name").equalTo(text.toString());



        //  query.keepSynced(true);//Load Data OffLine

        options = new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(query, Food.class)
                .build();

        searchAdapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {
            @Override
            public FoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.food_item, parent, false);

                return new FoodViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(final FoodViewHolder holder, final int position, final Food model) {
                // Bind the Chat object to the ChatHolder

                //Send Image Name to Recyclerview
                holder.textFoodName.setText(model.getName());

                //Send Image  to Recyclerview
                Picasso.get()
                        .load(model.getImage())//Url
                        //  .networkPolicy(NetworkPolicy.OFFLINE)//تحميل الصوره Offline
                        //.placeholder(R.drawable.d)//الصوره الافتراضه اللى هتظهر لحد لما الصوره تتحمل
                        .into(holder.foodimageView);

                final Food clickItem = model;


                //لما المستخدم يضغط على اى صف
                holder.setItemClickListener(new ItemClickListener() {

                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Start New Activity

                        Intent foodDeatilsIntent = new Intent(FoodList.this, FoodDetails.class);

                        foodDeatilsIntent.putExtra("FoodId", adapter.getRef(position).getKey());//send food id new Activity
                        startActivity(foodDeatilsIntent);


                    }
                });


            }//end OnBind


        };//end Adapter

        recyclerViewFoods.setAdapter(searchAdapter); //set Adapter for Recycler view is search resulet
    }


   //load suggest from firebase
    //Search
    private void loadSuggest()
    {
         foodList.orderByChild("MenuId").equalTo(categoryId)
                                     .addValueEventListener(new ValueEventListener() {
                                         @Override
                                         public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                         {

                                             for (DataSnapshot postSnapshot:dataSnapshot.getChildren())
                                             {
                                                        Food foodItem=postSnapshot.getValue(Food.class);

                                                        suggestList.add(foodItem.getName()); //Add name of food to Suggest List
                                             }
                                         }

                                         @Override
                                         public void onCancelled(@NonNull DatabaseError databaseError) {

                                         }
                                     });
    }


     //Load Data from Firebase to displayed Recyclerview
    private void loadListFoods(String categoryId)
    {



        //---Using Firebase UI to populate a RecyclerView--------//
        query= FirebaseDatabase.getInstance()
                .getReference()
                .child("Foods");

        query.keepSynced(true);//Load Data OffLine

        options = new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(query, Food.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {
            @Override
            public FoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.food_item, parent, false);

                return new FoodViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(final FoodViewHolder holder, final int position, final Food model) {
                // Bind the Chat object to the ChatHolder

                //Send Image  to Recyclerview
               // holder.shareImage.setImageResource(R.drawable.ic_share_black_24dp);

                //Send Image Name to Recyclerview
                holder.textFoodName.setText(model.getName());

                //Send Image  to Recyclerview
                Picasso.get()
                        .load(model.getImage())//Url
                        //  .networkPolicy(NetworkPolicy.OFFLINE)//تحميل الصوره Offline
                        //.placeholder(R.drawable.d)//الصوره الافتراضه اللى هتظهر لحد لما الصوره تتحمل
                        .into(holder.foodimageView);

                final Food clickItem=model;


                // Click to Share
                holder.shareImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {

                        shareItem(model.getImage(),clickItem);
                    }
                });



                    //Check food do Favorites or Not ,is Not Display Image Favorite border...
                if (localDatabe.isFoodFavorites(adapter.getRef(position).getKey()))
                    holder.favimage.setImageResource(R.drawable.ic_favorite_black_24dp);

                      //if Click on Image Favorites
                holder.favimage.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                            //Not Favorites
                        if (!localDatabe.isFoodFavorites(adapter.getRef(position).getKey()))
                        {
                             //Add Favorites
                            localDatabe.addToFavorites(adapter.getRef(position).getKey());
                            holder.favimage.setImageResource(R.drawable.ic_favorite_black_24dp);
                            Toast.makeText(FoodList.this, model.getName()+" was add Favorites", Toast.LENGTH_SHORT).show();
                        }else
                            { //IF FOOD Favorites
                                    //Remove Food Favorites
                                localDatabe.removeFavorites(adapter.getRef(position).getKey());
                                 holder.favimage.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                                Toast.makeText(FoodList.this, model.getName()+" Remove From Favorites", Toast.LENGTH_SHORT).show();
                            }
                    }
                });

                //لما المستخدم يضغط على اى صف
                holder.setItemClickListener(new ItemClickListener() {

                    @Override
                    public void onClick(View view, int position, boolean isLongClick)
                    {
                        //Start New Activity

                        Intent foodDeatilsIntent=new Intent(FoodList.this,FoodDetails.class);

                               foodDeatilsIntent.putExtra("FoodId", adapter.getRef(position).getKey());//send food id new Activity
                               startActivity(foodDeatilsIntent);
                    }
                });

            }//end OnBind
        };//end Adapter

        recyclerViewFoods.setAdapter(adapter);
       // swipeRefresh.setRefreshing(false);
    }

    //Start Adapter
    @Override
    protected void onStart() {
        super.onStart();

        adapter.startListening();

    }

    //Stop Adapter
    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }






    //Use Picasso to load the url into a Bitmap
    public void shareItem(String url, final Food food) {

        Picasso.get().load(url).into(new Target() {
            @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {


                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("image/*");
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(bitmap,food));
                i.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/developer?id=MBasuony_JA");
               startActivity(Intent.createChooser(i, "Share link "));
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable)
            {
                Toast.makeText(FoodList.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override public void onPrepareLoad(Drawable placeHolderDrawable) { }
        });
    }
    //Convert Bitmap into Uri
    public Uri getLocalBitmapUri(Bitmap bmp,Food food) {

        Uri bmpUri = null;

        try {

            File file =  new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Name Food"+food.getName() +"Description : "+food.getDescription() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }
}





