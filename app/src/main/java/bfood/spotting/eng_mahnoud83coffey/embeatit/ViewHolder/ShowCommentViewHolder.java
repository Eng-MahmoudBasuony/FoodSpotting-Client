package bfood.spotting.eng_mahnoud83coffey.embeatit.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import bfood.spotting.eng_mahnoud83coffey.embeatit.R;


public class ShowCommentViewHolder extends RecyclerView.ViewHolder{


   public TextView userPhone;
   public TextView textComment;
   public RatingBar ratingBar;

    public ShowCommentViewHolder(View itemView)
    {
        super(itemView);

        userPhone=(TextView)itemView.findViewById(R.id.phone_show_comment);
        textComment=(TextView)itemView.findViewById(R.id.textcomment_show_comment);
        ratingBar=(RatingBar)itemView.findViewById(R.id.rating_show_comment);

    }


}
