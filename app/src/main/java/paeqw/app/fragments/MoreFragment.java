package paeqw.app.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import paeqw.app.R;
import paeqw.app.activities.LoginActivity;
import paeqw.app.collections.SpaceManager;
import paeqw.app.helpers.RetrofitClient;
import paeqw.app.helpers.SharedPreferencesHelper;
import paeqw.app.interfaces.PlantIdentificationService;
import paeqw.app.models.Plant;
import paeqw.app.models.Space;
import paeqw.app.models.UsageInfoResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoreFragment extends Fragment {
    public MoreFragment() {
        // Required empty public constructor
    }

    Button button;
    TextView textView;
    TextView plantcount;
    TextView creditsRemaining;
    ImageView profileImage;

    public static MoreFragment newInstance() {
        MoreFragment fragment = new MoreFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        View rootView = inflater.inflate(R.layout.fragment_more, container, false);
        initViews(rootView);
        Glide.with(rootView).load(firebaseUser.getPhotoUrl()).apply(RequestOptions.circleCropTransform()).into(profileImage);
        textView.setText(firebaseUser.getDisplayName());
        SpaceManager spaceManager = new SpaceManager(getContext());
        spaceManager.loadFromDatabase().thenRun(() -> {
            int plantcountt = 0;
            for (Space spac : spaceManager.getSpaceList()) {
                for (Plant pla : spac.getPlantList()) {
                    plantcountt++;
                }
            }
            plantcount.setText("Plants added: " + plantcountt);
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(getContext());
                sharedPreferencesHelper.clearSpaces();
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });

        // Fetch and display credits info
        fetchAndDisplayCreditsInfo();

        return rootView;
    }

    private void initViews(View rootView) {
        button = rootView.findViewById(R.id.buttonLogout);
        textView = rootView.findViewById(R.id.textViewUsernameLabel);
        profileImage = rootView.findViewById(R.id.rounded_image_view);
        plantcount = rootView.findViewById(R.id.plant_count);
        creditsRemaining = rootView.findViewById(R.id.credits_remaining);
    }

    private void fetchAndDisplayCreditsInfo() {
        RetrofitClient retrofitClient = new RetrofitClient("https://plant.id");
        PlantIdentificationService apiService = retrofitClient.getRetrofit().create(PlantIdentificationService.class);

        Call<UsageInfoResponse> call = apiService.getUsageInfo("PC8mewzTR9DzXa2Lu6OLKDjAvgIP4yShsnUwoXUowrGZuyJE8P");
        call.enqueue(new Callback<UsageInfoResponse>() {
            @Override
            public void onResponse(Call<UsageInfoResponse> call, Response<UsageInfoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int remainingCredits = response.body().getRemaining().getTotal();
                    creditsRemaining.setText("Credits for identification remaining:" + remainingCredits);
                } else {
                    creditsRemaining.setText("Failed to fetch credits info");
                }
            }

            @Override
            public void onFailure(Call<UsageInfoResponse> call, Throwable t) {
                creditsRemaining.setText("Error: " + t.getMessage());
            }
        });
    }
}