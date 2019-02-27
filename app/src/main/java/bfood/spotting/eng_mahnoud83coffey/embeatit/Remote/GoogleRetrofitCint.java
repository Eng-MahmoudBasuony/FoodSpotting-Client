package bfood.spotting.eng_mahnoud83coffey.embeatit.Remote;

import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class GoogleRetrofitCint
{

    private  static Retrofit retrofit=null;

    //Get Your Address Location
    public static Retrofit getGoogleApiClint(String baseURL)
    {

        if (retrofit ==null)
        {
            retrofit=new Retrofit.Builder()
                    .baseUrl(baseURL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
        }

        return retrofit;
    }


}
