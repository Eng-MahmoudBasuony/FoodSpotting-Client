package bfood.spotting.eng_mahnoud83coffey.embeatit.ViewHolder;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import bfood.spotting.eng_mahnoud83coffey.embeatit.Cart;
import bfood.spotting.eng_mahnoud83coffey.embeatit.Common.Common;
import bfood.spotting.eng_mahnoud83coffey.embeatit.Database.Database;
import bfood.spotting.eng_mahnoud83coffey.embeatit.Model.Order;
import bfood.spotting.eng_mahnoud83coffey.embeatit.R;

import com.squareup.picasso.Picasso;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;


// الAdabter عاديه عشان اعرض الطلبات المخزنه فى الSQlite فى الRecycler view الخاصه بActivity ال Cars
//-----------------Class Adabter-------------------//
public class CartAdabter extends RecyclerView.Adapter<CartViewHolder>
{


    private List<Order>listData;
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

        Picasso.with(cart).load(listData.get(position).getImage()).into(holder.image);

        final Locale locale=new Locale("en","US");
        NumberFormat fmt=NumberFormat.getInstance(locale);

        int price=(Integer.parseInt(listData.get(position).getPrice()))*(Integer.parseInt(listData.get(position).getQuentity()));

        holder.textCartPrice.setText(fmt.format(price));

        holder.textCartName.setText(listData.get(position).getProudactName());



        holder.numberButtonCart.setNumber(listData.get(position).getQuentity());


        holder.numberButtonCart.setOnValueChangeListener(
                new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue)
            {



                  Order order=listData.get(position);
                        order.setQuentity(String.valueOf(newValue));

                     new Database(cart).updateCart(order);
                Log.d("Coulom this =", ""+listData.get(position).getUserPhone());

                     //Update textTotalPrice
                     //Calculate Total Price
                       int totalPrice=0;

                List<Order> orders=new Database(cart).getCarts(Common.currentUser.getPhone());

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


    //********Swipe Delete Cart*********//
    public Order getItem(int postion)
    {
      return listData.get(postion);
    }

    public void removeItem(int position)
    {
        listData.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Order item,int position)
    {
        listData.add(position,item);
        notifyItemInserted(position);
    }


}
