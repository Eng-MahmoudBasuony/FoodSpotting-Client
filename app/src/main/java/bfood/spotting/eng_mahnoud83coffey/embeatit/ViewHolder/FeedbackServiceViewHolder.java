package bfood.spotting.eng_mahnoud83coffey.embeatit.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import bfood.spotting.eng_mahnoud83coffey.embeatit.R;


public class FeedbackServiceViewHolder extends RecyclerView.ViewHolder {

    public RatingBar ratingBar;
    public TextView textRating;
    public TextView textShipperName;
    public EditText editTextComment;
    public Button btnThankYou;


    public FeedbackServiceViewHolder(View itemView)
    {
        super(itemView);

        ratingBar=(RatingBar)itemView.findViewById(R.id.ratingbar_feedback);
        textRating=(TextView)itemView.findViewById(R.id.text_rating_feedback);
        editTextComment=(EditText)itemView.findViewById(R.id.edittext_feedback);
        btnThankYou=(Button)itemView.findViewById(R.id.btn_feedback);
        textShipperName=(TextView)itemView.findViewById(R.id.text_shipper_name);


    }


}
