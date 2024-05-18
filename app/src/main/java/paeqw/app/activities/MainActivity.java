package paeqw.app.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import paeqw.app.R;
import paeqw.app.fragments.BlogFragment;
import paeqw.app.fragments.MoreFragment;
import paeqw.app.fragments.PlantsListFragment;
import paeqw.app.fragments.ScanPlantFragment;
import paeqw.app.fragments.SearchPlantFragment;
import paeqw.app.models.SharedViewModel;

import androidx.lifecycle.ViewModelProvider;

public class MainActivity extends AppCompatActivity {
    private SharedViewModel sharedViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

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
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}