package paeqw.app.ui;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DatabaseHelper {
    FirebaseDatabase database;

    public DatabaseHelper() {
        this.database = FirebaseDatabase.getInstance();
    }

    public void writeUserIdToDatabase(String userID) {
        DatabaseReference myRef = database.getReference("/");

        myRef.child("users").setValue(userID)
            .addOnFailureListener(e -> {
                Log.e("WaterYourPlants", e.getMessage() == null ? "what" : e.getMessage());
            });
    }
}
