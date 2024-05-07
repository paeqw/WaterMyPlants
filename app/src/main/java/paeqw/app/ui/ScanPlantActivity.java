package paeqw.app.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import paeqw.app.R;

public class ScanPlantActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_plant);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_item3);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_item1) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            } else if (itemId == R.id.nav_item2) {
                startActivity(new Intent(getApplicationContext(), SearchPlantActivity.class));
                finish();
            } else if (itemId == R.id.nav_item3) {
                startActivity(new Intent(getApplicationContext(), ScanPlantActivity.class));
                finish();
            } else if (itemId == R.id.nav_item4) {
                startActivity(new Intent(getApplicationContext(), BlogActivity.class));
                finish();
            } else if (itemId == R.id.nav_item5) {
                startActivity(new Intent(getApplicationContext(), MoreActivity.class));
                finish();
            }
            return true;
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("shouldFinish", true);
        startActivity(intent);
    }
}