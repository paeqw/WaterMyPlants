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
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import paeqw.app.models.Plant;
import paeqw.app.models.Space;

public class DatabaseHelper {
    private FirebaseDatabase database;
    private FirebaseStorage storage;

    public DatabaseHelper() {
        this.database = FirebaseDatabase.getInstance();
        this.storage = FirebaseStorage.getInstance();
    }
    public void addPlantToDatabase(String userId, String spaceName, Plant plant) {
        DatabaseReference plantRef = database.getReference("users").child(userId).child("spaces").child(spaceName).child("plants").child(plant.getName());
        plantRef.setValue(plant);
    }

    // Uploads a plant image and returns the URI
    public CompletableFuture<Uri> uploadPlantImage(Uri fileUri, String userId, String spaceName, String plantName) {
        StorageReference fileRef = storage.getReference().child("images").child(userId).child(spaceName).child(plantName);
        CompletableFuture<Uri> future = new CompletableFuture<>();

        fileRef.putFile(fileUri).continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw task.getException();
            }
            return fileRef.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                future.complete(downloadUri);
            } else {
                Log.e("Storage", "Upload failed", task.getException());
                future.completeExceptionally(task.getException());
            }
        });

        return future;
    }
    public CompletableFuture<Void> removeSpaceFromDatabase(String spaceName, String userId) {
        DatabaseReference spacesRef = database.getReference("users").child(userId).child("spaces");

        CompletableFuture<Void> future = new CompletableFuture<>();

        DatabaseReference spaceRef = spacesRef.child(spaceName);

        spaceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    spaceRef.removeValue().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("Database", "Space removed successfully");
                            future.complete(null);
                        } else {
                            Log.e("Database", "Failed to remove space", task.getException());
                            future.completeExceptionally(task.getException());
                        }
                    });
                } else {
                    Log.e("Database", "Space does not exist");
                    future.completeExceptionally(new Exception("Space does not exist"));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Database", "Error removing space", databaseError.toException());
                future.completeExceptionally(databaseError.toException());
            }
        });

        return future;
    }


    public CompletableFuture<List<Space>> fetchSpaces(String userId) {
        DatabaseReference spacesRef = database.getReference("users").child(userId).child("spaces");
        CompletableFuture<List<Space>> future = new CompletableFuture<>();

        spacesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Map each child to a Space object
                    List<Space> spaces = new ArrayList<>();
                    for (DataSnapshot spaceSnapshot : dataSnapshot.getChildren()) {
                        Space space = spaceSnapshot.getValue(Space.class);
                        if (space != null) {
                            spaces.add(space);
                        }
                    }
                    future.complete(spaces);
                } else {
                    future.completeExceptionally(new Exception("No spaces found"));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Database", "Error loading spaces", databaseError.toException());
                future.completeExceptionally(databaseError.toException());
            }
        });

        return future;
    }



    public CompletableFuture<Void> addSpaceToDatabase(Space space, String userId) {
        DatabaseReference spacesRef = database.getReference("users").child(userId).child("spaces");

        CompletableFuture<Void> future = new CompletableFuture<>();

        // First, check if the space already exists
        spacesRef.child(space.getSpaceName()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.e("Database", "Space with the same name already exists");
                    future.completeExceptionally(new Exception("Space with the same name already exists"));
                } else {
                    // Space does not exist, add new space
                    spacesRef.child(space.getSpaceName()).setValue(space).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("Database", "Space added successfully");
                            future.complete(null);
                        } else {
                            Log.e("Database", "Failed to add space", task.getException());
                            future.completeExceptionally(task.getException());
                        }
                    });

                    // Add plants to the database if any
                    space.getPlantList().forEach(plant -> addPlantToDatabase(userId, space.getSpaceName(), plant));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Database", "Error checking if space exists", databaseError.toException());
                future.completeExceptionally(databaseError.toException());
            }
        });

        return future;
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




}
