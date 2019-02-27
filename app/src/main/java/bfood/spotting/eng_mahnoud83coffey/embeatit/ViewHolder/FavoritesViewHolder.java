package bfood.spotting.eng_mahnoud83coffey.embeatit.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import bfood.spotting.eng_mahnoud83coffey.embeatit.Interface.ItemClickListener;
import bfood.spotting.eng_mahnoud83coffey.embeatit.R;


public class FavoritesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{


    public TextView favoritesFoodName;
    public ImageView favoritesFoodImage;
    public TextView favoritesFoodPrice;
    public ImageView favoritesCartImage;
    //-------------------------
    private ItemClickListener itemClickListener;// Interface

    public RelativeLayout viewFaoritesBackground;
    public LinearLayout viewFaoritesForBackground;

    public void setItemClickListener(ItemClickListener itemClickListener)
    {
        this.itemClickListener = itemClickListener;
    }

    public FavoritesViewHolder(View itemView)
    {
        super(itemView);

        favoritesFoodName=(TextView)itemView.findViewById(R.id.favorites_food_name);
        favoritesFoodImage=(ImageView)itemView.findViewById(R.id.favorites_food_image);
        favoritesFoodPrice=(TextView)itemView.findViewById(R.id.favorites_food_price);
        favoritesCartImage=(ImageView)itemView.findViewById(R.id.favorites_cart_image);

        viewFaoritesBackground=(RelativeLayout)itemView.findViewById(R.id.view_favorites_background);
        viewFaoritesForBackground=(LinearLayout)itemView.findViewById(R.id.view_favorites_forground);



        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {

        itemClickListener.onClick(view,getAdapterPosition(),false);

    }

}
