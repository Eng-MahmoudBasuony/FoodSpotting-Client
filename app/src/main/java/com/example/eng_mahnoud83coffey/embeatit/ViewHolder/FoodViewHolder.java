package com.example.eng_mahnoud83coffey.embeatit.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.eng_mahnoud83coffey.embeatit.Interface.ItemClickListener;
import com.example.eng_mahnoud83coffey.embeatit.R;


//الViewHoldr لعرض Table ال Foods الاطعمه اللى عندى فى ال RecyclerView بستخدام FirebaseUI مع استخدام CallBack Interface  لاكشن الضغط
public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{


    public TextView  textFoodName;
    public ImageView foodimageView;
    //-------------------------
    private ItemClickListener itemClickListener;// Interface


    public void setItemClickListener(ItemClickListener itemClickListener)
    {
        this.itemClickListener = itemClickListener;
    }

    public FoodViewHolder(View itemView)
    {
        super(itemView);


        textFoodName=(TextView)itemView.findViewById(R.id.food_name);
        foodimageView=(ImageView)itemView.findViewById(R.id.food_image);

        itemView.setOnClickListener(this);

    }

    @Override
    public void onClick(View view)
    {

        itemClickListener.onClick(view,getAdapterPosition(),false);

    }



}