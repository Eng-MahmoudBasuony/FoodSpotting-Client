package bfood.spotting.eng_mahnoud83coffey.embeatit.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import bfood.spotting.eng_mahnoud83coffey.embeatit.Model.Request;
import bfood.spotting.eng_mahnoud83coffey.embeatit.Model.User;
import bfood.spotting.eng_mahnoud83coffey.embeatit.Remote.ApiService;
import bfood.spotting.eng_mahnoud83coffey.embeatit.Remote.GoogleRetrofitCint;
import bfood.spotting.eng_mahnoud83coffey.embeatit.Remote.IGoogleServicecs;
import bfood.spotting.eng_mahnoud83coffey.embeatit.Remote.RetrofitClint;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;


//عشان لما اعوز اجيب بيانات المستخدم الحالى اوصلها من اى مكان فى المشروع
public class Common
{


    public static User currentUser;
    public static Request currentRequest;

    //-----------------Tables--------------------------------//
    public static final String DRIVER_LOCATION ="DriverLocation" ;
    public static final String FEEDBACK_SERVICE="FeedbackService";
    public static final String SUCESSfFUL_REQUEST_To_CLIENT="SuccessfulRequestToClient";
    public static final String RATING="Rating";
    public static final String FOODS="Foods";
    public static final String USER="User";
    public static final String MENU_IMAGE="MenuImage";



    public static final String DELETE="Delete";
    public static final String USER_KEY="User";
    public static final String PWD_KEY="Password";

    public static final String PHONE_TEXT="userPhone";

    public static final String FOOD_ID="foodId";

    public static final String toopicName="news";

    //This link provides a reference for the HTTP syntax used to pass messages from your app server to client apps via Firebase Cloud Messaging.
    public static final String BASE_URL="https://fcm.googleapis.com";

    //Api your Location
    public static final String Googel_Api_URL="https://maps.googleapis.com";


    //  استخدام كائن الـ retrofit وربطه بالـ API الخاص بنا
    public static ApiService getFCMClinet()
    {
      return   RetrofitClint.getClint(BASE_URL).create(ApiService.class);
    }

    //  استخدام كائن الـ retrofit وربطه بالـ API الخاص بنا
    public static IGoogleServicecs getGoogleMapsApi()
    {
        return GoogleRetrofitCint.getGoogleApiClint(Googel_Api_URL).create(IGoogleServicecs.class);
    }




    public static String converCodeToStatus(String Status)
    {
        String status;

        switch (Status)
        {

            case "0":
                status = "Placed";
                break;

            case "1":
                status="On my Way";
                break;

            case "2":
                status="shipping";
                break;

            default:
                status="no statue";
                break;

        }

        return status;
    }



 //Method Check Connected to Internet
 public static boolean IsConnectedToInternet(Context context)
  {


  ConnectivityManager connectivityManager= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

    if (connectivityManager !=null)
    {

     NetworkInfo [] networkInfos=connectivityManager.getAllNetworkInfo();


     if (networkInfos!=null)
     {

      for (int i=0;i<networkInfos.length;i++)
      {
          if (networkInfos[i].getState()==NetworkInfo.State.CONNECTED)
          {
           return true;
          }
      }

     }

    }

    return false;
 }

   //This Function will convert Currency to number base on Location
 public static BigDecimal formatCurrency(String amount, Locale locale) throws ParseException
  {
        NumberFormat format=NumberFormat.getCurrencyInstance(locale);

        if (format instanceof DecimalFormat)
            ((DecimalFormat)format).setParseBigDecimal(true);

        return (BigDecimal)format.parse(amount.replace("[^\\d.,]",""));
    }



}
