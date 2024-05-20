package paeqw.app.interfaces;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface PlantIdentificationService {
    @Multipart
    @POST("/api/v3/identification")
    Call<ResponseBody> identifyPlant(
            @Header("Api-Key") String apiKey,
            @Part MultipartBody.Part image
    );
}