package bfood.spotting.eng_mahnoud83coffey.embeatit;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.RelativeLayout;

import bfood.spotting.eng_mahnoud83coffey.embeatit.Common.Common;
import bfood.spotting.eng_mahnoud83coffey.embeatit.Database.Database;
import bfood.spotting.eng_mahnoud83coffey.embeatit.Helper.RecyclerItemTouchHeleper;
import bfood.spotting.eng_mahnoud83coffey.embeatit.Interface.RecyclerItemTouchHleperListiner;
import bfood.spotting.eng_mahnoud83coffey.embeatit.Model.Favorites;



import bfood.spotting.eng_mahnoud83coffey.embeatit.ViewHolder.FavoritesAdabter;
import bfood.spotting.eng_mahnoud83coffey.embeatit.ViewHolder.FavoritesViewHolder;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FavoritesActivity extends AppCompatActivity implements RecyclerItemTouchHleperListiner {

    //----------------------------------
    private FirebaseDatabase database;
    private DatabaseReference foodList; //using index
    //----------------------------------
    private RecyclerView recyclerViewFavorites;
    private RecyclerView.LayoutManager layoutManager;


    private FavoritesAdabter favoritesAdabter;
    private RelativeLayout rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        //-------------------Id----------------------------//
        recyclerViewFavorites=(RecyclerView)findViewById(R.id.recyclerView_Favorites);
        rootLayout=(RelativeLayout)findViewById(R.id.rootLayoutFavorites);


        //------------------Firebase-------------------------//
        database=FirebaseDatabase.getInstance();
        foodList =database.getReference("Foods");



        recyclerViewFavorites.setHasFixedSize(true);
         layoutManager=new LinearLayoutManager(this);
        recyclerViewFavorites.setLayoutManager(layoutManager);

       //------------------Recycler View Animation------------------//
        LayoutAnimationController controller= AnimationUtils.loadLayoutAnimation(recyclerViewFavorites.getContext(),R.anim.layout_animation_from_righit);
        recyclerViewFavorites.setLayoutAnimation(controller);


        //Swipe to delete
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback=new RecyclerItemTouchHeleper(0,ItemTouchHelper.LEFT,this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerViewFavorites);

         loadFavorites();

    }


    private void loadFavorites()
    {
      favoritesAdabter=new FavoritesAdabter(this,new Database(this).getAllFavorites(Common.currentUser.getPhone()));
      recyclerViewFavorites.setAdapter(favoritesAdabter);
    }


    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position)
    {
        if (viewHolder instanceof FavoritesViewHolder)
        {

            String name=((FavoritesAdabter)recyclerViewFavorites.getAdapter()).getItem(position).getFoodName();

            final Favorites deleteOrder=((FavoritesAdabter)recyclerViewFavorites.getAdapter()).getItem(viewHolder.getAdapterPosition());
            final int deleteIndex=viewHolder.getAdapterPosition();

            //*****Remove Item*******//
            favoritesAdabter.removeItem(deleteIndex);
            new Database(getBaseContext()).removeFavorites(deleteOrder.getFoodId(), Common.currentUser.getPhone());


            //*****UNDO Item*******//
            //Make SnakeBar
            Snackbar snackbar=Snackbar.make(rootLayout,name +"removed From Favorites",Snackbar.LENGTH_LONG);

            snackbar.setAction("UNDO", new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    favoritesAdabter.restoreItem(deleteOrder,deleteIndex);
                    new Database(getBaseContext()).addToFavorites(deleteOrder);

                }
            });

            snackbar.setActionTextColor(Color.GREEN);
            snackbar.show();

        }


    }
}
