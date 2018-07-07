package com.example.eng_mahnoud83coffey.embeatit.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.eng_mahnoud83coffey.embeatit.Model.User;
import com.example.eng_mahnoud83coffey.embeatit.Remote.ApiService;
import com.example.eng_mahnoud83coffey.embeatit.Remote.RetrofitClint;


//عشان لما اعوز اجيب بيانات المستخدم الحالى اوصلها من اى مكان فى المشروع
public class Common
{

    public static User currentUser;
    public static final String DELETE="Delete";
    public static final String USER_KEY="User";
    public static final String PWD_KEY="Password";

    //This link provides a reference for the HTTP syntax used to pass messages from your app server to client apps via Firebase Cloud Messaging.
    public static final String BASE_URL="https://fcm.googleapis.com";

    //  استخدام كائن الـ retrofit وربطه بالـ API الخاص بنا
    public static ApiService getFCMClinet()
    {
      return   RetrofitClint.getClint(BASE_URL).create(ApiService.class);
    }


 public static String converCodeToStatus(String Status)
 {
  if (Status.equals("0"))
   return "Placed"; //وضعت
  else if (Status.equals("1"))
   return "On my Way"; //انا في طريقي
  else return " shipped"; //تم شحنها

 }




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





}
