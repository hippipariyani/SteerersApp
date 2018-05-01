//package com.heisen_berg.steerersapp.ui;
//
//import android.annotation.SuppressLint;
//import android.app.Activity;
//import android.content.Context;
//import android.location.Location;
//import android.util.Log;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.bumptech.glide.Glide;
//import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.location.LocationRequest;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ServerValue;
//import com.google.firebase.database.ValueEventListener;
//import com.heisen_berg.steerersapp.R;
//import com.heisen_berg.steerersapp.adapter.PostAdapter;
//import com.heisen_berg.steerersapp.model.Post;
//
///**
// * Created by heisen-berg on 25/02/18.
// */
//
//public class PostView extends PostAdapter.UserViewHolder implements View.OnClickListener{
//
//    Context context;
//    android.support.design.widget.CheckableImageButton likeicon;
//    Post post;
//    DatabaseReference mDatabase;
//    Activity activity;
//    ImageView image, verified;
//    TextView description, likecount;
//    View like;
//    String firebaseUser;
//    View v;
//    GoogleApiClient mGoogleApiClient;
//    LocationRequest mLocationRequest;
//    Location mLastLocation;
//    int REQUEST_CHECK_SETTINGS = 100;
//
//    public PostView(View v, Context context, Activity activity){
//        super(v);
//        this.context = context;
//        this.activity = activity;
//        initialize(v, context, activity);
//        initializeListeners();
//    }
//
//    private void publicPostOpened(){
//
//    }
//
//    public void setPublicPost(final Post post){
//        this.post = post;
//        try{
//            Glide.with(context).load(post.getPhotos().get(0)).into(image);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//        if(post.getDescription().equals("")){
//            description.setVisibility(View.GONE);
//        }else{
//            description.setText(post.getDescription());
//        }
//
//        mDatabase.child("poststats").child(post.getId()).child("likes").child(firebaseUser).addValueEventListener(new ValueEventListener() {
//            @SuppressLint("RestrictedApi")
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                try {
//                    if(dataSnapshot.getValue()!=null){
//                        likeicon.setChecked(true);
//                    }else{
//                        likeicon.setChecked(false);
//                    }
//                }catch (Exception e){
//                    Log.i("Tag", "datasnapshot was null");
//                }
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {}
//        });
//    }
//
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()){
//            case R.id.like:
//                likePost();
//                break;
//            case R.id.likeicon:
//                likePost();
//                break;
//        }
//    }
//
//    private void likePost(){
//        final DatabaseReference likeRef = mDatabase.child("poststats").child(post.getId()).child("likes").child(firebaseUser);
//        likeRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @SuppressLint("RestrictedApi")
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if(dataSnapshot.getValue()==null){
//                    likeRef.setValue(ServerValue.TIMESTAMP);
//                    likeicon.setChecked(true);
//                    changeLikeCount(likecount.getText().toString(), false);
//                }else{
//                    likeRef.removeValue();
//                    likeicon.setChecked(false);
//                    changeLikeCount(likecount.getText().toString(), true);
//                }
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }
//
//    private void initialize(View v, Context context, Activity activity){
//        mDatabase = FirebaseDatabase.getInstance().getReference();
//        this.v = v;
//        this.context = context;
//        this.activity = activity;
//        this.image = v.findViewById(R.id.image);
//        this.description = v.findViewById(R.id.tv_description);
//        this.likeicon = v.findViewById(R.id.likeicon);
//        this.like = v.findViewById(R.id.like);
//        this.likecount = v.findViewById(R.id.tv_like);
//        firebaseUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
//    }
//
//    private void initializeListeners(){
//        this.like.setOnClickListener(this);
//        this.likeicon.setOnClickListener(this);
//    }
//
//    private void changeLikeCount(String numlikes, Boolean status){
//        Long l = null;
//        if (!numlikes.equals("Like")){
//            l = Long.valueOf(numlikes);
//        }
//        if (status){
//            if (!numlikes.equals("Like")){
//                likecount.setText(String.valueOf(l-1));
//            } else if (numlikes.equals("Like")){
//                likecount.setText("1");
//            }
//        } else {
//            if (!numlikes.equals("Like")){
//                likecount.setText(String.valueOf(l+1));
//            }
//        }
//    }
//}
