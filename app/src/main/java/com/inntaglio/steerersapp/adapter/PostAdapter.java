package com.inntaglio.steerersapp.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.design.widget.CheckableImageButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.inntaglio.steerersapp.R;
import com.inntaglio.steerersapp.model.Post;

import java.util.List;

/**
 * Created by heisen-berg on 24/02/18.
 */

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.UserViewHolder> {

    private List<Post> list;
    private Context context;
    private Activity activity;
    private PostAdapter.UserViewHolder mView;

    public PostAdapter(List<Post> list, Activity activity) {
        this.list = list;
        this.activity = activity;
    }

    @Override
    public PostAdapter.UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new PostAdapter.UserViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false));
    }

    @Override
    public void onBindViewHolder(final PostAdapter.UserViewHolder holder, final int position) {
        final Post model = list.get(position);

        mView = holder;

        Glide.with(context).load(model.getPhotos().get(0)).into(holder.imageView);
        holder.textView.setText(model.getDescription());

        final String firebaseUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("poststats").child(model.getId()).child("likes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                holder.tvLike.setText(String.valueOf(dataSnapshot.getChildrenCount())+" Votes");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i("xoxo", "onCancelled: "+databaseError.toString());
            }
        });

        databaseReference.child("poststats").child(model.getId()).child("likes").child(firebaseUser).addValueEventListener(new ValueEventListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if(dataSnapshot.getValue()!=null){
                        holder.likeIcon.setChecked(true);
                    }else{
                        holder.likeIcon.setChecked(false);
                    }
                }catch (Exception e){
                    Log.i("xoxo", "datasnapshot was null"+e.toString());
                    return;
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        holder.likeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference likeRef = databaseReference.child("poststats").child(model.getId()).child("likes").child(firebaseUser);
                likeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue()==null){
                            likeRef.setValue(ServerValue.TIMESTAMP);
                            holder.likeIcon.setChecked(true);
                        }else{
                            likeRef.removeValue();
                            holder.likeIcon.setChecked(false);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.i("xoxo", "onCancelled: "+databaseError.toString());
                    }
                });
            }
        });

        holder.tvLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference likeRef = databaseReference.child("poststats").child(model.getId()).child("likes").child(firebaseUser);
                likeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue()==null){
                            likeRef.setValue(ServerValue.TIMESTAMP);
                            holder.likeIcon.setChecked(true);
                        }else{
                            likeRef.removeValue();
                            holder.likeIcon.setChecked(false);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.i("xoxo", "onCancelled: "+databaseError.toString());
                    }
                });
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder{
        private ImageView imageView;
        private TextView textView, tvLike;
        private CheckableImageButton likeIcon;
        public UserViewHolder(final View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_image);
            textView = itemView.findViewById(R.id.tv_description);

            tvLike = itemView.findViewById(R.id.tv_like);
            likeIcon = itemView.findViewById(R.id.likeicon);
        }
    }
}
