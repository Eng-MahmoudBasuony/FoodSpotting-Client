package bfood.spotting.eng_mahnoud83coffey.embeatit.Service;


import android.util.Log;

import bfood.spotting.eng_mahnoud83coffey.embeatit.Common.Common;
import bfood.spotting.eng_mahnoud83coffey.embeatit.Model.Token;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService
{

    @Override
    public void onTokenRefresh()
    {
        super.onTokenRefresh();

        String tokenRefershed= FirebaseInstanceId.getInstance().getToken();

        if (Common.currentUser !=null)
               updateTokenFirebase(tokenRefershed);

        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("Token", "Refreshed token: " + refreshedToken);

    }

    private void updateTokenFirebase(String tokenRefershed)
    {

        FirebaseDatabase db=FirebaseDatabase.getInstance();
        DatabaseReference Tokens=db.getReference("Tokens");


        Token token=new Token(tokenRefershed,false); //parser isServerToken false because Clint

         Tokens.child(Common.currentUser.getPhone()).setValue(token);

    }





}
