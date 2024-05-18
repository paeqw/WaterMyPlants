package paeqw.app.helpers;

import android.net.Uri;
import android.util.Log;
import androidx.annotation.NonNull;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import paeqw.app.models.Plant;
import paeqw.app.models.Space;

public class DatabaseHelper {
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private FirebaseAuth auth;

    public DatabaseHelper() {
        this.database = FirebaseDatabase.getInstance();
        this.storage = FirebaseStorage.getInstance();
        this.auth = FirebaseAuth.getInstance();
    }

    private CompletableFuture<String> getToken() {
        CompletableFuture<String> future = new CompletableFuture<>();
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            user.getIdToken(true)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String idToken = task.getResult().getToken();
                            future.complete(idToken);
                        } else {
                            future.completeExceptionally(task.getException());
                        }
                    });
        } else {
            future.completeExceptionally(new Exception("User not authenticated"));
        }

        return future;
    }

    private CompletableFuture<Void> verifyToken() {
        CompletableFuture<Void> future = new CompletableFuture<>();
        getToken().thenAccept(idToken -> {
            // Optionally, send the token to your backend server for verification.
            future.complete(null);
        }).exceptionally(throwable -> {
            future.completeExceptionally(throwable);
            return null;
        });

        return future;
    }

    public CompletableFuture<Void> addPlantToDatabase(String userId, String spaceName, Plant plant) {
        return verifyToken().thenCompose(aVoid -> {
            DatabaseReference plantRef = database.getReference("users").child(userId).child("spaces").child(spaceName).child("plants").child(plant.getName());
            CompletableFuture<Void> future = new CompletableFuture<>();
            plantRef.setValue(plant).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    future.complete(null);
                } else {
                    future.completeExceptionally(task.getException());
                }
            });
            return future;
        });
    }

    public CompletableFuture<Uri> uploadPlantImage(Uri fileUri, String userId, String spaceName, String plantName) {
        return verifyToken().thenCompose(aVoid -> {
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
        });
    }

    public CompletableFuture<Void> removeSpaceFromDatabase(String spaceName, String userId) {
        return verifyToken().thenCompose(aVoid -> {
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
        });
    }

    public CompletableFuture<List<Space>> fetchSpaces(String userId) {
        return verifyToken().thenCompose(aVoid -> {
            DatabaseReference spacesRef = database.getReference("users").child(userId).child("spaces");
            CompletableFuture<List<Space>> future = new CompletableFuture<>();

            spacesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
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
        });
    }

    public CompletableFuture<Void> updateSpaceInDatabase(Space space, String userId) {
        return verifyToken().thenCompose(aVoid -> {
            DatabaseReference spacesRef = database.getReference("users").child(userId).child("spaces");
            CompletableFuture<Void> future = new CompletableFuture<>();
            DatabaseReference spaceRef = spacesRef.child(space.getSpaceName());

            spaceRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        spaceRef.setValue(space).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d("Database", "Space updated successfully");
                                future.complete(null);
                            } else {
                                Log.e("Database", "Failed to update space", task.getException());
                                future.completeExceptionally(task.getException());
                            }
                        });
                    } else {
                        Log.e("Database", "Space does not exist and cannot be updated");
                        future.completeExceptionally(new Exception("Space does not exist and cannot be updated"));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("Database", "Error checking if space exists", databaseError.toException());
                    future.completeExceptionally(databaseError.toException());
                }
            });

            return future;
        });
    }

    public CompletableFuture<Void> addSpaceToDatabase(Space space, String userId) {
        return verifyToken().thenCompose(aVoid -> {
            DatabaseReference spacesRef = database.getReference("users").child(userId).child("spaces");
            DatabaseReference spaceRef = spacesRef.child(space.getSpaceName());
            CompletableFuture<Void> future = new CompletableFuture<>();

            spaceRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Space existingSpace = dataSnapshot.getValue(Space.class);
                        if (!existingSpace.getPlantList().equals(space.getPlantList())) {
                            Log.d("Database", "Updating space due to plantList changes.");
                            updateSpaceInDatabase(space, userId).thenAccept(aVoid -> {
                                Log.d("Database", "Space updated successfully with new plants.");
                                future.complete(null);
                            }).exceptionally(throwable -> {
                                Log.e("Database", "Failed to update space with new plants.", throwable);
                                future.completeExceptionally(throwable);
                                return null;
                            });
                        } else {
                            Log.d("Database", "No changes in plantList, no update needed.");
                            future.complete(null);
                        }
                    } else {
                        spaceRef.setValue(space).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d("Database", "Space added successfully");
                                future.complete(null);
                            } else {
                                Log.e("Database", "Failed to add space", task.getException());
                                future.completeExceptionally(task.getException());
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("Database", "Error checking if space exists", databaseError.toException());
                    future.completeExceptionally(databaseError.toException());
                }
            });

            return future;
        });
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
                    Log.e("WaterYourPlants", "Database operation was cancelled.");
                }
            });
        }
    }
}
