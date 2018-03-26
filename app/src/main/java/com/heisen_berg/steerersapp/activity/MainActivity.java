package com.heisen_berg.steerersapp.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.heisen_berg.steerersapp.R;
import com.heisen_berg.steerersapp.adapter.PostAdapter;
import com.heisen_berg.steerersapp.app.IsLoadingVariable;
import com.heisen_berg.steerersapp.app.PrefManager;
import com.heisen_berg.steerersapp.model.Post;
import com.heisen_berg.steerersapp.ui.ChangeToolbarFont;
import com.heisen_berg.steerersapp.ui.SweetAlertDialogIosFont;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference databaseReference;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_main)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.btn_add_post)
    AppCompatImageButton addPost;

    private List<Post> list;
    private PostAdapter postAdapter;
    private PrefManager prefManager;
    private GoogleApiClient mGoogleApiClient;
    IsLoadingVariable isLoadingVariable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (!isAuthenticated()){
            startActivity(new Intent(MainActivity.this, UserLoginActivity.class));
            finish();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        prefManager = PrefManager.getInstance(MainActivity.this);

        if (prefManager.getKeyModule()){
            addPost.setVisibility(View.GONE);
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(MainActivity.this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        databaseReference = FirebaseDatabase.getInstance().getReference("posts");


        setSupportActionBar(toolbar);
        if (prefManager.getKeyModule()){
            getSupportActionBar().setTitle("Home");
        } else {
            getSupportActionBar().setTitle("Home (Admin)");
        }
        ChangeToolbarFont.changeToolbarFont(toolbar, MainActivity.this);
        toolbar.setOverflowIcon(getResources().getDrawable(R.drawable.ic_settings_white));

        Log.i("xoxo", "onCreate: "+prefManager.getKeyModule());
        if (mFirebaseUser != null){
            Log.i("xoxo", "onCreate: "+mFirebaseUser.getEmail());
        } else {
            Log.i("xoxo", "onCreate: user is null");
        }

        list = new ArrayList<>();
        updateList();
        LinearLayoutManager manager = new LinearLayoutManager(MainActivity.this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        postAdapter = new PostAdapter(list, MainActivity.this);
        recyclerView.setAdapter(postAdapter);

        swipeRefreshLayout.setOnRefreshListener(this);
        isLoadingVariable = new IsLoadingVariable();
        isLoadingVariable.setIsLoading(true);
        isLoadingVariable.setListener(new IsLoadingVariable.ChangeListener() {
            @Override
            public void onChange() {
                if(isLoadingVariable.isLoading()){
                    updateList();
                }else{
                    Log.i("xoxo", "Not Loading Layout!!!");
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        new Handler().postDelayed(new Runnable() {
            @Override public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        } , 1500);
        isLoadingVariable.setIsLoading(true);
    }

    private void updateList(){

        final SweetAlertDialogIosFont pDialog = new SweetAlertDialogIosFont(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.setTitleText("Fetching data...");
        pDialog.setContentText("Make sure you have working wifi or data connection...");
        pDialog.show();
        pDialog.setCancelable(false);

        list.clear();
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Post post = new Post();
                post.setId(dataSnapshot.getKey());
                post.setDescription(dataSnapshot.child("data").child("description").getValue().toString());
                ArrayList<String> photos = new ArrayList<>();
                for(DataSnapshot temp : dataSnapshot.child("data").child("photos").getChildren()){
                    photos.add(temp.getValue().toString());
                    post.setPhotos(photos);
                }
                list.add(post);
                postAdapter.notifyDataSetChanged();
                pDialog.dismiss();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i("xoxo", "onCancelled: "+databaseError.toString());
            }
        });
    }

    private int getItemIndex(Post model){
        int index = -1;
        for (int i = 0; i < list.size(); i++){
            if (list.get(i).id.equals(model.id) ){
                index = i;
                break;
            }
        }
        return index;
    }

    @OnClick(R.id.btn_add_post)
    public void setAddPost(){
        startActivity(new Intent(MainActivity.this, AddPostActivity.class));
        overridePendingTransition(R.anim.slide_in_up, R.anim.fade_out);
    }

    private Boolean isAuthenticated() {
        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            return false;
        }else{
            return true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_logout:
                logout();
                break;
        }
        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void logout(){
        new SweetAlertDialogIosFont(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(getResources().getString(R.string.dialog_logout_title))
                .setContentText(getResources().getString(R.string.dialog_logout_message))
                .setConfirmText(getResources().getString(R.string.dialog_logout_confirm_text))
                .setCancelText(getResources().getString(R.string.dialog_logout_cancel_text))
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        PrefManager.getInstance(MainActivity.this).clearSession();
                        try {
                            prefManager.clearSession();
                            mFirebaseAuth.signOut();
                            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                                @Override
                                public void onResult(@NonNull Status status) {
                                    startActivity(new Intent(MainActivity.this, UserLoginActivity.class));
                                    finish();
                                }
                            });
                            finish();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                })
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                    }
                }).show();
    }
}
