package com.example.eng_mahnoud83coffey.embeatit.ViewHolder;


import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.example.eng_mahnoud83coffey.embeatit.Interface.ItemClickListener;
import com.example.eng_mahnoud83coffey.embeatit.Model.Order;
import com.example.eng_mahnoud83coffey.embeatit.R;
import com.google.android.gms.common.api.Api;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


 // رViewHolder عاديه عشان الاAdabter
//------------------View Holder-----------------//
class  CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{


    public TextView textCartName,textCartPrice;
    public ImageView imageCartCount;
    private ItemClickListener itemClickListener; //Interface


    public CartViewHolder(View itemView)
    {
        super(itemView);

        textCartName=(TextView)itemView.findViewById(R.id.cart_item_name);
        textCartPrice=(TextView)itemView.findViewById(R.id.cart_item_price);
        imageCartCount=(ImageView)itemView.findViewById(R.id.cart_item_count);


    }

    public void setTextCartName(TextView textCartName) {
        this.textCartName = textCartName;
    }


    @Override
    public void onClick(View view) {

    }

}





// الAdabter عاديه عشان اعرض الطلبات المخزنه فى الSQlite فى الRecycler view الخاصه بActivity ال Cars
//-----------------Class Adabter-------------------//
public class CartAdabter extends RecyclerView.Adapter<CartViewHolder>
{


    private List<Order>listData=new ArrayList<>();
    private Context context;

    public CartAdabter(List<Order> listData, Context context)
    {
        this.listData = listData;
        this.context = context;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        LayoutInflater layoutInflater=LayoutInflater.from(context);

        View view=layoutInflater.inflate(R.layout.cart_item_layout,parent,false);




        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position)
    {
        TextDrawable textDrawable=TextDrawable.builder()
                                  .buildRound(""+listData.get(position).getQuentity(), Color.RED);

        holder.imageCartCount.setImageDrawable(textDrawable);


        Locale locale=new Locale("en","US");
        NumberFormat fmt=NumberFormat.getInstance(locale);

        int price=(Integer.parseInt(listData.get(position).getPrice()))*(Integer.parseInt(listData.get(position).getQuentity()));
        holder.textCartPrice.setText(fmt.format(price));

        holder.textCartName.setText(listData.get(position).getProudactName());


    }

    @Override
    public int getItemCount() {
        return listData.size();
    }
}
