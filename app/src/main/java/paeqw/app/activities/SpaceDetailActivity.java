package paeqw.app.activities;

import static android.view.ViewGroup.LayoutParams;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.time.format.DateTimeFormatter;
import java.util.List;

import paeqw.app.R;
import paeqw.app.collections.SpaceManager;
import paeqw.app.exceptions.CouldNotFindException;
import paeqw.app.helpers.SharedPreferencesHelper;
import paeqw.app.models.Plant;
import paeqw.app.models.Space;

public class SpaceDetailActivity extends AppCompatActivity {

    private SpaceManager spaceManager;
    private String spaceName;
    private Space space;
    private TextView textView;
    private SharedPreferencesHelper sharedPreferencesHelper;
    private GridLayout gridLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_space_detail);

        initViews();

        String json = getIntent().getStringExtra("spaceManager");
        Gson gson = new Gson();
        Type type = new TypeToken<List<Space>>(){}.getType();
        List<Space> spaceList = gson.fromJson(json, type);
        spaceManager = new SpaceManager(this, spaceList);

        spaceName = getIntent().getStringExtra("spaceName");

        try {
            List<Space> searchResult = spaceManager.searchSpace(spaceName);
            space = searchResult.get(0);

        } catch (CouldNotFindException e) {
            throw new RuntimeException(e);
        }

        showPlants();


        textView.setText("Details for: " + space.getSpaceName() + "\nPlants: " + space.getPlantList().size());
    }
    private void initViews(){
        textView = findViewById(R.id.detailTextView);
        gridLayout = findViewById(R.id.gridLayout);
        sharedPreferencesHelper = new SharedPreferencesHelper(this);
    }
    private void showPlants() {
        gridLayout.removeAllViews();
        for (Plant pl: space.getPlantList()) {
            View plantView = createPlantView(this, pl);
            gridLayout.addView(plantView);
        }
        gridLayout.addView(createButton(this));
    }
    private Button createButton(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;

        int desiredWidth = screenWidth / 2;
        int desiredHeight = (int) (desiredWidth * 16 / 9.0);

        Button button = new Button(context);
        button.setText(R.string.add_new_plant_button);
        button.setBackgroundColor(getResources().getColor(R.color.lighterGray,null));
        button.setBackgroundResource(R.drawable.rounded_square);
        GridLayout.LayoutParams frameLayoutParams = new GridLayout.LayoutParams();
        frameLayoutParams.width = desiredWidth;
        frameLayoutParams.height = desiredHeight;
        frameLayoutParams.setMargins(10, 10, 10, 10);
        button.setLayoutParams(frameLayoutParams);
        return button;
    }

    public View createPlantView(Context context, Plant plant) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;
        int desiredWidth = screenWidth / 2;
        int desiredHeight = (int) (desiredWidth * 16 / 9.0);

        FrameLayout frameLayout1 = new FrameLayout(context);
        frameLayout1.setBackgroundResource(R.drawable.rounded_square);


        FrameLayout frameLayout = new FrameLayout(context);

        frameLayout.setBackgroundResource(R.drawable.rounded_square);
        GridLayout.LayoutParams frameLayoutParams = new GridLayout.LayoutParams();
        frameLayoutParams.width = desiredWidth;
        frameLayoutParams.height = desiredHeight;
        frameLayoutParams.setMargins(10, 10, 10, 10);
        frameLayout.setLayoutParams(frameLayoutParams);

        ImageView imageView = new ImageView(context);
        FrameLayout.LayoutParams imageParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        imageView.setLayoutParams(imageParams);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Picasso.get().load(plant.getImageUrl()).into(imageView);
        frameLayout.addView(imageView);

        TextView nameView = new TextView(context);
        nameView.setText(plant.getName());
        nameView.setTextColor(Color.WHITE);
        nameView.setBackgroundColor(Color.parseColor("#66000000"));
        FrameLayout.LayoutParams nameParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.TOP
        );
        nameParams.topMargin = 20;
        nameView.setLayoutParams(nameParams);
        frameLayout.addView(nameView);

        TextView dateView = new TextView(context);
        String lastWateredText = "Not watered yet";
        if (plant.getWhenLastWatered() != null) {
            lastWateredText = "Last watered: " + DateTimeFormatter.ofPattern("dd MMM yyyy").format(plant.getWhenLastWatered());
        }
        dateView.setText(lastWateredText);
        dateView.setTextColor(Color.WHITE);
        dateView.setBackgroundColor(Color.parseColor("#66000000"));
        FrameLayout.LayoutParams dateParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM
        );
        dateParams.bottomMargin = 20;
        dateView.setLayoutParams(dateParams);
        frameLayout.addView(dateView);


        frameLayout1.addView(frameLayout);
        return frameLayout1;
    }




}