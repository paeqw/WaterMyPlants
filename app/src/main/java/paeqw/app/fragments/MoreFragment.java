package paeqw.app.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import paeqw.app.R;
import paeqw.app.activities.LoginActivity;
import paeqw.app.helpers.SharedPreferencesHelper;

public class MoreFragment extends Fragment {
    public MoreFragment() {

    }
    Button button;
    TextView textView;
    ImageView profileImage;
    public static MoreFragment newInstance() {
        MoreFragment fragment = new MoreFragment();
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
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        View rootView = inflater.inflate(R.layout.fragment_more, container, false);
        initViews(rootView);
        Glide.with(rootView).load(firebaseUser.getPhotoUrl()).apply(RequestOptions.circleCropTransform()).into(profileImage);
        textView.setText(firebaseUser.getDisplayName());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(getContext());
                sharedPreferencesHelper.clearSpaces();
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });
        return rootView;
    }
    private void initViews(View rootView) {
        button = rootView.findViewById(R.id.button);
        textView = rootView.findViewById(R.id.textViewUsernameLabel);
        profileImage = rootView.findViewById(R.id.rounded_image_view);
    }
}