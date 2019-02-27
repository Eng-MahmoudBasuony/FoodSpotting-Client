package bfood.spotting.eng_mahnoud83coffey.embeatit.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import bfood.spotting.eng_mahnoud83coffey.embeatit.Interface.ItemClickListener;
import bfood.spotting.eng_mahnoud83coffey.embeatit.R;




//ال ViewHolder لعرض Table الCategory فى الرRecycler view بستخدام FirebaseUI ,و ايضا استخدام الCallBack الInterFace عشان اكشن الضغط على الItem
public class MenuVIewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


    public TextView textMenuName;
    public ImageView imageView;
    //-------------------------
    private ItemClickListener itemClickListener;// Interface






    public MenuVIewHolder(View itemView) {
        super(itemView);

        textMenuName=(TextView)itemView.findViewById(R.id.menu_name);
        imageView=(ImageView)itemView.findViewById(R.id.menu_image);





        itemView.setOnClickListener(this);

    }

    @Override
    public void onClick(View view)
    {

        itemClickListener.onClick(view,getAdapterPosition(),false);
    }

          //Setter ItemClickListiner
    public void setItemClickListener(ItemClickListener itemClickListener)
    {
        this.itemClickListener = itemClickListener;


    }




}
