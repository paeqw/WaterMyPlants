package paeqw.app.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import paeqw.app.R;
import paeqw.app.activities.PlantDetailsActivity;
import paeqw.app.helpers.PlantResponse;
import paeqw.app.helpers.RetrofitClient;
import paeqw.app.interfaces.PlantApiService;
import paeqw.app.models.PlantApi;
import paeqw.app.models.Space;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SearchPlantFragment extends Fragment {
    private static final String TAG = "SearchPlantFragment";
    private static final String API_KEY = "sk-y3wB66450eb6dac165507";
    private LinearLayout linearLayout;
    private TextInputEditText searchField;
    private ProgressBar progressBar;
    private PlantApiService apiService;

    public SearchPlantFragment() {
        // Required empty public constructor
    }

    public static SearchPlantFragment newInstance() {
        SearchPlantFragment fragment = new SearchPlantFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate called");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called");
        View view = inflater.inflate(R.layout.fragment_search_plant, container, false);


        linearLayout = view.findViewById(R.id.linearLayout);
        searchField = view.findViewById(R.id.searchField);
        progressBar = view.findViewById(R.id.progressBar);  // Initialize ProgressBar

        Retrofit retrofit = RetrofitClient.getClient("https://perenual.com/");
        apiService = retrofit.create(PlantApiService.class);

        // Load default list initially
        searchPlants("");

        searchField.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String query = searchField.getText().toString();
                Log.d(TAG, "Search query: " + query);
                searchPlants(query);
                return true;
            }
            return false;
        });

        return view;
    }

    private void searchPlants(String query) {
        Log.d(TAG, "searchPlants called with query: " + query);
        showLoading(true);
        Call<PlantResponse> call;

        if (query.isEmpty()) {
            // Make the default API call if query is empty
            call = apiService.getPlants(API_KEY);
        } else {
            // Make the API call with the query
            call = apiService.getPlants(API_KEY, query);
        }

        Log.d(TAG, "Request URL: " + call.request().url());

        call.enqueue(new Callback<PlantResponse>() {
            @Override
            public void onResponse(Call<PlantResponse> call, Response<PlantResponse> response) {
                Log.d(TAG, "API call onResponse");
                if (response.isSuccessful() && response.body() != null) {
                    List<PlantApi> plants = response.body().getData();
                    Log.d(TAG, "Received plants: " + plants.size());
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> displayPlants(plants));
                    }
                } else {
                    Log.e(TAG, "Response unsuccessful or body is null");
                    if (response.errorBody() != null) {
                        try {
                            Log.e(TAG, "Error response: " + response.errorBody().string());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                showLoading(false);
            }

            @Override
            public void onFailure(Call<PlantResponse> call, Throwable t) {
                Log.e(TAG, "API call failed", t);
                showLoading(false);
            }
        });
    }

    private void displayPlants(List<PlantApi> plants) {
        Log.d(TAG, "Displaying plants");
        if (plants == null || plants.isEmpty()) {
            Log.d(TAG, "No plants to display");
            return;
        }

        linearLayout.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(getContext());

        for (PlantApi plant : plants) {
            Log.d(TAG, "Adding plant: " + plant.getCommonName());
            linearLayout.addView(createPlantView(plant, inflater, getContext()));
        }
    }

    private View createPlantView(PlantApi plant, LayoutInflater inflater, Context context) {
        View plantView = inflater.inflate(R.layout.plant_search_template, linearLayout, false);

        ShapeableImageView plantImage = plantView.findViewById(R.id.plant_image);
        TextView plantName = plantView.findViewById(R.id.plant_name);
        TextView scientificName = plantView.findViewById(R.id.scientific_name);

        plantName.setText(plant.getCommonName());
        scientificName.setText(TextUtils.join(", ", plant.getScientificName()));

        if (plant.getDefaultImage() != null && plant.getDefaultImage().getMediumUrl() != null) {
            Glide.with(this)
                    .load(plant.getDefaultImage().getMediumUrl())
                    .into(plantImage);
        } else {
            plantImage.setImageResource(R.drawable.placeholderimage);
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                GridLayout.LayoutParams.WRAP_CONTENT,
                GridLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        params.setMargins(10, 10, 10, 10);
        plantView.setLayoutParams(params);

        plantView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), PlantDetailsActivity.class);
                intent.putExtra("plant_id", plant.getId());
                startActivity(intent);
            }
        });

        return plantView;
    }

    private void showLoading(boolean isLoading) {
        Log.d(TAG, "showLoading: " + isLoading);
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        linearLayout.setVisibility(isLoading ? View.GONE : View.VISIBLE);
    }
}
