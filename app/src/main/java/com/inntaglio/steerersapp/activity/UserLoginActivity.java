package com.inntaglio.steerersapp.activity;

import android.accounts.Account;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
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
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.inntaglio.steerersapp.R;
import com.inntaglio.steerersapp.app.PrefManager;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserLoginActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    @BindView(R.id.btn_default_google_login)
    SignInButton defaultGoogleLogin;
    @BindView(R.id.btn_custom_google_login)
    Button customGoogleLogin;

    @BindView(R.id.tv_mood_bana_de)
    TextView tvMoodBanaDe;

    public static final int RC_SIGN_IN = 9001;

    ProgressDialog progressDialog;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private GoogleApiClient mGoogleApiClient;
    private CallbackManager mCallbackManager;
    private Button fbBtn;

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

        // Initialize Facebook Login button
        fbBtn = (Button) findViewById(R.id.btn_custom_facebook_login) ;
        mCallbackManager = CallbackManager.Factory.create();
        fbBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LoginManager.getInstance().logInWithReadPermissions(UserLoginActivity.this, Arrays.asList("email","public_profile"));
                LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d("facebook login", "facebook:onSuccess:" + loginResult);
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Log.d("facebook login", "facebook:onCancel");
                        // ...
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d("facebook login", "facebook:onError", error);
                        // ...
                    }
                });
            }
        });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("facebook login", "handleFacebookAccessToken:" + token);

        final AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("facebook login", "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w("facebook login", "signInWithCredential", task.getException());
                            Toast.makeText(UserLoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            startActivity(new Intent(UserLoginActivity.this, AccountActivity.class));
                            finish();
                        }
                    }
                });
    }


    @OnClick(R.id.tv_mood_bana_de)
    public void setTvMoodBanaDe(){
        startActivity(new Intent(UserLoginActivity.this, AdminLoginActivity.class));
        overridePendingTransition(R.anim.slide_in_up, R.anim.fade_out);
    }

    private void progressDialog(){
        progressDialog = new ProgressDialog(UserLoginActivity.this);
        progressDialog.setMessage("Loading..."); // Setting Message
        progressDialog.setTitle("Please wait...!!!"); // Setting Title
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
        progressDialog.setCancelable(false);
    }


    @OnClick(R.id.btn_default_google_login)
    public void setDefaultGoogleLogin(){
        setGoogleSignIn();
        progressDialog.show();
    }

    @OnClick(R.id.btn_custom_facebook_login)
    public void click(){
        defaultGoogleLogin.performClick();
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
        progressDialog.dismiss();
        PrefManager.getInstance(UserLoginActivity.this).setKeyModule(true);
        startActivity(new Intent(UserLoginActivity.this, AccountActivity.class));
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        progressDialog.dismiss();

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

        // For google login
        if (requestCode == RC_SIGN_IN){
            String reqCode = String.valueOf(requestCode);
            Log.i("hippi",reqCode);
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()){
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                String rCode = String.valueOf(requestCode);
                Log.i("hippi",rCode);
                Toast.makeText(UserLoginActivity.this,"Login cancelled, please try again!!!",Toast.LENGTH_LONG).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        progressDialog.show();
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(UserLoginActivity.this);
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            try {
                                loadMainActivity();
                            } catch (Exception e) {
                                progressDialog.dismiss();
                                alertDialogBuilder.setMessage("Login Failed, Try Again !");
                                        alertDialogBuilder.setPositiveButton("Ok",
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface arg0, int arg1) {
                                                        finish();
                                                    }
                                                });
                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();
                                Log.i("xoxo", "onComplete: googleAuth: "+e.toString());
                            }
                        }else {
                            Log.i("xoxo", "onComplete: googleAuth: "+task.getException().toString());
                            progressDialog.dismiss();
                            alertDialogBuilder.setMessage("Login Failed, Try Again !");
                            alertDialogBuilder.setPositiveButton("Ok",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            finish();
                                        }
                                    });
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
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
