package com.example.eng_mahnoud83coffey.embeatit.ViewHolder;


import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.eng_mahnoud83coffey.embeatit.Cart;
import com.example.eng_mahnoud83coffey.embeatit.Common.Common;
import com.example.eng_mahnoud83coffey.embeatit.Database.Database;
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
class  CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
                                                                    ,View.OnCreateContextMenuListener{


    public TextView textCartName,textCartPrice;
    public ElegantNumberButton numberButtonCart;
    private ItemClickListener itemClickListener; //Interface


    public CartViewHolder(View itemView)
    {
        super(itemView);

        textCartName=(TextView)itemView.findViewById(R.id.cart_item_name);
        textCartPrice=(TextView)itemView.findViewById(R.id.cart_item_price);
        numberButtonCart=(ElegantNumberButton) itemView.findViewById(R.id.number_button_Cart);

       itemView.setOnCreateContextMenuListener(this);
    }

    public void setTextCartName(TextView textCartName) {
        this.textCartName = textCartName;
    }


    @Override
    public void onClick(View view) {

    }

     @Override
     public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo)
     {
          contextMenu.setHeaderTitle("Select action ");
          contextMenu.add(0,0,getAdapterPosition(),Common.DELETE);
     }
 }





// الAdabter عاديه عشان اعرض الطلبات المخزنه فى الSQlite فى الRecycler view الخاصه بActivity ال Cars
//-----------------Class Adabter-------------------//
public class CartAdabter extends RecyclerView.Adapter<CartViewHolder>
{


    private List<Order>listData=new ArrayList<>();
    private Cart cart;

    public CartAdabter(List<Order> listData, Cart cart)
    {
        this.listData = listData;
        this.cart = cart;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        LayoutInflater layoutInflater=LayoutInflater.from(cart);

        View view=layoutInflater.inflate(R.layout.cart_item_layout,parent,false);




        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, final int position)
    {

      /*  TextDrawable textDrawable=TextDrawable.builder()
                                  .buildRound(""+listData.get(position).getQuentity(), Color.RED);

        holder.imageCartCount.setImageDrawable(textDrawable);*/


        final Locale locale=new Locale("en","US");
        NumberFormat fmt=NumberFormat.getInstance(locale);

        int price=(Integer.parseInt(listData.get(position).getPrice()))*(Integer.parseInt(listData.get(position).getQuentity()));

        holder.textCartPrice.setText(fmt.format(price));

        holder.textCartName.setText(listData.get(position).getProudactName());



        holder.numberButtonCart.setNumber(listData.get(position).getQuentity());
        holder.numberButtonCart.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue)
            {

                  Order order=listData.get(position);
                       order.setQuentity(String.valueOf(newValue));

                       new Database(cart).updateCart(order);

                       int totalPrice=0;
                       List<Order>orders=new Database(cart).getCarts();

                       for (Order item:orders)
                       {
                           totalPrice+=(Integer.parseInt(order.getPrice()))*(Integer.parseInt(item.getQuentity()));
                       }

                 Locale locale1=new Locale("en","US");
                 NumberFormat numberFormat=NumberFormat.getCurrencyInstance(locale1);
                 cart.textTotalPrice.setText(numberFormat.format(totalPrice));


            }
        });


    }

    @Override
    public int getItemCount() {
        return listData.size();
    }
}
