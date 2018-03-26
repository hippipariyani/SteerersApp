package com.heisen_berg.steerersapp.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageButton;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.GoogleAuthProvider;
import com.heisen_berg.steerersapp.BuildConfig;
import com.heisen_berg.steerersapp.R;
import com.heisen_berg.steerersapp.app.PrefManager;
import com.heisen_berg.steerersapp.ui.SweetAlertDialogIosFont;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import org.json.JSONException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import butterknife.OnTouch;

import static com.heisen_berg.steerersapp.app.Config.RC_SIGN_IN;

public class UserLoginActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    @BindView(R.id.btn_default_google_login)
    SignInButton defaultGoogleLogin;
    @BindView(R.id.btn_custom_google_login)
    AppCompatImageButton customGoogleLogin;
    @BindView(R.id.tv_mood_bana_de)
    TextView tvMoodBanaDe;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private GoogleApiClient mGoogleApiClient;

    private SweetAlertDialogIosFont pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_user_login);
        ButterKnife.bind(this);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if (mFirebaseUser != null){
            Log.i("xoxo", "onCreate: "+mFirebaseUser.getEmail());
        } else {
            Log.i("xoxo", "onCreate: user is null");
        }

        progressDialog();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        if (mGoogleApiClient == null || !mGoogleApiClient.isConnected()){
            try {
                mGoogleApiClient = new GoogleApiClient
                        .Builder(this)
                        .addConnectionCallbacks(this)
                        .enableAutoManage(this, this)
                        .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                        .build();
            } catch (Exception ex){
                Log.i("xoxo", "onCreate: userlog: "+ex.toString());
            }
        }
    }

    @OnClick(R.id.tv_mood_bana_de)
    public void setTvMoodBanaDe(){
        startActivity(new Intent(UserLoginActivity.this, AdminLoginActivity.class));
        overridePendingTransition(R.anim.slide_in_up, R.anim.fade_out);
    }

    private void progressDialog(){
        pDialog = new SweetAlertDialogIosFont(UserLoginActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Please wait..");
        pDialog.setCancelable(false);
    }

    @OnClick(R.id.btn_default_google_login)
    public void setDefaultGoogleLogin(){
        setGoogleSignIn();
        pDialog.show();
    }

    @OnClick(R.id.btn_custom_google_login)
    public void setCustomGoogleLogin(){
        setDefaultGoogleLogin();
    }

    private void setGoogleSignIn() {
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(intent, RC_SIGN_IN);
    }

    private void loadMainActivity(){
        pDialog.cancel();
        PrefManager.getInstance(UserLoginActivity.this).setKeyModule(true);
        startActivity(new Intent(UserLoginActivity.this, MainActivity.class));
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        pDialog.cancel();
        if (requestCode == RC_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()){
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                new SweetAlertDialogIosFont(UserLoginActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Cancelled!!!")
                        .setContentText("Login cancelled, please try again!!!").show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        pDialog.show();
        final SweetAlertDialogIosFont eDialog = new SweetAlertDialogIosFont(UserLoginActivity.this, SweetAlertDialog.ERROR_TYPE);
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            try {
                                loadMainActivity();
                            } catch (Exception e) {
                                pDialog.cancel();
                                eDialog.setTitleText("Failed!!!")
                                        .setContentText("Login failed, please try again!!!")
                                        .show();
                                Log.i("xoxo", "onComplete: googleAuth: "+e.toString());
                            }
                        }else {
                            Log.i("xoxo", "onComplete: googleAuth: "+task.getException().toString());
                            pDialog.cancel();
                            eDialog.setTitleText("Failed!!!")
                                    .setContentText("Login failed, please try again!!!")
                                    .show();
                        }
                    }
                });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGoogleApiClient.stopAutoManage(UserLoginActivity.this);
        mGoogleApiClient.disconnect();
    }
}
