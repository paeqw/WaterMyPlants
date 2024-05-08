package paeqw.app.helpers;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.List;

import paeqw.app.interfaces.DatabaseCallback;
import paeqw.app.models.Plant;
import paeqw.app.models.Space;

public class DatabaseHelper {
    private FirebaseDatabase database;
    private FirebaseStorage storage;

    public DatabaseHelper() {
        this.database = FirebaseDatabase.getInstance();
        this.storage = FirebaseStorage.getInstance();
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
                        databaseRef.child(userId).setValue("")
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("WaterYourPlants", "User added successfully.");
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("WaterYourPlants", e.getMessage() == null ? "Unknown error occurred" : e.getMessage());
                                });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle possible cancellations
                    Log.e("WaterYourPlants", "Database operation was cancelled.");
                }
            });
        }
    }


    public void addSpaceToDatabase(String userId, Space space) {
        DatabaseReference spaceRef = database.getReference("users").child(userId).child("spaces").child(space.getSpaceName());
        spaceRef.setValue(space).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("Database", "Space added successfully");
            } else {
                Log.e("Database", "Failed to add space", task.getException());
            }
        });

        for (Plant plant : space.getPlantList()) {
            addPlantToDatabase(userId, space.getSpaceName(), plant);
        }
    }

    public void addPlantToDatabase(String userId, String spaceName, Plant plant) {
        DatabaseReference plantRef = database.getReference("users").child(userId).child("spaces").child(spaceName).child("plants").child(plant.getName());
        plantRef.setValue(plant);
    }

    public void uploadPlantImage(Uri fileUri, String userId, String spaceName, String plantName, DatabaseCallback<Uri> callback) {
        StorageReference fileRef = storage.getReference().child("images").child(userId).child(spaceName).child(plantName);
        UploadTask uploadTask = fileRef.putFile(fileUri);
        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw task.getException();
            }
            return fileRef.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                callback.onCallback(downloadUri);
            } else {
                Log.e("Storage", "Upload failed", task.getException());
            }
        });
    }

    public void fetchSpaces(String userId, DatabaseCallback<List<Space>> callback) {
        DatabaseReference spacesRef = database.getReference("users").child(userId).child("spaces");
        spacesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Space> spaces = dataSnapshot.getValue(List.class); // Cast to the correct type
                callback.onCallback(spaces);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Database", "Error loading spaces", databaseError.toException());
            }
        });
    }

}
