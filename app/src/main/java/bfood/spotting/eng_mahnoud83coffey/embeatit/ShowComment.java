package bfood.spotting.eng_mahnoud83coffey.embeatit;

import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import bfood.spotting.eng_mahnoud83coffey.embeatit.Common.Common;
import bfood.spotting.eng_mahnoud83coffey.embeatit.Model.Rating;



import bfood.spotting.eng_mahnoud83coffey.embeatit.ViewHolder.ShowCommentViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class ShowComment extends AppCompatActivity {



    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private FirebaseRecyclerAdapter<Rating,ShowCommentViewHolder> adapter;

    private FirebaseDatabase database;
    private DatabaseReference tableRating;

    private String foodId="";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_comment);


        //-------------Id------------------//
        recyclerView=(RecyclerView)findViewById(R.id.recyclerView_show_comment);
        mSwipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipe_show_comment);

        //---Initial Firebase-----------//
        database=FirebaseDatabase.getInstance();
        tableRating=database.getReference("Rating");

        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);


        //----Event
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh()
            {

                if (getIntent() !=null)
                    foodId=getIntent().getStringExtra(Common.FOOD_ID);

                    if (!foodId.isEmpty() &&foodId!=null)
                    {
                        Query query=tableRating.orderByChild("foodId").equalTo(foodId);

                        FirebaseRecyclerOptions<Rating>options=new FirebaseRecyclerOptions.Builder<Rating>()
                                                       .setQuery(query,Rating.class)
                                                       .build();

                        adapter=new FirebaseRecyclerAdapter<Rating, ShowCommentViewHolder>(options) {
                            @Override
                            protected void onBindViewHolder(@NonNull ShowCommentViewHolder holder, int position, @NonNull Rating model)
                            {

                                holder.userPhone.setText(model.getUserPhone());
                                holder.ratingBar.setRating(Float.parseFloat(model.getRating()));
                                holder.textComment.setText(model.getComment());
                            }

                            @NonNull
                            @Override
                            public ShowCommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_show_comment,parent,false);

                                return new ShowCommentViewHolder(view);

                            }
                        };

                        loadComment(foodId);

                    }else
                    {
                        Toast.makeText(ShowComment.this, "food id null", Toast.LENGTH_LONG).show();
                    }
            }

        });

        //Thread To Load Comment on First Lunch
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run()
            {

                mSwipeRefreshLayout.setRefreshing(true);


                if (getIntent() !=null)
                    foodId=getIntent().getStringExtra(Common.FOOD_ID);

                if (!foodId.isEmpty() &&foodId!=null)
                {
                    Query query=tableRating.child(foodId).orderByChild("foodId");

                    FirebaseRecyclerOptions<Rating>options=new FirebaseRecyclerOptions.Builder<Rating>()
                            .setQuery(query,Rating.class)
                            .build();

                    adapter=new FirebaseRecyclerAdapter<Rating, ShowCommentViewHolder>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull ShowCommentViewHolder holder, int position, @NonNull Rating model)
                        {

                            holder.userPhone.setText(model.getUserPhone());
                            holder.ratingBar.setRating(Float.parseFloat(model.getRating()));
                            holder.textComment.setText(model.getComment());
                        }

                        @NonNull
                        @Override
                        public ShowCommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_show_comment,parent,false);

                            return new ShowCommentViewHolder(view);

                        }
                    };

                   loadComment(foodId);

                }else
                    {
                        Toast.makeText(ShowComment.this, "food id null", Toast.LENGTH_LONG).show();
                    }
            }
        });




    }



    private void loadComment(String foodId)
    {
    adapter.startListening();
    recyclerView.setAdapter(adapter);
    adapter.notifyDataSetChanged();
    mSwipeRefreshLayout.setRefreshing(false);
    }



    @Override
    protected void onStop() {
        super.onStop();


        if (adapter!=null)
           adapter.stopListening();
    }




}
