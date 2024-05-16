package paeqw.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import paeqw.app.R;
import paeqw.app.helpers.PlantResponse;
import paeqw.app.helpers.RetrofitClient;
import paeqw.app.interfaces.PlantApiService;
import paeqw.app.models.PlantApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PlantDetailsActivity extends AppCompatActivity {
    private static final String TAG = "PlantDetailsActivity";
    private static final String API_KEY = "sk-y3wB66450eb6dac165507";

    private TextView commonNameTextView;
    private TextView scientificNameTextView;
    private TextView familyTextView;
    private TextView descriptionTextView;
    // Add other TextView fields here

    private PlantApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_details);

        initViews();

        Retrofit retrofit = RetrofitClient.getClient("https://perenual.com/");
        apiService = retrofit.create(PlantApiService.class);

        Intent intent = getIntent();
        int plantId = intent.getIntExtra("plant_id", -1);

        if (plantId != -1) {
            fetchPlantDetails(plantId);
        }
    }

    private void initViews() {
        commonNameTextView = findViewById(R.id.common_name);
        scientificNameTextView = findViewById(R.id.scientific_name);
        familyTextView = findViewById(R.id.family);
        descriptionTextView = findViewById(R.id.description);
    }

    private void fetchPlantDetails(int plantId) {
        Call<PlantApi> call = apiService.getPlantDetails(plantId, API_KEY);
        call.enqueue(new Callback<PlantApi>() {
            @Override
            public void onResponse(Call<PlantApi> call, Response<PlantApi> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PlantApi plant = response.body();
                    displayPlantDetails(plant);
                } else {
                    Log.e(TAG, "Response unsuccessful or body is null");
                }
            }

            @Override
            public void onFailure(Call<PlantApi> call, Throwable t) {
                Log.e(TAG, "API call failed", t);
            }
        });
    }

    private void displayPlantDetails(PlantApi plant) {
        commonNameTextView.setText(plant.getCommonName());
        scientificNameTextView.setText(TextUtils.join(", ", plant.getScientificName()));
        familyTextView.setText(plant.getFamily());
        descriptionTextView.setText(plant.getDescription());
    }
}
