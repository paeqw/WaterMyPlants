package paeqw.app.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;

import java.util.List;

import paeqw.app.R;
import paeqw.app.collections.SpaceManager;
import paeqw.app.exceptions.CouldNotFindException;
import paeqw.app.models.Space;

public class SpaceDetailActivity extends AppCompatActivity {

    private SpaceManager spaceManager;
    private String spaceName;
    private Space space;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_space_detail);
        Gson gson = new Gson();
        spaceManager = gson.fromJson(getIntent().getStringExtra("spaceManager"),SpaceManager.class);
        spaceName = getIntent().getStringExtra("spaceName");

        try {
            List<Space> spaceList = spaceManager.searchSpace(spaceName);
            space = spaceList.get(0);
        } catch (CouldNotFindException e) {
            throw new RuntimeException(e);
        }


        TextView textView = findViewById(R.id.detailTextView);
        textView.setText("Details for: " + space.getSpaceName() + "\nPlants: " + space.getPlantList().size());
    }
}