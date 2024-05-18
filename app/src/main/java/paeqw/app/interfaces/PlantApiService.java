package paeqw.app.interfaces;

import paeqw.app.helpers.PlantResponse;
import paeqw.app.models.CareGuideApi;
import paeqw.app.models.PlantApi;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PlantApiService {
    @GET("api/species-list")
    Call<PlantResponse> getPlants(@Query("key") String apiKey);

    @GET("api/species-list")
    Call<PlantResponse> getPlants(@Query("key") String apiKey, @Query("q") String query);

    @GET("api/species/details/{id}")
    Call<PlantApi> getPlantDetails(@Path("id") int id, @Query("key") String apiKey);

    @GET("api/species-care-guide-list")
    Call<CareGuideApi> getCareGuides(@Query("species_id") int plantId, @Query("key") String apiKey);
}
