package hu.ait.android.instant.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import hu.ait.android.instant.BottomNavActivity;
import hu.ait.android.instant.FragmentProfile;
import hu.ait.android.instant.R;
import hu.ait.android.instant.data.DataManager;
import hu.ait.android.instant.data.Post;
import hu.ait.android.instant.data.User;

public class PostsAdapter
        extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    private Context context;
    private List<Post> postList;
    private List<String> postKeys;
    private String uId;
    private DatabaseReference postsRef;

    public PostsAdapter(Context context, String uId) {
        this.context = context;
        this.uId = uId;

        postList = new ArrayList<Post>();
        postKeys = new ArrayList<String>();

        postsRef = FirebaseDatabase.getInstance().getReference();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_posts, parent, false);
        return new ViewHolder(row);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Post post = postList.get(position);

        holder.tvAuthor.setText(post.getAuthor());
        holder.tvBody.setText(post.getBody());
        holder.tvTitle.setText(post.getTitle());

        if (post.getImageUrl() != null) {
            Glide.with(context).load(post.getImageUrl()).into(holder.ivPostImg);
            holder.ivPostImg.setVisibility(View.VISIBLE);
        } else {
            holder.ivPostImg.setVisibility(View.GONE);
        }

        if(post.getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    removePost(position);
                }
            });
        }

        final User userInfo = getUser(post.getUid());

        if(userInfo.getPhotoURL() != null)
            Glide.with(context).load(userInfo.getPhotoURL()).into(holder.ivProfAvatar);

        holder.layoutAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataManager.getInstance().setData(userInfo.getUId());
                ((BottomNavActivity)context).showFragment(FragmentProfile.TAG);
            }
        });
    }

    public User getUser(String uId) {
        if(uId.equals(DataManager.getInstance().getCurrentUser().getUId())) {
            return DataManager.getInstance().getCurrentUser();
        } else {
            return DataManager.getUser(uId);
        }
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public void addPost(Post post, String key) {
        postList.add(post);
        postKeys.add(key);

        notifyDataSetChanged();
    }

    public void removePost(int index) {
        postsRef.child(postKeys.get(index)).removeValue();


        postList.remove(index);
        postKeys.remove(index);
        notifyItemRemoved(index);
    }

    public void removePostByKey(String key) {
        int index = postKeys.indexOf(key);
        if (index != -1) {
            postList.remove(index);
            postKeys.remove(index);
            notifyItemRemoved(index);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvAuthor;
        public TextView tvTitle;
        public TextView tvBody;
        public ImageButton btnDelete;
        public ImageView ivPostImg;
        public ImageView ivProfAvatar;
        public LinearLayout layoutAccount;

        public ViewHolder(View itemView) {
            super(itemView);

            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvBody = itemView.findViewById(R.id.tvBody);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            ivPostImg = itemView.findViewById(R.id.ivPostImg);
            ivProfAvatar = itemView.findViewById(R.id.ivProfAvatar);
            layoutAccount = itemView.findViewById(R.id.layoutAccount);
        }
    }
}

