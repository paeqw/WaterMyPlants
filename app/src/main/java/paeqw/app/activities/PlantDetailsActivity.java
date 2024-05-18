package paeqw.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import paeqw.app.R;
import paeqw.app.collections.SpaceManager;
import paeqw.app.helpers.RetrofitClient;
import paeqw.app.interfaces.PlantApiService;
import paeqw.app.models.CareGuide;
import paeqw.app.models.CareGuideApi;
import paeqw.app.models.PlantApi;
import paeqw.app.helpers.AnimationUtils;
import paeqw.app.models.SharedViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PlantDetailsActivity extends AppCompatActivity {
    private SharedViewModel sharedViewModel;
    private SpaceManager spaceManager;
    private static final String TAG = "PlantDetailsActivity";
    private static final String API_KEY = "sk-y3wB66450eb6dac165507";
    private TextView commonNameTextView;
    private TextView scientificNameTextView;
    private TextView descriptionTextView;
    private LinearLayout detailsLayout;
    private LinearLayout careGuidesLayout;
    private ImageView bigImage;
    private LinearLayout horizontalSquares;
    private ImageView smallTopImage;
    private ImageView smallBottomImage;
    private PlantApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_details);

        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        Log.d(TAG, "SharedViewModel hash code: " + sharedViewModel.hashCode());

        spaceManager = sharedViewModel.getSpaceManager();

        if (spaceManager == null) {
            Log.e(TAG, "SpaceManager is null");
            Toast.makeText(this, "Failed to load SpaceManager", Toast.LENGTH_LONG).show();
            finish(); // Or any other appropriate error handling
            return;
        }

        Log.d(TAG, "SpaceManager size: " + spaceManager.getSpaceList().size());

        initViews();

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
        bigImage = findViewById(R.id.bigimage);
        smallTopImage = findViewById(R.id.smallTop);
        horizontalSquares = findViewById(R.id.horizontalSquares);
        smallBottomImage = findViewById(R.id.smalBottom);
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

        List<ImageView> imageViews = new ArrayList<>();
        imageViews.add(bigImage);
        imageViews.add(smallBottomImage);
        imageViews.add(smallTopImage);
        for (ImageView img : imageViews) {
            if (plant.getDefaultImage() != null && plant.getDefaultImage().getMediumUrl() != null) {
                Glide.with(this)
                        .load(plant.getDefaultImage().getMediumUrl())
                        .into(img);
            }
        }

        if (plant.getWateringGeneralBenchmark() != null) {
            addTextViewIfNotEmpty("Watering Benchmark: ", plant.getWateringGeneralBenchmark().getValue() + " " +
                    plant.getWateringGeneralBenchmark().getUnit());
        }

        addFrameWithText("Volume Water Requirement: ", TextUtils.join(", ", plant.getVolumeWaterRequirement()), R.drawable.water_drop_24px);
        addFrameWithText("Maintenance: ", plant.getMaintenance(), R.drawable.difficulty_meter_24px);
        addFrameWithText("Growth Rate: ", plant.getGrowthRate(), R.drawable.growth_rate_24px);
        addFrameWithText("Type: ", plant.getType(), R.drawable.potted_plant_24px);

        if (plant.getHardiness() != null) {
            addTextViewIfNotEmpty("Hardiness: ", plant.getHardiness().getMin() + " - " +
                    plant.getHardiness().getMax());
        }

        addFrameWithText("Pruning Month: ", TextUtils.join(", ", plant.getPruningMonth()), R.drawable.pruning_24px);
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

    private void addFrameWithText(String label, String text, int iconResId) {
        if (text != null && !text.isEmpty()) {
            FrameLayout frameLayout = createFrameLayout();

            ImageView imageView = new ImageView(this);
            int size24dp = (int) getResources().getDimension(R.dimen.size_24dp);
            imageView.setMaxWidth(size24dp);
            imageView.setMaxHeight(size24dp);
            imageView.setColorFilter(ContextCompat.getColor(this, R.color.white));
            imageView.setImageResource(iconResId);

            FrameLayout.LayoutParams iconParams = new FrameLayout.LayoutParams(size24dp, size24dp);
            iconParams.gravity = Gravity.TOP | Gravity.START;
            iconParams.setMargins(
                    getResources().getDimensionPixelSize(R.dimen.icon_margin),
                    getResources().getDimensionPixelSize(R.dimen.icon_margin),
                    0,
                    0
            );
            imageView.setLayoutParams(iconParams);

            TextView textView = new TextView(this);
            textView.setText(label + text);
            textView.setTextSize(16);
            textView.setTextColor(getResources().getColor(R.color.white, null));

            FrameLayout.LayoutParams textParams = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
            );
            textParams.gravity = Gravity.CENTER;
            textView.setLayoutParams(textParams);

            frameLayout.addView(imageView);
            frameLayout.addView(textView);

            horizontalSquares.addView(frameLayout);
        }
    }

    private FrameLayout createFrameLayout() {
        FrameLayout frameLayout = new FrameLayout(this);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                getResources().getDimensionPixelSize(R.dimen.frame_layout_width),
                getResources().getDimensionPixelSize(R.dimen.frame_layout_height)
        );
        params.setMargins(
                getResources().getDimensionPixelSize(R.dimen.frame_layout_margin),
                getResources().getDimensionPixelSize(R.dimen.frame_layout_margin),
                getResources().getDimensionPixelSize(R.dimen.frame_layout_margin),
                getResources().getDimensionPixelSize(R.dimen.frame_layout_margin)
        );
        frameLayout.setLayoutParams(params);

        int padding8dp = (int) getResources().getDimension(R.dimen.padding_8dp);
        frameLayout.setPadding(padding8dp, padding8dp, padding8dp, padding8dp);

        frameLayout.setBackground(getResources().getDrawable(R.drawable.rounded_square, null));
        frameLayout.setBackgroundTintList(getResources().getColorStateList(R.color.darkerGray, null));

        return frameLayout;
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
                sectionDescription.setVisibility(View.VISIBLE);
                sectionDescription.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        sectionDescription.getViewTreeObserver().removeOnPreDrawListener(this);
                        AnimationUtils.expandView(sectionDescription);
                        rotateIcon(expandIcon, 0f, 180f);
                        return true;
                    }
                });
            } else {
                AnimationUtils.collapseView(sectionDescription);
                rotateIcon(expandIcon, 180f, 0f);
            }
        });

        careGuidesLayout.addView(view);
    }

    private void rotateIcon(ImageView icon, float fromDegrees, float toDegrees) {
        icon.animate().rotation(toDegrees).setDuration(300).start();
    }
}
