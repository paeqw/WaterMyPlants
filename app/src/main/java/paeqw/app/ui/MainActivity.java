package paeqw.app.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import paeqw.app.R;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_item1);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            ViewGroup parent = findViewById(R.id.includedLayout);
            if (parent != null) {
                parent.removeAllViews();
                LayoutInflater inflater = LayoutInflater.from(this);
                View newLayout = null;
                if (itemId == R.id.nav_item1) {
                    newLayout = inflater.inflate(R.layout.activity_plant_list, parent, false);
                } else if (itemId == R.id.nav_item2) {
                    newLayout = inflater.inflate(R.layout.activity_search_plant, parent, false);
                } else if (itemId == R.id.nav_item3) {
                    newLayout = inflater.inflate(R.layout.activity_scan_plant, parent, false);
                } else if (itemId == R.id.nav_item4) {
                    newLayout = inflater.inflate(R.layout.activity_blog, parent, false);
                } else if (itemId == R.id.nav_item5) {
                    newLayout = inflater.inflate(R.layout.activity_more, parent, false);
                }
                if (newLayout != null) {
                    parent.addView(newLayout); // Add the new layout to the parent layout
                }
            }
            return true;
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, StartActivity.class);
        intent.putExtra("shouldFinish", true);
        startActivity(intent);
    }
}