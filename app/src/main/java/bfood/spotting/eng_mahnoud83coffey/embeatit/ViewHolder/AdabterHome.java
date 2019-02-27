package bfood.spotting.eng_mahnoud83coffey.embeatit.ViewHolder;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import bfood.spotting.eng_mahnoud83coffey.embeatit.FoodList;
import bfood.spotting.eng_mahnoud83coffey.embeatit.Model.Category;
import bfood.spotting.eng_mahnoud83coffey.embeatit.R;

import com.squareup.picasso.Picasso;

import java.util.List;


//Adabter Home Activity
public class AdabterHome extends RecyclerView.Adapter<AdabterHome.ViewHolderHome>
{

   private List<Category>categories;
   private Context context;

    public AdabterHome(Context context,List<Category>categories)
    {
        this.categories=categories;
        this.context=context;

    }


    @NonNull
    @Override
    public ViewHolderHome onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {

        View view= LayoutInflater.from(context).inflate(R.layout.menu_item,parent,false);

        return new ViewHolderHome(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderHome holder, int position)
    {
        Category category  =categories.get(position);


       holder.menu_name.setText(category.getName());
        //  holder.menu_Image.setImageResource(category.getImage());
        Picasso.with(context).load(category.getImage()).into(holder.menu_Image);

    }

    @Override
    public int getItemCount()
    {
        return categories.size();
    }

    //View Holder
    class ViewHolderHome extends RecyclerView.ViewHolder
  {

      TextView menu_name;
      ImageView menu_Image;

      public ViewHolderHome(View itemView)
      {
          super(itemView);

          menu_name=(TextView)itemView.findViewById(R.id.menu_name);
          menu_Image=(ImageView)itemView.findViewById(R.id.menu_image);


          itemView.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view)
              {
                  Category category=categories.get(getAdapterPosition());

                  Intent intent=new Intent(context, FoodList.class);

                  intent.putExtra("CategoryId",category.getMenuId());

                  context.startActivity(intent);


              }
          });
      }




  }


}
