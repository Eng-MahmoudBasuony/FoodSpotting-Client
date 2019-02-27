package bfood.spotting.eng_mahnoud83coffey.embeatit;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.Toast;

import bfood.spotting.eng_mahnoud83coffey.embeatit.Common.Common;
import bfood.spotting.eng_mahnoud83coffey.embeatit.Model.FeedbackDelivery;



import bfood.spotting.eng_mahnoud83coffey.embeatit.ViewHolder.FeedbackServiceViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class FeedbackService extends AppCompatActivity
{

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private FirebaseDatabase database;
    private DatabaseReference refFeedbackServiceTable;
    private DatabaseReference refSuccessfulRequestToClient;

    private FirebaseRecyclerAdapter<FeedbackDelivery,FeedbackServiceViewHolder>adapter;
    private FirebaseRecyclerOptions<FeedbackDelivery>options;

    private String rating="0";
    private String comment="Not Comment";

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_service);


        //------------Id----------------------//
        recyclerView=(RecyclerView)findViewById(R.id.recyclerView_feedback_service);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        progressDialog=new ProgressDialog(this);

        //-----------Init Firebase
        database=FirebaseDatabase.getInstance();
        refFeedbackServiceTable=database.getReference(Common.FEEDBACK_SERVICE).child(Common.currentUser.getPhone());
        refSuccessfulRequestToClient=database.getReference(Common.SUCESSfFUL_REQUEST_To_CLIENT);


        loadAllRatingFeedback();

    }

    private void loadAllRatingFeedback()
    {
        options=new FirebaseRecyclerOptions.Builder<FeedbackDelivery>()
                                            .setQuery(refFeedbackServiceTable,FeedbackDelivery.class)
                                             .build();

        adapter=new FirebaseRecyclerAdapter<FeedbackDelivery, FeedbackServiceViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FeedbackServiceViewHolder holder, final int position, @NonNull final FeedbackDelivery model)
            {


                holder.textShipperName.setText(model.getShipperName());

                final FeedbackDelivery feedbackDelivery=model;

                holder.ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener()
                {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float v, boolean b)
                    {
                        switch (String.valueOf(v))
                        {
                            case "1.0":
                               holder.textRating.setText(getResources().getString(R.string.Bad));
                                rating="1";
                             break;

                            case "2.0":
                                holder.textRating.setText(getResources().getString(R.string.Acceptable));
                                rating="2";
                                break;


                            case "3.0":
                                holder.textRating.setText(getResources().getString(R.string.good));
                                rating="3";
                                break;

                            case "4.0":
                                holder.textRating.setText(getResources().getString(R.string.very_good));
                                rating="4";
                                break;

                            case "5.0":
                                holder.textRating.setText(getResources().getString(R.string.Excellent));
                                rating="5";

                                break;

                             default:
                                 holder.textRating.setText(getResources().getString(R.string.not_rating));
                                 rating="0";
                              break;

                        }
                    }
                });



               holder.btnThankYou.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {

                       comment = holder.editTextComment.getText().toString();

                       if (comment!=null&&rating!=null&&!comment.isEmpty()&&!rating.isEmpty())
                       {

                       progressDialog.setMessage("Please What...");
                       progressDialog.show();

                       Map<String, Object> updatefeedback = new HashMap<>();
                       updatefeedback.put("commentClientForShipper", comment);
                       updatefeedback.put("ratingClientforShipper", rating);

                       //Update FeedBack from Table "SuccessfulRequestToClient"
                       refSuccessfulRequestToClient
                               .child(feedbackDelivery.getIdOrderShipper())
                               .updateChildren(updatefeedback)
                               .addOnSuccessListener(new OnSuccessListener<Void>() {
                                   @Override
                                   public void onSuccess(Void aVoid) {

                                       //Remove item FeedBack from Table "FeedbackService"
                                       refFeedbackServiceTable
                                               .child(adapter.getRef(position).getKey()) //order in Table FeedbackService
                                               .removeValue()
                                               .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                   @Override
                                                   public void onSuccess(Void aVoid) {

                                                       progressDialog.dismiss();
                                                       Toast.makeText(FeedbackService.this, "Thank you", Toast.LENGTH_SHORT).show();

                                                   }
                                               });

                                       adapter.notifyDataSetChanged();
                                   }
                               });

                       // adapter.notifyDataSetChanged();

                   }else
                       {
                           Toast.makeText(FeedbackService.this, "Please Enter Comment or Rating", Toast.LENGTH_SHORT).show();
                       }

                   }
               });

            }

            @NonNull
            @Override
            public FeedbackServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feedback_service,parent,false);

                return new FeedbackServiceViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onStart() {
        adapter.startListening();
        super.onStart();
    }

    @Override
    protected void onStop() {
        adapter.stopListening();
        super.onStop();
    }


}
