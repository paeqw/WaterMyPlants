package paeqw.app.activities;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import paeqw.app.R;
import paeqw.app.fragments.BlogFragment;
import paeqw.app.fragments.MoreFragment;
import paeqw.app.fragments.PlantsListFragment;
import paeqw.app.fragments.ScanPlantFragment;
import paeqw.app.fragments.SearchPlantFragment;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_item1) {
                replaceFragment(new PlantsListFragment());
            } else if (itemId == R.id.nav_item2) {
                replaceFragment(new SearchPlantFragment());
            } else if (itemId == R.id.nav_item3) {
                replaceFragment(new ScanPlantFragment());
            } else if (itemId == R.id.nav_item4) {
                replaceFragment(new BlogFragment());
            } else if (itemId == R.id.nav_item5) {
                replaceFragment(new MoreFragment());
            }
            return true;
        });

        if (savedInstanceState == null) {
            replaceFragment(new PlantsListFragment());
        }

        // Handle the intent extra to show a specific fragment
        if (getIntent().hasExtra("showFragment")) {
            String fragmentToShow = getIntent().getStringExtra("showFragment");
            if ("SearchPlantFragment".equals(fragmentToShow)) {
                replaceFragment(new SearchPlantFragment());
                bottomNavigationView.setSelectedItemId(R.id.nav_item2);
            }
            if ("ScanPlantFragment".equalsIgnoreCase(fragmentToShow)) {
                replaceFragment(new ScanPlantFragment());
                bottomNavigationView.setSelectedItemId(R.id.nav_item3);
            }
        }
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
