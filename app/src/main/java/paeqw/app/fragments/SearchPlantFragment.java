package paeqw.app.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import paeqw.app.R;

public class SearchPlantFragment extends Fragment {
    public SearchPlantFragment() {
        // Required empty public constructor
    }

    public static SearchPlantFragment newInstance() {
        SearchPlantFragment fragment = new SearchPlantFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_plant, container, false);
    }
}