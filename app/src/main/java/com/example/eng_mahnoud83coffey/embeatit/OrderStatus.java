package com.example.eng_mahnoud83coffey.embeatit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eng_mahnoud83coffey.embeatit.Common.Common;
import com.example.eng_mahnoud83coffey.embeatit.Interface.ItemClickListener;
import com.example.eng_mahnoud83coffey.embeatit.Model.Category;
import com.example.eng_mahnoud83coffey.embeatit.Model.Request;
import com.example.eng_mahnoud83coffey.embeatit.ViewHolder.MenuVIewHolder;
import com.example.eng_mahnoud83coffey.embeatit.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class OrderStatus extends AppCompatActivity {

    private RecyclerView recyclerViewOrderStatus;
    private RecyclerView.LayoutManager  layoutManager;
    //----------------------------------------------
    private FirebaseDatabase database;
    private DatabaseReference table_requests;
    //--------Firebase UI--------//
    private Query query;
    private FirebaseRecyclerOptions<Request> options;
    private FirebaseRecyclerAdapter adapter;




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);


        //------------------------Id-------------------------------//
         recyclerViewOrderStatus=(RecyclerView)findViewById(R.id.recyclerView_OrderStatus);

         //---------------------RecyclerView-----------------//
        recyclerViewOrderStatus.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerViewOrderStatus.setLayoutManager(layoutManager);


        //-----------------------Firebase------------------------//
        database=FirebaseDatabase.getInstance();
        table_requests=database.getReference("Requests");

        //-------------------Event-----------------

        //if we start orderStatus activity from Home Activity
        //we will not put any extra so we just LoadOrder by phone from common

        if (getIntent() == null)
                   loadOrderStatus(Common.currentUser.getPhone());
        else
                   loadOrderStatus(getIntent().getStringExtra("userPhone"));




    }


    private void loadOrderStatus(String phone)
    {


            //---Using Firebase UI to populate a RecyclerView--------//
            query = FirebaseDatabase.getInstance()
                    .getReference()
                    .child("Requests").orderByChild("phone").equalTo(phone);

            //.orderByChild("phone").equalTo(phone)

            query.keepSynced(true);//Load Data OffLine

            options = new FirebaseRecyclerOptions.Builder<Request>()
                    .setQuery(query, Request.class)
                    .build();

            adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(options) {
                @Override
                public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    // Create a new instance of the ViewHolder, in this case we are using a custom
                    // layout called R.layout.message for each item
                    View view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.orderstatus_item, parent, false);

                    return new OrderViewHolder(view);
                }

                @Override
                protected void onBindViewHolder(final OrderViewHolder holder, final int position, final Request model) {
                    // Bind the Chat object to the ChatHolder


                    //Send Image Name to Recyclerview
                    holder.textOrderId.setText(adapter.getRef(position).getKey());
                    holder.textOrderStatus.setText(Common.converCodeToStatus(model.getStatus()));
                    holder.textOrderPhone.setText(model.getPhone());
                    holder.textOrderAddress.setText(model.getAddress());


                    final Request clickItem = model;


                    //لما المستخدم يضغط على اى صف
                    holder.setItemClickListener(new ItemClickListener() {

                        @Override
                        public void onClick(View view, int position, boolean isLongClick) {
                            //Get CategoryId and send to new Activity


                        }
                    });


                }//end OnBind


            };//end Adapter

            recyclerViewOrderStatus.setAdapter(adapter);


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



}
