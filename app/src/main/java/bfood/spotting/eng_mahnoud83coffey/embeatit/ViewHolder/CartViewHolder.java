package bfood.spotting.eng_mahnoud83coffey.embeatit.ViewHolder;


import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import bfood.spotting.eng_mahnoud83coffey.embeatit.Common.Common;
import bfood.spotting.eng_mahnoud83coffey.embeatit.Interface.ItemClickListener;
import bfood.spotting.eng_mahnoud83coffey.embeatit.R;


// رViewHolder عاديه عشان الاAdabter
//------------------View Holder-----------------//
public class  CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
                                                                                        ,View.OnCreateContextMenuListener{


    public TextView textCartName,textCartPrice;
    public ElegantNumberButton numberButtonCart;
    public ImageView image;
    private ItemClickListener itemClickListener; //Interface

    public RelativeLayout viewBackground;
    public LinearLayout viewForBackground;



    public CartViewHolder(View itemView)
    {
        super(itemView);

        textCartName=(TextView)itemView.findViewById(R.id.cart_item_name);
        textCartPrice=(TextView)itemView.findViewById(R.id.cart_item_price);
        numberButtonCart=(ElegantNumberButton) itemView.findViewById(R.id.number_button_Cart);
        image=(ImageView)itemView.findViewById(R.id.cart_image_row);

        viewBackground=(RelativeLayout)itemView.findViewById(R.id.view_background);
        viewForBackground=(LinearLayout)itemView.findViewById(R.id.view_forground);


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
        contextMenu.add(0,0,getAdapterPosition(), Common.DELETE);
    }
}



