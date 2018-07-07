package com.example.eng_mahnoud83coffey.embeatit.Remote;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClint
{

    private  static Retrofit retrofit=null;


    public static Retrofit getClint(String baseURL)
    {

        if (retrofit ==null)
        {
        retrofit=new Retrofit.Builder()
                                 .baseUrl(baseURL)
                                 .addConverterFactory(GsonConverterFactory.create())
                                 .build();
        }



        return retrofit;
    }


}
