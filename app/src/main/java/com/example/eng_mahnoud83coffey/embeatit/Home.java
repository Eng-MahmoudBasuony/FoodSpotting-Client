package com.example.eng_mahnoud83coffey.embeatit;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eng_mahnoud83coffey.embeatit.Common.Common;
import com.example.eng_mahnoud83coffey.embeatit.Interface.ItemClickListener;
import com.example.eng_mahnoud83coffey.embeatit.Model.Category;
import com.example.eng_mahnoud83coffey.embeatit.ViewHolder.MenuVIewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;


//كلاس Activity الرئيسيه اللى بعد تسجيل الدخول -وبعرض فيها Table ال Category اللى هيا فئات الاطعمه
public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private FirebaseDatabase database;
    private DatabaseReference category;
    //--------------------------------
    private TextView textFullName;
    private  FloatingActionButton fab;
    private View headerView;
    //-------------------------------
    private RecyclerView recyclerViewMenu;
    private RecyclerView.LayoutManager layoutManager;
    //--------Firebase UI--------//
    private Query query;
    private FirebaseRecyclerOptions<Category> options;
    private FirebaseRecyclerAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Menu");






        //---------------------Firebase-----------------------//
        database=FirebaseDatabase.getInstance();
        category=database.getReference("Category");




        //---------------------------Id----------------------------//
         fab = (FloatingActionButton) findViewById(R.id.fab_home);

         recyclerViewMenu=(RecyclerView)findViewById(R.id.recyclerView);





        //---------------------------Action Button-----------------------//
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent cartIntent=new Intent(Home.this,Cart.class);
                      startActivity(cartIntent);


            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
         ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
               this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
         drawer.addDrawerListener(toggle);
         toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);




       //--Set Name for User--//
         headerView=navigationView.getHeaderView(0);

         textFullName=(TextView)headerView.findViewById(R.id.text_Full_Name_User);

        textFullName.setText(Common.currentUser.getName());

        //--Load Menu--//
        recyclerViewMenu.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerViewMenu.setLayoutManager(layoutManager);

        loadMenu();






    }



     //Method Load Data From FirebaseDatabse AND sen Data to RecyclerView
    private void loadMenu()
    {


        //---Using Firebase UI to populate a RecyclerView--------//
        query= FirebaseDatabase.getInstance()
                .getReference()
                .child("Category");

          query.keepSynced(true);//Load Data OffLine

        options = new FirebaseRecyclerOptions.Builder<Category>()
                .setQuery(query, Category.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Category, MenuVIewHolder>(options) {
            @Override
            public MenuVIewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.menu_item, parent, false);

                return new MenuVIewHolder(view);
            }

            @Override
            protected void onBindViewHolder(final MenuVIewHolder holder, final int position, final Category model) {
                // Bind the Chat object to the ChatHolder

                         //Send Image Name to Recyclerview
                holder.textMenuName.setText(model.getName());

                //Send Image  to Recyclerview
                Picasso.get()
                        .load(model.getImage())//Url
                      //  .networkPolicy(NetworkPolicy.OFFLINE)//تحميل الصوره Offline
                       // .placeholder(R.drawable.d)//الصوره الافتراضه اللى هتظهر لحد لما الصوره تتحمل
                        .into(holder.imageView, new Callback()
                        {
                            @Override
                            public void onSuccess() {//لو الصوره اتحملت offline بنجاح

                            }
                            @Override
                            public void onError(Exception e)
                            {//لو الصوره ماتحملتشى Offline بنجاح اعرضها Online

                                Picasso.get()
                                        .load(model.getImage())
                                        .placeholder(R.drawable.d)//الصوره الافتراضه اللى هتظهر لحد لما الصوره تتحمل
                                        .into(holder.imageView);
                            }
                        });


                  final Category clickItem=model;


                //لما المستخدم يضغط على اى صف
                 holder.setItemClickListener(new ItemClickListener() {

                     @Override
                     public void onClick(View view, int position, boolean isLongClick)
                     {
                          //Get CategoryId and send to new Activity

                         Intent foodsListIntent=new Intent(Home.this,FoodList.class);

                               foodsListIntent.putExtra("CategoryId",adapter.getRef(position).getKey());//Just Get Key Of item
                               startActivity(foodsListIntent);


                      }
                 });



            }//end OnBind


        };//end Adapter

        recyclerViewMenu.setAdapter(adapter);
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






    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu) {
            // Handle the camera action
        } else if (id == R.id.nav_cart)
        {
          Intent intentCart=new Intent(Home.this,Cart.class);
                startActivity(intentCart);

        } else if (id == R.id.nav_orders)
        {
            Intent intentOrderStatus=new Intent(Home.this,OrderStatus.class);
            startActivity(intentOrderStatus);

        } else if (id == R.id.nav_logout)
        {
          //LogOut
            Intent  intentSignIn=new Intent(Home.this,SignIn.class);
                    intentSignIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intentSignIn);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
