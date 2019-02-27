package bfood.spotting.eng_mahnoud83coffey.embeatit.ViewHolder;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import bfood.spotting.eng_mahnoud83coffey.embeatit.Interface.ItemClickListener;
import bfood.spotting.eng_mahnoud83coffey.embeatit.R;


//الView Holder ده ل الاكتيفيتى OrderStatus
public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{


    public TextView textOrderId,textOrderStatus,textOrderPhone,textOrderAddress;
    public ImageView btnCancelOrder;
    public Button btnTrackingShipper;

    private ItemClickListener itemClickListener;


    public OrderViewHolder(View itemView)
    {
        super(itemView);

        textOrderId=(TextView)itemView.findViewById(R.id.order_status_Id);
        textOrderStatus=(TextView)itemView.findViewById(R.id.order_status);
        textOrderPhone=(TextView)itemView.findViewById(R.id.order_status_phone);
        textOrderAddress=(TextView)itemView.findViewById(R.id.order_status_address);
        btnCancelOrder=(ImageView)itemView.findViewById(R.id.btn_delete_status);
        btnTrackingShipper=(Button)itemView.findViewById(R.id.btn_shipping_tracking);

        itemView.setOnClickListener(this);
    }


    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view)
    {
      this.itemClickListener.onClick(view,getAdapterPosition(),false);

    }








}
