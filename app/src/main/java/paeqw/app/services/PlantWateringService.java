package paeqw.app.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.List;

import paeqw.app.collections.SpaceManager;
import paeqw.app.models.Plant;
import paeqw.app.models.Space;
import paeqw.app.utils.NotificationUtils;

public class PlantWateringService extends Service {
    private static final String TAG = "PlantWateringService";
    private SpaceManager spaceManager;

    @Override
    public void onCreate() {
        super.onCreate();
        spaceManager = new SpaceManager(this);
        NotificationUtils.createNotificationChannel(this);
        checkPlantsWatering();
    }

    private void checkPlantsWatering() {
        spaceManager.loadFromDatabase().thenRun(() -> {
            for (Space spa : spaceManager.getSpaceList()) {
                List<Plant> plants =spa.getPlantList();
                for (Plant plant : plants) {
                    if (plant.needsWater()) {
                        String title = "Time to water " + plant.getName();
                        String message = "Your plant needs water. Don't forget to water it!";
                        NotificationUtils.showNotification(this, title, message);
                    }
                }
            }


        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        checkPlantsWatering();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
