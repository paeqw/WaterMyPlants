package paeqw.app.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.lifecycle.ViewModelProvider;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.util.List;
import java.util.concurrent.TimeUnit;

import paeqw.app.R;
import paeqw.app.activities.SpaceDetailActivity;
import paeqw.app.collections.SpaceManager;
import paeqw.app.exceptions.CouldNotFindException;
import paeqw.app.models.Space;

public class PlantsListFragment extends Fragment {

    private SpaceManager spaceManager;
    private LinearLayout linearLayout;
    private Button addSpaceButton;
    private EditText searchField;
    private ProgressBar progressBar;

    public PlantsListFragment() {}

    public static PlantsListFragment newInstance() {
        return new PlantsListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_plants_list, container, false);
        initViews(rootView);
        initListeners();

        loadData();

        return rootView;
    }

    private void loadData() {
        spaceManager = new SpaceManager(getActivity());
        progressBar.setVisibility(View.VISIBLE);
        linearLayout.setVisibility(View.GONE);

        spaceManager.loadFromDatabase().thenRun(() -> {
            spaceManager.loadFromSharedPreferences();
            getActivity().runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                linearLayout.setVisibility(View.VISIBLE);
                showViews();
            });
        });
    }

    private void initViews(View rootView) {
        linearLayout = rootView.findViewById(R.id.linear);
        addSpaceButton = rootView.findViewById(R.id.addSpaceButton);
        searchField = rootView.findViewById(R.id.searchField);
        progressBar = rootView.findViewById(R.id.progressBar);
    }

    private void initListeners() {
        addSpaceButton.setOnClickListener(v -> addSpaceButtonClicked());
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                showViews(searchField.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    private void addSpaceButtonClicked() {
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.add_space_dialog);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }

        EditText editText = dialog.findViewById(R.id.editTextCustom);
        Button button = dialog.findViewById(R.id.buttonSubmit);

        button.setOnClickListener(view -> {
            String inputText = editText.getText().toString();
            spaceManager.addSpace(new Space(inputText));
            saveViews();
            showViews();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void showViews() {
        linearLayout.removeAllViews();
        for (Space el : spaceManager.getSpaceList()) {
            linearLayout.addView(generateSpaceView(getActivity(), el));
        }
    }

    private void showViews(String name) {
        linearLayout.removeAllViews();
        try {
            for (Space el : spaceManager.searchSpace(name)) {
                linearLayout.addView(generateSpaceView(getActivity(), el));
            }
        } catch (CouldNotFindException e) {
            // Handle the exception
        }
    }

    private void saveViews() {
        spaceManager.saveToSharedPreferences();
    }

    private View generateSpaceView(Context context, Space space) {
        LayoutInflater inflater = LayoutInflater.from(context);
        FrameLayout frameLayout = (FrameLayout) inflater.inflate(R.layout.space_view_template, null);

        TextView nameTextView = frameLayout.findViewById(R.id.space_name);
        nameTextView.setText(space.getSpaceName());

        TextView plantCountTextView = frameLayout.findViewById(R.id.plant_count);
        plantCountTextView.setText("Plants: " + space.getPlantList().size());

        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.wtf("ASJKDHKAJSHDKJHASKJDHKAJHDKJAH","WORKING");
                Intent intent = new Intent(context, SpaceDetailActivity.class);
                Gson gson = new Gson();
                String list = gson.toJson(spaceManager.getSpaceList());
                intent.putExtra("spaceName", space.getSpaceName());
                intent.putExtra("spaceManager", list);
                startActivity(intent);
            }
        });

        Button moreButton = frameLayout.findViewById(R.id.more_button);

        moreButton.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            LayoutInflater inflaterForDialog = LayoutInflater.from(context);
            View dialogView = inflaterForDialog.inflate(R.layout.modify_space_dialog, null);

            TextView textView = dialogView.findViewById(R.id.text);
            textView.setText("Modifying space: '" + space.getSpaceName() + "'");
            Button buttonModify = dialogView.findViewById(R.id.buttonModify);
            Button buttonDelete = dialogView.findViewById(R.id.buttonDelete);

            builder.setView(dialogView);
            AlertDialog dialog = builder.create();

            buttonModify.setOnClickListener(v -> {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                LayoutInflater inflaterForDialog1 = LayoutInflater.from(context);
                View dialogView1 = inflaterForDialog1.inflate(R.layout.modify_space_name_dialog, null);

                TextInputEditText editText1 = dialogView1.findViewById(R.id.editTextCustom);
                editText1.setText(space.getSpaceName());

                builder1.setView(dialogView1);
                AlertDialog dialog1 = builder1.create();

                Window window = dialog1.getWindow();
                if (window != null) {
                    window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                }

                Button submitButton = dialogView1.findViewById(R.id.buttonSubmit);
                submitButton.setOnClickListener(va -> {
                    String newName = editText1.getText().toString();
                    if (!newName.isEmpty()) {
                        Space space1 = new Space(newName);
                        space1.setPlantList(space.getPlantList());
                        try {
                            spaceManager.removeSpace(space);
                        } catch (CouldNotFindException e) {
                        }
                        spaceManager.addSpace(space1);
                        nameTextView.setText(newName);
                        showViews();
                        saveViews();
                        dialog.dismiss();
                        dialog1.dismiss();
                    } else {
                        editText1.setError("Name cannot be empty");
                    }
                });
                dialog1.show();
            });
            buttonDelete.setOnClickListener(v -> {
                try {
                    spaceManager.removeSpace(space);
                    saveViews();
                    showViews();
                } catch (CouldNotFindException e) {
                    Log.e("Water your plants", "Error deleting space", e);
                }
                dialog.dismiss();
            });

            dialog.show();

            Window window = dialog.getWindow();
            if (window != null) {
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            }
        });

        return frameLayout;
    }
}
