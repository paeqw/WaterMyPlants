package paeqw.app.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import paeqw.app.R;
import paeqw.app.models.BlogPost;

public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.BlogViewHolder> {
    private List<BlogPost> blogPosts;

    public BlogAdapter(List<BlogPost> blogPosts) {
        this.blogPosts = blogPosts;
    }

    @NonNull
    @Override
    public BlogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_blog_post, parent, false);
        return new BlogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlogViewHolder holder, int position) {
        BlogPost blogPost = blogPosts.get(position);
        holder.title.setText(blogPost.getTitle());
        holder.content.setText(blogPost.getContent());
    }

    @Override
    public int getItemCount() {
        return blogPosts.size();
    }

    static class BlogViewHolder extends RecyclerView.ViewHolder {
        TextView title, content;

        BlogViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.blog_title);
            content = itemView.findViewById(R.id.blog_content);
        }
    }
}
