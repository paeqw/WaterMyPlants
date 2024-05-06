package paeqw.app.helpers;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DatabaseHelper {
    FirebaseDatabase database;

    public DatabaseHelper() {
        this.database = FirebaseDatabase.getInstance();
    }

    public void addUserToDatabase() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users");

            databaseRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        databaseRef.setValue(userId)
                                .addOnSuccessListener(aVoid -> {

                                })
                                .addOnFailureListener(e -> {
                                    Log.e("WaterYourPlants", e.getMessage() == null ? "what" : e.getMessage());
                                });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

}
