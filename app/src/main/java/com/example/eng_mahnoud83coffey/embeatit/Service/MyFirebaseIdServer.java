package com.example.eng_mahnoud83coffey.embeatit.Service;


import com.example.eng_mahnoud83coffey.embeatit.Common.Common;
import com.example.eng_mahnoud83coffey.embeatit.Model.Token;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseIdServer extends FirebaseInstanceIdService
{

    @Override
    public void onTokenRefresh()
    {
        super.onTokenRefresh();

        String tokenRefershed= FirebaseInstanceId.getInstance().getToken();

        if (Common.currentUser !=null)
               updateTokenFirebase(tokenRefershed);

    }

    private void updateTokenFirebase(String tokenRefershed)
    {

        FirebaseDatabase db=FirebaseDatabase.getInstance();
        DatabaseReference Tokens=db.getReference("Tokens");


        Token token=new Token(tokenRefershed,false); //parser isServerToken false because Clint

         Tokens.child(Common.currentUser.getPhone()).setValue(token);

    }



}
