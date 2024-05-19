package paeqw.app.collections;

import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import paeqw.app.exceptions.CouldNotFindException;
import paeqw.app.helpers.DatabaseHelper;
import paeqw.app.helpers.SharedPreferencesHelper;
import paeqw.app.models.Space;

import java.io.Serializable;

public class SpaceManager implements Serializable {
    List<Space> spaceList;
    Context context;
    DatabaseHelper databaseHelper;

    public SpaceManager(Context context, List<Space> spaceList) {
        this.spaceList = spaceList;
        this.context = context;
        databaseHelper = new DatabaseHelper();
    }

    public SpaceManager(Context context) {
        spaceList = new ArrayList<>();
        this.context = context;
        databaseHelper = new DatabaseHelper();
    }

    public void saveToSharedPreferences() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            for (Space el : spaceList) {
                databaseHelper.addSpaceToDatabase(el, userId);
            }
        }
        SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(context);
        sharedPreferencesHelper.saveSpaces(spaceList);
    }

    public CompletableFuture<Void> loadFromSharedPreferences() {
        return CompletableFuture.runAsync(() -> {
            SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(context);
            List<Space> fromSharedPreferences = sharedPreferencesHelper.getSpaces();
            for (Space el : fromSharedPreferences) {
                addSpace(el);
            }
        });
    }

    public CompletableFuture<Void> loadFromDatabase() {
        return CompletableFuture.supplyAsync(() -> {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user == null) {
                        throw new IllegalStateException("User not logged in");
                    }
                    return user.getUid();
                }).thenCompose(userId -> databaseHelper.fetchSpaces(userId))
                .thenAccept(spaces -> {
                    for (Space el : spaces) {
                        addSpace(el);
                    }
                    saveToSharedPreferences();
                })
                .exceptionally(ex -> {
                    Log.e("Database", "Error fetching spaces", ex);
                    return null;
                });
    }

    public void addSpace(Space space) {
        for (Space el : spaceList) {
            if (el.getSpaceName().equalsIgnoreCase(space.getSpaceName())) return;
        }
        spaceList.add(space);
    }

    public void removeSpace(Space space) throws CouldNotFindException {
        spaceList.remove(space);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            databaseHelper.removeSpaceFromDatabase(space.getSpaceName(), userId);
        }
    }

    public List<Space> searchSpace(String name) throws CouldNotFindException {
        List<Space> searchResult = new ArrayList<>();
        for (Space el : spaceList) {
            if (el.getSpaceName().toLowerCase().startsWith(name.toLowerCase())) searchResult.add(el);
        }
        if (!searchResult.isEmpty()) return searchResult;
        throw new CouldNotFindException("Could not find space with given name");
    }

    public List<Space> getSpaceList() {
        return spaceList;
    }
}
