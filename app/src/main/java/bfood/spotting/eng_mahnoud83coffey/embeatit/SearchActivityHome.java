package bfood.spotting.eng_mahnoud83coffey.embeatit;

import android.content.Intent;
import android.net.Uri;
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

import bfood.spotting.eng_mahnoud83coffey.embeatit.Common.Common;
import bfood.spotting.eng_mahnoud83coffey.embeatit.Database.Database;
import bfood.spotting.eng_mahnoud83coffey.embeatit.Interface.ItemClickListener;
import bfood.spotting.eng_mahnoud83coffey.embeatit.Model.Favorites;
import bfood.spotting.eng_mahnoud83coffey.embeatit.Model.Food;
import bfood.spotting.eng_mahnoud83coffey.embeatit.Model.Order;



import bfood.spotting.eng_mahnoud83coffey.embeatit.ViewHolder.FoodViewHolder;
import com.facebook.CallbackManager;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
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

import java.util.ArrayList;
import java.util.List;

public class SearchActivityHome extends AppCompatActivity {

    //----------------------------------
    private FirebaseDatabase database;
    private DatabaseReference foodList; //using index
    //----------------------------------
    private RecyclerView recyclerViewFoods;
    private RecyclerView.LayoutManager layoutManager;
    //--------Firebase UI--------//
    private Query searchByName;
    private FirebaseRecyclerOptions<Food> options;
    private FirebaseRecyclerAdapter<Food,FoodViewHolder> adapter;
    //--------------------------------------
    private String categoryId;
    //-----------Search Functionality-------
    List<String> suggestList =new ArrayList<>();
    MaterialSearchBar materialSearchBar;
    private FirebaseRecyclerAdapter<Food,FoodViewHolder> searchAdapter;
    private SwipeRefreshLayout swipeRefresh;

    //----------------------------------
    Database localDatabe;

    CallbackManager callbackManager;
    ShareDialog shareDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_home);



        shareDialog = new ShareDialog(this);  // initialize facebook shareDialog.

        //-------------------Id----------------------------//
        recyclerViewFoods=(RecyclerView)findViewById(R.id.recyclerView_SearchHome);
        materialSearchBar=(MaterialSearchBar)findViewById(R.id.search_Bar_Home);//---Search*/



        //------------------Firebase-------------------------//
        database=FirebaseDatabase.getInstance();
        foodList =database.getReference("Foods");

        //------------------Recycler View------------------//
        recyclerViewFoods.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerViewFoods.setLayoutManager(layoutManager);

        //-------------------------------------
        localDatabe=new Database(this);


        //------------------ Search ----------//
        materialSearchBar.setHint("Enter your Food");
        // materialSearchBar.setSpeechMode(false);//if set to true, microphone icon will be displayed instead of search icon
        loadSuggest();//load suggest from firebase
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



        //LoadAllFood
        loadAllFoods();



    }

    private void loadAllFoods()
    {

        //Create Query by Category Id
        Query  searchById= foodList;

        options = new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(searchById, Food.class)
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
                holder.textFoodPrice.setText(String.format("$ %s", model.getPrice()));

                //Send Image  to Recyclerview
                Picasso.with(getApplicationContext())
                        .load(model.getImage())//Url
                        //  .networkPolicy(NetworkPolicy.OFFLINE)//تحميل الصوره Offline
                        //.placeholder(R.drawable.d)//الصوره الافتراضه اللى هتظهر لحد لما الصوره تتحمل
                        .into(holder.foodimageView);

                final Food clickItem = model;


                // Click to Share
                holder.shareImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (ShareDialog.canShow(ShareLinkContent.class)) {

                            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                    //Le asignamos una imagen para mostrar que subimos a internete
                                    .setImageUrl(Uri.parse(model.getImage()))
                                    //Un enlace al que se accederia clickando en Facebook
                                    .setContentUrl(Uri.parse(model.getImage()))
                                    .setContentDescription(model.getDescription())
                                    .setQuote("https://play.google.com/store/apps/developer?id=MBasuony_JA")
                                    .build();
                            shareDialog.show(linkContent);  // Show facebook ShareDialog
                        }
                        //  shareItem(model.getImage(),clickItem);
                    }
                });


                //Quick Cart
                holder.queckCartImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        boolean isExists = new Database(getBaseContext()).checkFoodExists(adapter.getRef(position).getKey(), Common.currentUser.getPhone());

                        if (!isExists){
                            //Send Data to Sqlite
                            new Database(getBaseContext()).addToCarts(new Order(
                                    Common.currentUser.getPhone(),
                                    adapter.getRef(position).getKey(),
                                    model.getName(),
                                    "1",
                                    model.getPrice(),
                                    model.getDiscount(),
                                    model.getImage())
                            );

                            Toast.makeText(SearchActivityHome.this, "Added To Cars", Toast.LENGTH_SHORT).show();

                        }else
                        {
                            new Database(getBaseContext()).increaseCart(adapter.getRef(position).getKey(), Common.currentUser.getPhone());

                            Toast.makeText(SearchActivityHome.this, "The quantity of the same food was increased", Toast.LENGTH_SHORT).show();

                        }

                    }
                });






                //Check food do Favorites or Not ,is Not Display Image Favorite border...
                if (localDatabe.isFoodFavorites(adapter.getRef(position).getKey(),Common.currentUser.getPhone()))
                    holder.favimage.setImageResource(R.drawable.ic_favorite_black_24dp);


                //if Click on Image Favorites
                holder.favimage.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {

                        Favorites favorites=new Favorites();

                        favorites.setFoodId(adapter.getRef(position).getKey());
                        favorites.setFoodName(model.getName());
                        favorites.setFoodPrice(model.getPrice());
                        favorites.setFoodMenuId(model.getMenuId());
                        favorites.setFoodImage(model.getImage());
                        favorites.setFoodDiscount(model.getDiscount());
                        favorites.setFoodDescription(model.getDescription());
                        favorites.setUserPhone(Common.currentUser.getPhone());


                        //Not Favorites
                        if (!localDatabe.isFoodFavorites(adapter.getRef(position).getKey(),Common.currentUser.getPhone()))
                        {
                            //Add Favorites
                            localDatabe.addToFavorites(favorites);

                            holder.favimage.setImageResource(R.drawable.ic_favorite_black_24dp);

                            Toast.makeText(SearchActivityHome.this, model.getName()+" was add Favorites", Toast.LENGTH_SHORT).show();

                        }else
                        { //IF FOOD Favorites
                            //Remove Food Favorites
                            localDatabe.removeFavorites(adapter.getRef(position).getKey(),Common.currentUser.getPhone());
                            holder.favimage.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                            Toast.makeText(SearchActivityHome.this, model.getName()+" Remove From Favorites", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                //لما المستخدم يضغط على اى صف
                holder.setItemClickListener(new ItemClickListener() {

                    @Override
                    public void onClick(View view, int position, boolean isLongClick)
                    {
                        //Start New Activity

                        Intent foodDeatilsIntent=new Intent(SearchActivityHome.this,FoodDetails.class);

                        foodDeatilsIntent.putExtra("FoodId", adapter.getRef(position).getKey());//send food id new Activity
                        startActivity(foodDeatilsIntent);
                    }
                });

            }//end OnBind
        };//end Adapter
        adapter.startListening();
        recyclerViewFoods.setAdapter(adapter);
     }


    //load suggest from firebase
    //Search
    private void loadSuggest()
    {
        //search on table Foods by menuId
        foodList.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {

                        for (DataSnapshot postSnapshot:dataSnapshot.getChildren())
                        {
                            Food foodItem=postSnapshot.getValue(Food.class);

                            suggestList.add(foodItem.getName()); //Add name of food to Suggest List
                        }

                        materialSearchBar.setLastSuggestions(suggestList);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    private void startSearch(CharSequence text) {
        //---Using Firebase UI to populate a RecyclerView--------//
        // query = foodList.orderByChild("Name").equalTo(text.toString()); //Compare Name

        //---Using Firebase UI to populate a RecyclerView--------//

        //Create Query By Name
        searchByName= foodList.orderByChild("name").equalTo(text.toString());
        //  query.keepSynced(true);//Load Data OffLine
        options = new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(searchByName, Food.class)
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
                Picasso.with(getApplicationContext())
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

                        Intent foodDeatilsIntent = new Intent(SearchActivityHome.this, FoodDetails.class);

                        foodDeatilsIntent.putExtra("FoodId", searchAdapter.getRef(position).getKey());//send food id new Activity
                        startActivity(foodDeatilsIntent);


                    }
                });


            }//end OnBind


        };//end Adapter


        searchAdapter.startListening();
        recyclerViewFoods.setAdapter(searchAdapter); //set Adapter for Recycler view is search resulet
    }



    //Start Adapter
    @Override
    protected void onStart() {
        super.onStart();
         //adapter.startListening();

    }

    //Stop Adapter
    @Override
    protected void onStop() {

        if (adapter!=null)
            adapter.stopListening();


        if (searchAdapter!=null)
            searchAdapter.stopListening();

        super.onStop();
    }
}
