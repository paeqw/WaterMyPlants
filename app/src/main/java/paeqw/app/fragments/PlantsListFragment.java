package paeqw.app.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import paeqw.app.R;
import paeqw.app.collections.SpaceManager;
import paeqw.app.exceptions.CouldNotFindException;
import paeqw.app.models.Space;

public class PlantsListFragment extends Fragment {
    LinearLayout linearLayout;
    SpaceManager spaceManager;
    Button addSpaceButton;
    public PlantsListFragment() {

    }

    public static PlantsListFragment newInstance() {
        PlantsListFragment fragment = new PlantsListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_plants_list, container, false);
        initViews(rootView);
        initListeners();
        spaceManager = new SpaceManager(getActivity());
        loadViews();
        showViews();

        return rootView;
    }
    public void showViews() {
        linearLayout.removeAllViews();
        for (Space el : spaceManager.getSpaceList()) {
            linearLayout.addView(generateSpaceView(getActivity(),el));
        }
    }
    public void loadViews(){
        spaceManager.loadFromSharedPreferences();
    }
    public void saveViews(){
        spaceManager.saveToSharedPreferences();
    }
    public void initViews(View rootView){
        linearLayout = rootView.findViewById(R.id.linear);
        addSpaceButton = rootView.findViewById(R.id.addSpaceButton);
    }

    public void initListeners(){
        addSpaceButton.setOnClickListener(v -> addSpaceButtonClicked());
    }
    public void addSpaceButtonClicked() {
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
    public View generateSpaceView(Context context, Space space) {
        LayoutInflater inflater = LayoutInflater.from(context);
        FrameLayout frameLayout = (FrameLayout) inflater.inflate(R.layout.space_view_template, null);

        TextView nameTextView = frameLayout.findViewById(R.id.space_name);
        nameTextView.setText(space.getSpaceName());

        TextView plantCountTextView = frameLayout.findViewById(R.id.plant_count);
        plantCountTextView.setText("Plants: " + space.getPlantList().size());

        Button moreButton = frameLayout.findViewById(R.id.more_button);
        moreButton.setOnClickListener(view -> {
            try {
                spaceManager.removeSpace(space);
                saveViews();
                showViews();
            } catch (CouldNotFindException e) {
                Log.e("Water your plants", "What", e);
            }
        });

        return frameLayout;
    }
}