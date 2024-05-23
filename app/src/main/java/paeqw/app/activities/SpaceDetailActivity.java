package paeqw.app.activities;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import paeqw.app.R;
import paeqw.app.collections.SpaceManager;
import paeqw.app.exceptions.CouldNotFindException;
import paeqw.app.helpers.SharedPreferencesHelper;
import paeqw.app.models.Plant;
import paeqw.app.models.Space;
import paeqw.app.services.PlantWateringService;

public class SpaceDetailActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "plant_watering_channel";
    private SpaceManager spaceManager;
    private LocalDateTime selectedDate;

    private String spaceName;
    private Space space;
    private TextView textView;
    private SharedPreferencesHelper sharedPreferencesHelper;
    private GridLayout gridLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_space_detail);

        initViews();
        scheduleWateringService();

/*
        createNotificationChannel();
*/

        String json = getIntent().getStringExtra("spaceManager");
        Gson gson = new Gson();
        Type type = new TypeToken<List<Space>>(){}.getType();
        List<Space> spaceList = gson.fromJson(json, type);
        spaceManager = new SpaceManager(this, spaceList);

        spaceName = getIntent().getStringExtra("spaceName");

        try {
            List<Space> searchResult = spaceManager.searchSpace(spaceName);
            space = searchResult.get(0);

        } catch (CouldNotFindException e) {
            throw new RuntimeException(e);
        }

        showPlants();

        textView.setText("Details for: " + space.getSpaceName() + "\nPlants: " + space.getPlantList().size());
    }

    private void initViews(){
        textView = findViewById(R.id.detailTextView);
        gridLayout = findViewById(R.id.gridLayout);
        sharedPreferencesHelper = new SharedPreferencesHelper(this);
    }

    private void showPlants() {
        gridLayout.removeAllViews();
        for (Plant pl: space.getPlantList()) {
            View plantView = createPlantView(this, pl);
            gridLayout.addView(plantView);
        }
        gridLayout.addView(createButton(this));
    }

    private Button createButton(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;

        int desiredWidth = screenWidth / 2;
        int desiredHeight = (int) (desiredWidth * 16 / 9.0);

        MaterialButton button = new MaterialButton(context);

        Drawable icon = ContextCompat.getDrawable(context, R.drawable.add_24px);
        button.setIcon(icon);
        button.setIconGravity(MaterialButton.ICON_GRAVITY_TEXT_START);

        button.setIconTint(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white)));

        button.setBackgroundColor(ContextCompat.getColor(context, R.color.lighterGray));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddPlantDialog();
            }
        });

        GridLayout.LayoutParams frameLayoutParams = new GridLayout.LayoutParams();
        frameLayoutParams.width = desiredWidth;
        frameLayoutParams.height = desiredHeight;
        frameLayoutParams.setMargins(10, 10, 10, 10);
        button.setLayoutParams(frameLayoutParams);

        return button;
    }

    public View createPlantView(Context context, Plant plant) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;
        int desiredWidth = screenWidth / 2;
        int desiredHeight = (int) (desiredWidth * 16 / 9.0);

        FrameLayout frameLayout1 = new FrameLayout(context);
        frameLayout1.setBackgroundResource(R.drawable.rounded_square);

        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setBackgroundResource(R.drawable.rounded_square);
        GridLayout.LayoutParams frameLayoutParams = new GridLayout.LayoutParams();
        frameLayoutParams.width = desiredWidth;
        frameLayoutParams.height = desiredHeight;
        frameLayoutParams.setMargins(10, 10, 10, 10);
        frameLayout.setLayoutParams(frameLayoutParams);

        ImageView imageView = new ImageView(context);
        FrameLayout.LayoutParams imageParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        imageView.setLayoutParams(imageParams);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Picasso.get().load(plant.getImageUrl()).into(imageView);
        frameLayout.addView(imageView);

        TextView nameView = new TextView(context);
        nameView.setText(plant.getName());
        nameView.setTextColor(Color.WHITE);
        nameView.setBackgroundColor(Color.parseColor("#66000000"));
        FrameLayout.LayoutParams nameParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.TOP
        );
        nameParams.topMargin = 20;
        nameView.setLayoutParams(nameParams);
        frameLayout.addView(nameView);

        TextView dateView = new TextView(context);
        String lastWateredText = "Not watered yet";
        if (plant.getWhenLastWatered() != null) {
            lastWateredText = "Last watered: " + DateTimeFormatter.ofPattern("dd MMM yyyy").format(LocalDateTime.ofInstant(Instant.ofEpochMilli(plant.getWhenLastWatered()), ZoneId.systemDefault()));
        }
        dateView.setText(lastWateredText);
        dateView.setTextColor(Color.WHITE);
        dateView.setBackgroundColor(Color.parseColor("#66000000"));
        FrameLayout.LayoutParams dateParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM
        );
        dateParams.bottomMargin = 20;
        dateView.setLayoutParams(dateParams);
        frameLayout.addView(dateView);

        TextView intervalView = new TextView(context);
        String intervalText = "Watering interval: " + plant.getWateringInterval() + " days";
        intervalView.setText(intervalText);
        intervalView.setTextColor(Color.WHITE);
        intervalView.setBackgroundColor(Color.parseColor("#66000000"));
        FrameLayout.LayoutParams intervalParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM
        );
        intervalParams.bottomMargin = 140;
        intervalView.setLayoutParams(intervalParams);
        frameLayout.addView(intervalView);

        TextView needsWaterView = new TextView(context);
        String needsWaterText = needsWatering(plant) ? "Needs water!" : "";
        needsWaterView.setText(needsWaterText);
        needsWaterView.setTextColor(Color.RED);
        needsWaterView.setBackgroundColor(Color.parseColor("#66000000"));
        FrameLayout.LayoutParams needsWaterParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM
        );
        needsWaterParams.bottomMargin = 80;
        needsWaterView.setLayoutParams(needsWaterParams);
        frameLayout.addView(needsWaterView);

        frameLayout1.addView(frameLayout);
        frameLayout1.setOnClickListener(view -> {
            showPlantDialog(plant);
        });
        return frameLayout1;
    }

    private void showPlantModifyDialog(Plant plant){
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.change_when_last_watered_dialog);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }

        TextInputEditText editTextDate = dialog.findViewById(R.id.editTextDate);
        Button buttonModify = dialog.findViewById(R.id.buttonSubmit);

        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
            }
        });
        datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long selection) {
                editTextDate.setText(datePicker.getHeaderText());

                selectedDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(selection),
                        TimeZone.getDefault().toZoneId());
            }
        });

        buttonModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedDate != null) {
                    plant.setWhenLastWatered(selectedDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
                    spaceManager.saveToSharedPreferences();
                    dialog.dismiss();
                    showPlants();
                } else Toast.makeText(SpaceDetailActivity.this, "Please select date!", Toast.LENGTH_LONG).show();
            }
        });

        dialog.show();
    }

    private void showPlantDialog(Plant plant) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.plant_costam_dialog);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }

        Button buttonModify = dialog.findViewById(R.id.buttonModify);
        Button buttonDelete = dialog.findViewById(R.id.buttonDelete);
        TextView plantDialogTextView = dialog.findViewById(R.id.text);
        plantDialogTextView.setText("Modifying plant:'" + plant.getName() + "'");

        buttonModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPlantModifyDialog(plant);
                dialog.dismiss();
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    space.removePlant(plant);
                } catch (CouldNotFindException e) {
                    throw new RuntimeException(e);
                }
                List<Space> spaceList = spaceManager.getSpaceList();
                for (int i = 0; i < spaceList.size(); i++) {
                    if (spaceList.get(i).getSpaceName().equalsIgnoreCase(space.getSpaceName())) {
                        spaceList.remove(i);
                        spaceList.add(space);
                    }
                }
                spaceManager.setSpaceList(spaceList);
                spaceManager.saveToSharedPreferences();
                showPlants();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void scheduleWateringService() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, PlantWateringService.class);

        int pendingIntentFlag = PendingIntent.FLAG_UPDATE_CURRENT;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntentFlag |= PendingIntent.FLAG_IMMUTABLE;
        }

        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, pendingIntentFlag);

        long interval = 5 * 60 * 1000;

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + interval, interval, pendingIntent);
    }


    private void showAddPlantDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_plant_to_space_dialog);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }

        Button buttonAddByName = dialog.findViewById(R.id.buttonAddByName);
        Button buttonAddByScaning = dialog.findViewById(R.id.buttonAddByScaning);

        buttonAddByName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(SpaceDetailActivity.this, MainActivity.class);
                intent.putExtra("showFragment", "SearchPlantFragment");
                startActivity(intent);
            }
        });

        buttonAddByScaning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(SpaceDetailActivity.this, MainActivity.class);
                intent.putExtra("showFragment", "ScanPlantFragment");
                startActivity(intent);
            }
        });

        // Show the dialog
        dialog.show();
    }

    private boolean needsWatering(Plant plant) {
        if (plant.getWhenLastWatered() == null) {
            return true;
        }
        LocalDateTime lastWateredDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(plant.getWhenLastWatered()), ZoneId.systemDefault());
        LocalDateTime nextWateringDate = lastWateredDate.plusDays(plant.getWateringInterval());
        return LocalDateTime.now().isAfter(nextWateringDate);
    }

    private void sendNotification(Plant plant) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Watering Reminder")
                .setContentText("Your plant " + plant.getName() + " needs water!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

//    private void createNotificationChannel() {
//        CharSequence name = "Plant Watering Channel";
//        String description = "Channel for plant watering reminders";
//        int importance = NotificationManager.IMPORTANCE_HIGH;
//        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
//        channel.setDescription(description);
//        NotificationManager notificationManager = getSystemService(NotificationManager.class);
//        notificationManager.createNotificationChannel(channel);
//    }
}
