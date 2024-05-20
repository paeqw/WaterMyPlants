package paeqw.app.fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import paeqw.app.R;
import paeqw.app.models.Space;
import paeqw.app.collections.SpaceManager;
import paeqw.app.interfaces.PlantIdentificationService;
import paeqw.app.helpers.DatabaseHelper;
import paeqw.app.helpers.RetrofitClient;
import paeqw.app.models.Plant;
import paeqw.app.models.PlantIdentificationResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ScanPlantFragment extends Fragment {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final String API_KEY = "PC8mewzTR9DzXa2Lu6OLKDjAvgIP4yShsnUwoXUowrGZuyJE8P";
    private Uri photoUri;
    private ImageView imageView;
    private ProgressBar progressBar;
    private String responseString;
    private TextView resultTextView;
    private Button addPlant;
    private SpaceManager spaceManager;
    private DatabaseHelper databaseHelper;

    public ScanPlantFragment() {
        // Required empty public constructor
    }

    public static ScanPlantFragment newInstance() {
        ScanPlantFragment fragment = new ScanPlantFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseHelper = new DatabaseHelper();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan_plant, container, false);
        imageView = view.findViewById(R.id.imageView);
        progressBar = view.findViewById(R.id.progressBar);
        resultTextView = view.findViewById(R.id.resultTextView);
        Button takePhotoButton = view.findViewById(R.id.takePhotoButton);
        Button identifyPlantButton = view.findViewById(R.id.identifyPlantButton);
        addPlant = view.findViewById(R.id.addPlantButton);

        spaceManager = new SpaceManager(getContext());
        spaceManager.loadFromDatabase();

        addPlant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (responseString != null) {
                    Gson gson = new Gson();
                    PlantIdentificationResponse response = gson.fromJson(responseString, PlantIdentificationResponse.class);
                    PlantIdentificationResponse.Suggestion suggestion = response.result.classification.suggestions.get(0);

                    // Create a new Plant object
                    String plantName = suggestion.name;
                    double probability = suggestion.probability * 100;
                    String plantInfo = String.format("%s - probability %.1f%%", plantName, probability);

                    // Show the dialog to add the plant to a space
                    showAddPlantDialog(plantName, photoUri);
                } else {
                    Toast.makeText(getContext(), "No plant identification result available", Toast.LENGTH_SHORT).show();
                }
            }
        });

        takePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions();
                } else {
                    dispatchTakePictureIntent();
                }
            }
        });

        identifyPlantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (photoUri != null) {
                    uploadImageForIdentification();
                } else {
                    Toast.makeText(getActivity(), "Please take a photo first", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestMultiplePermissionsLauncher.launch(new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_MEDIA_IMAGES
            });
        } else {
            requestMultiplePermissionsLauncher.launch(new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            });
        }
    }

    private final ActivityResultLauncher<String[]> requestMultiplePermissionsLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            result -> {
                Boolean cameraGranted = result.getOrDefault(Manifest.permission.CAMERA, false);
                Boolean storageGranted = result.getOrDefault(Manifest.permission.READ_MEDIA_IMAGES, false);

                if (cameraGranted != null && cameraGranted && storageGranted != null && storageGranted) {
                    dispatchTakePictureIntent();
                } else {
                    Toast.makeText(getActivity(), "Camera and storage permissions are required to take photos", Toast.LENGTH_SHORT).show();
                }
            }
    );

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create a content resolver Uri for saving the image
            ContentResolver resolver = getActivity().getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "IMG_" + System.currentTimeMillis() + ".jpg");
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
            photoUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

            if (photoUri != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            } else {
                Toast.makeText(getActivity(), "Failed to create image Uri", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK) {
            // The image is saved at the path specified by `photoUri`
            // You can display it in an ImageView or process it further
            imageView.setImageURI(photoUri);
        }
    }

    private void uploadImageForIdentification() {
        progressBar.setVisibility(View.VISIBLE);
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), photoUri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();

            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imageBytes);
            MultipartBody.Part body = MultipartBody.Part.createFormData("images", "image.jpg", requestFile);

            RetrofitClient retrofitClient = new RetrofitClient();
            Retrofit retrofit = retrofitClient.getClient("https://plant.id");

            PlantIdentificationService service = retrofit.create(PlantIdentificationService.class);
            Call<ResponseBody> call = service.identifyPlant(API_KEY, body);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    progressBar.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        try {
                            responseString = response.body().string();
                            parseAndDisplayResult(responseString);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Identification failed", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "API call failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getActivity(), "Failed to process image", Toast.LENGTH_SHORT).show();
        }
    }

    private void parseAndDisplayResult(String responseString) {
        Gson gson = new Gson();
        PlantIdentificationResponse response = gson.fromJson(responseString, PlantIdentificationResponse.class);
        StringBuilder resultText = new StringBuilder();

        PlantIdentificationResponse.Suggestion suggestion = response.result.classification.suggestions.get(0);
        String name = suggestion.name;
        double probability = suggestion.probability * 100;
        resultText.append(String.format("%s - probability %.1f%%\n", name, probability));

        resultTextView.setText(resultText.toString());
        addPlant.setClickable(true);
    }

    private void showAddPlantDialog(String plantName, Uri photoUri) {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.add_to_space_dialog_with_int);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }

        Spinner spinner = dialog.findViewById(R.id.spinner);
        EditText plantNameEditText = dialog.findViewById(R.id.plantNameEditText);
        EditText intervalEditText = dialog.findViewById(R.id.intervalEditText);
        Button button = dialog.findViewById(R.id.buttonSubmit);

        plantNameEditText.setText(plantName);

        // Get the list of spaces
        List<Space> spaceList = spaceManager.getSpaceList();

        // Set up the spinner with spaceList
        ArrayAdapter<Space> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, spaceList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        button.setOnClickListener(view -> {
            Space selectedSpace = (Space) spinner.getSelectedItem();
            String interval = intervalEditText.getText().toString();
            String updatedPlantName = plantNameEditText.getText().toString();

            if (selectedSpace != null && !interval.isEmpty() && !updatedPlantName.isEmpty()) {
                int inter = Integer.parseInt(interval);
                // Upload the plant image to Firebase
                databaseHelper.uploadPlantImage(photoUri, "userId", selectedSpace.getSpaceName(), updatedPlantName)
                        .thenAccept(downloadUri -> {
                            Plant plant = new Plant(updatedPlantName, null, downloadUri.toString(), inter);
                            selectedSpace.addPlant(plant);
                            spaceManager.saveToSharedPreferences();
                            dialog.dismiss();
                        })
                        .exceptionally(throwable -> {
                            Toast.makeText(getContext(), "Failed to upload image: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            return null;
                        });
            } else {
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }
}
