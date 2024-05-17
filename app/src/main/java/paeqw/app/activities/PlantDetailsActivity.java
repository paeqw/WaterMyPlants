package paeqw.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import paeqw.app.R;
import paeqw.app.helpers.RetrofitClient;
import paeqw.app.interfaces.PlantApiService;
import paeqw.app.models.CareGuide;
import paeqw.app.models.CareGuideApi;
import paeqw.app.models.PlantApi;
import paeqw.app.helpers.AnimationUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PlantDetailsActivity extends AppCompatActivity {
    private static final String TAG = "PlantDetailsActivity";
    private static final String API_KEY = "sk-y3wB66450eb6dac165507";

    private TextView commonNameTextView;
    private TextView scientificNameTextView;
    private TextView descriptionTextView;
    private LinearLayout detailsLayout;
    private LinearLayout careGuidesLayout;

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
            fetchCareGuides(plantId);
        }
    }

    private void initViews() {
        commonNameTextView = findViewById(R.id.common_name);
        scientificNameTextView = findViewById(R.id.scientific_name);
        descriptionTextView = findViewById(R.id.description);
        detailsLayout = findViewById(R.id.details_layout);
        careGuidesLayout = findViewById(R.id.care_guides_layout);
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
        descriptionTextView.setText(plant.getDescription());

        if (plant.getWateringGeneralBenchmark() != null) {
            addTextViewIfNotEmpty("Watering Benchmark: ", plant.getWateringGeneralBenchmark().getValue() + " " +
                    plant.getWateringGeneralBenchmark().getUnit());
        }

        addTextViewIfNotEmpty("Volume Water Requirement: ", TextUtils.join(", ", plant.getVolumeWaterRequirement()));
        addTextViewIfNotEmpty("Maintenance: ", plant.getMaintenance());
        addTextViewIfNotEmpty("Growth Rate: ", plant.getGrowthRate());
        addTextViewIfNotEmpty("Type: ", plant.getType());

        if (plant.getHardiness() != null) {
            addTextViewIfNotEmpty("Hardiness: ", plant.getHardiness().getMin() + " - " +
                    plant.getHardiness().getMax());
        }

        addTextViewIfNotEmpty("Pruning Month: ", TextUtils.join(", ", plant.getPruningMonth()));
        addTextViewIfNotEmpty("Care Guides: ", plant.getCareGuides());
    }

    private void addTextViewIfNotEmpty(String label, String text) {
        if (text != null && !text.isEmpty()) {
            TextView textView = new TextView(this);
            textView.setText(label + text);
            textView.setTextSize(16);
            textView.setTextColor(getResources().getColor(R.color.white, null));
            detailsLayout.addView(textView);
        }
    }

    private void fetchCareGuides(int plantId) {
        Call<CareGuideApi> call = apiService.getCareGuides(plantId, API_KEY);
        call.enqueue(new Callback<CareGuideApi>() {
            @Override
            public void onResponse(Call<CareGuideApi> call, Response<CareGuideApi> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<CareGuide> careGuides = response.body().getData();
                    displayCareGuides(careGuides);
                } else {
                    Log.e(TAG, "Care Guide Response unsuccessful or body is null");
                }
            }

            @Override
            public void onFailure(Call<CareGuideApi> call, Throwable t) {
                Log.e(TAG, "Care Guide API call failed", t);
            }
        });
    }

    private void displayCareGuides(List<CareGuide> careGuides) {
        if (careGuides == null || careGuides.isEmpty()) {
            Log.d(TAG, "No care guides to display");
            return;
        }

        for (CareGuide careGuide : careGuides) {
            for (CareGuide.Section section : careGuide.getSection()) {
                addExpandableSection(section.getType(), section.getDescription());
            }
        }
    }

    private void addExpandableSection(String title, String description) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.item_expandable_section, null);

        TextView sectionTitle = view.findViewById(R.id.section_title);
        TextView sectionDescription = view.findViewById(R.id.section_description);
        ImageView expandIcon = view.findViewById(R.id.expand_icon);

        sectionTitle.setText(title);
        sectionDescription.setText(description);

        sectionTitle.setOnClickListener(v -> {
            if (sectionDescription.getVisibility() == View.GONE) {
                sectionDescription.setVisibility(View.VISIBLE); // Ensure visibility is set before measuring
                sectionDescription.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        sectionDescription.getViewTreeObserver().removeOnPreDrawListener(this);
                        AnimationUtils.expandView(sectionDescription);
                        rotateIcon(expandIcon, 0f, 180f); // Rotate to face up
                        return true;
                    }
                });
            } else {
                AnimationUtils.collapseView(sectionDescription);
                rotateIcon(expandIcon, 180f, 0f); // Rotate to face down
            }
        });

        careGuidesLayout.addView(view);
    }


    private void rotateIcon(ImageView icon, float fromDegrees, float toDegrees) {
        icon.animate().rotation(toDegrees).setDuration(300).start();
    }
}
