package bfood.spotting.eng_mahnoud83coffey.embeatit;

import android.content.Context;


import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.support.design.widget.Snackbar;

import bfood.spotting.eng_mahnoud83coffey.embeatit.Common.Common;
import bfood.spotting.eng_mahnoud83coffey.embeatit.Interface.ItemClickListener;
 import bfood.spotting.eng_mahnoud83coffey.embeatit.Model.Request;



import bfood.spotting.eng_mahnoud83coffey.embeatit.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;


import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class OrderStatus extends AppCompatActivity {

    private RecyclerView recyclerViewOrderStatus;
    private RecyclerView.LayoutManager  layoutManager;
    //----------------------------------------------
    private FirebaseDatabase database;
    private DatabaseReference table_requests;
    //--------Firebase UI--------//
    private Query query;
    private FirebaseRecyclerOptions<Request> options;
    private FirebaseRecyclerAdapter<Request,OrderViewHolder> adapter;

    private RelativeLayout rootLayoutStatus;

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

        setContentView(R.layout.activity_order_status);


        //------------------------Id-------------------------------//
         recyclerViewOrderStatus=(RecyclerView)findViewById(R.id.recyclerView_OrderStatus);
         rootLayoutStatus=(RelativeLayout)findViewById(R.id.root_layout_status);

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

/*

            //---Using Firebase UI to populate a RecyclerView--------//
           Query query = FirebaseDatabase.getInstance()
                    .getReference()
                    .child("Requests").orderByChild("phone").equalTo(phone);

            //.orderByChild("phone").equalTo(phone)
*/

        Query getOrderByUser = table_requests.orderByChild("phoneClient").equalTo(Common.currentUser.getPhone());

            options = new FirebaseRecyclerOptions.Builder<Request>()
                    .setQuery(getOrderByUser, Request.class)
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
                    holder.textOrderPhone.setText(model.getPhoneClient());
                    holder.textOrderAddress.setText(model.getAddress());


                    final Request clickItem = model;

                   // Common.currentKeyOrderStatus=adapter.getRef(position).getKey();


                    //Cancel Order
                    holder.btnCancelOrder.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            if (adapter.getItem(position).getStatus().equals("0"))
                            { //Because not Shipping
                                deleteOrder(adapter.getRef(position).getKey());

                                //Check This Order in Table OrderNeedShippper
                            }
                            else{
                                    //Snackbar.make(findViewById(android.R.id.content),"you Cannot Cancel this Order",Snackbar.LENGTH_LONG).show();

                                 Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),"you Cannot Cancel This Order", Snackbar.LENGTH_LONG);

                                         snackbar.setActionTextColor(Color.RED);
                                         snackbar.setText("you Cannot Cancel This Order");
                                         snackbar.getView().setBackgroundColor(Color.RED);
                                         snackbar.show();
                            }

                        }
                    });


                    holder.btnTrackingShipper.setVisibility(View.GONE); //will hide the button and space acquired by button
                    //// holder.btnTrackingShipper.setVisibility(View.INVISIBLE); //will hide button only.
                    if (adapter.getItem(position).getStatus().equals("2")) //shipping
                    {

                        holder.btnTrackingShipper.setVisibility(View.VISIBLE); //Shaw Button

                        //open Map for Shaw RealTime Location to Shipper
                        holder.btnTrackingShipper.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view)
                            {
                                Intent intentTracing=new Intent(OrderStatus.this,TrackingShipper.class);
                                       intentTracing.putExtra("Latlng",model.getLatlng());
                                       intentTracing.putExtra("shipperPhone",model.getPhoneShipper());
                                       startActivity(intentTracing);
                            }
                        });
                    }else
                        {
                            holder.btnTrackingShipper.setVisibility(View.GONE); //will hide button only.
                        }


                    holder.setItemClickListener(new ItemClickListener() {
                        @Override
                        public void onClick(View view, int position, boolean isLongClick) {

                        }
                    });

                }//end OnBind


            };//end Adapter

            adapter.startListening();
            recyclerViewOrderStatus.setAdapter(adapter);


    }

    private void deleteOrder(String key)
    {
        //Delete Order From Table Requests
        table_requests.child(key).removeValue()
                      .addOnSuccessListener(new OnSuccessListener<Void>()
                      {
                          @Override
                          public void onSuccess(Void aVoid)
                          {

                              Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),"you Cannot Cancel This Order", Snackbar.LENGTH_LONG);

                              snackbar.setActionTextColor(Color.RED);
                              snackbar.setText("Done Cancel This Order");
                              snackbar.getView().setBackgroundColor(Color.GREEN);
                              snackbar.show();


                          }
                      });





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



     static class ThemedSnackbar {

        public static Snackbar make(View view, CharSequence text, int duration) {
            Snackbar snackbar = Snackbar.make(view, text, duration);
            snackbar.getView().setBackgroundColor(getAttribute(view.getContext(), R.attr.colorAccent));
            return snackbar;
        }


        public static Snackbar make(View view, int resId, int duration) {
            return make(view, view.getResources().getText(resId), duration);
        }


        private static int getAttribute(Context context, int resId) {
            TypedValue typedValue = new TypedValue();
            context.getTheme().resolveAttribute(resId, typedValue, true);
            return typedValue.data;
        }

    }


}
