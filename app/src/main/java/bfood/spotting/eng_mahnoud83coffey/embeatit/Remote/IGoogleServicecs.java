package bfood.spotting.eng_mahnoud83coffey.embeatit.Remote;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;


//Get Your Location Address
public interface IGoogleServicecs
{

    @GET
    Call<String>getAddressName(@Url String url);

}
