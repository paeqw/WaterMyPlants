package paeqw.app.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import paeqw.app.models.Space;

public class SharedPreferencesHelper {

    private static final String PREF_NAME = "WaterYourPlantsPrefs";
    private SharedPreferences sharedPreferences;
    private Gson gson;

    public SharedPreferencesHelper(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    public void saveSpaces(List<Space> spaces) {
        String json = gson.toJson(spaces);
        Log.d("saveSpaces", "JSON: " + json);
        sharedPreferences.edit().putString("spaces", json).apply();
    }
    public void clearSpaces() {
        String json = gson.toJson(new ArrayList<>());
        sharedPreferences.edit().putString("spaces", json).apply();
    }

    public List<Space> getSpaces() {
        String json = sharedPreferences.getString("spaces", null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<List<Space>>() {}.getType();
        return gson.fromJson(json, type);
    }
}
