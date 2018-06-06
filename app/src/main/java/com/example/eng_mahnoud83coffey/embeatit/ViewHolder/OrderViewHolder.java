package com.example.eng_mahnoud83coffey.embeatit.ViewHolder;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.eng_mahnoud83coffey.embeatit.Interface.ItemClickListener;
import com.example.eng_mahnoud83coffey.embeatit.R;

//الView Holder ده ل الاكتيفيتى OrderStatus
public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{


    public TextView textOrderId,textOrderStatus,textOrderPhone,textOrderAddress;

    private ItemClickListener itemClickListener;


    public OrderViewHolder(View itemView)
    {
        super(itemView);

        textOrderId=(TextView)itemView.findViewById(R.id.order_status_Id);
        textOrderStatus=(TextView)itemView.findViewById(R.id.order_status);
        textOrderPhone=(TextView)itemView.findViewById(R.id.order_status_phone);
        textOrderAddress=(TextView)itemView.findViewById(R.id.order_status_address);


        itemView.setOnClickListener(this);
    }


    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view)
    {
      itemClickListener.onClick(view,getAdapterPosition(),false);

    }








}
