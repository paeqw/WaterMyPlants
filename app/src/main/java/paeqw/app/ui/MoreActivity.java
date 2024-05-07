package paeqw.app.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import paeqw.app.R;

public class MoreActivity extends AppCompatActivity {

    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);

        button = findViewById(R.id.logout);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signOut();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_item5);
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