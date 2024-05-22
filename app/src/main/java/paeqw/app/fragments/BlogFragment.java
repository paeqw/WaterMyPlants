package paeqw.app.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import paeqw.app.R;
import paeqw.app.helpers.DatabaseHelper;
import paeqw.app.models.BlogPost;

import java.util.List;

public class BlogFragment extends Fragment {
    private RecyclerView recyclerView;
    private BlogAdapter blogAdapter;
    private DatabaseHelper databaseHelper;

    public BlogFragment() {
    }

    public static BlogFragment newInstance() {
        BlogFragment fragment = new BlogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseHelper = new DatabaseHelper();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blog, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fetchBlogPosts();



        return view;
    }

    private void fetchBlogPosts() {
        databaseHelper.fetchBlogPosts()
                .thenAccept(blogPosts -> {
                    blogAdapter = new BlogAdapter(blogPosts);
                    recyclerView.setAdapter(blogAdapter);
                })
                .exceptionally(throwable -> {
                    throwable.printStackTrace();
                    return null;
                });
    }
}
