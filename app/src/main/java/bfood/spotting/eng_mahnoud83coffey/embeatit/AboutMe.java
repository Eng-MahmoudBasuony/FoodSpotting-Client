package bfood.spotting.eng_mahnoud83coffey.embeatit;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;



public class AboutMe extends AppCompatActivity {

    private Button btnFacebook;
    private Button btnCall;
    private Button btnRate;
    private Button btnShareApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_me);


        //----------------ID--------------------------//
        btnFacebook = (Button) findViewById(R.id.btn_aboutme_facebook);
        btnRate = (Button) findViewById(R.id.btn_aboutme_rate);
        btnCall = (Button) findViewById(R.id.btn_aboutme_call);
        btnShareApp=(Button)findViewById(R.id.btn_aboutme_share_app);


        //-----------Event-------------//
        btnFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFacebook();

            }
        });


        btnRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                rateApp();
            }
        });


        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                onCallBtnClick();
            }
        });


        btnShareApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareApp();
            }
        });

    }


    private void openFacebook() {
        Intent intent;
        Toast.makeText(AboutMe.this, "Personal page of the Developer", Toast.LENGTH_LONG).show();
        try {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/" + "EMBasuony3"));
            startActivity(intent);

        } catch (Exception e) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/" + "EMBasuony"));
            startActivity(intent);

        }
    }

    private void rateApp()
    {

        Intent intent;
        Toast.makeText(AboutMe.this, "Personal page of the Developer", Toast.LENGTH_LONG).show();

            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=MBasuony_JA"));
            startActivity(intent);

    }


   //--------------------------------Call Phone---------------------------//
    private void onCallBtnClick()
    {
        if (Build.VERSION.SDK_INT < 23)
        {
            phoneCall();
        }else {

            if (ActivityCompat.checkSelfPermission(AboutMe.this,Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {

                phoneCall();

            }else {
                final String[] PERMISSIONS_STORAGE = {Manifest.permission.CALL_PHONE};
                //Asking request Permissions
                ActivityCompat.requestPermissions(AboutMe.this, PERMISSIONS_STORAGE, 9);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        boolean permissionGranted = false;

        switch(requestCode)
        {
            case 9:
                permissionGranted = grantResults[0]== PackageManager.PERMISSION_GRANTED;
                break;
        }

        if(permissionGranted)
        {
            phoneCall();
        }else {
            Toast.makeText(AboutMe.this, "You don't assign permission.", Toast.LENGTH_SHORT).show();
        }

    }

    private void phoneCall()
    {
        if (ActivityCompat.checkSelfPermission(AboutMe.this,Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:01013694426"));
            AboutMe.this.startActivity(callIntent);

        }else
            {
            Toast.makeText(AboutMe.this, "You don't assign permission.", Toast.LENGTH_SHORT).show();
        }
    }
   //--------------------------------------------------------------------//



    private void shareApp()
    {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.caffeyemb.basuony");

        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }


}
