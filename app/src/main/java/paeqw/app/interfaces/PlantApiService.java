package paeqw.app.interfaces;

import paeqw.app.helpers.PlantResponse;
import paeqw.app.models.PlantApi;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PlantApiService {
    @GET("api/species-list")
    Call<PlantResponse> getPlants(
            @Query("key") String apiKey,
            @Query("q") String query
    );
    @GET("api/species/details/{id}")
    Call<PlantApi> getPlantDetails(@Path("id") int id, @Query("key") String apiKey);
}


