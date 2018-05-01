package com.inntaglio.steerersapp.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.inntaglio.steerersapp.R;
import com.inntaglio.steerersapp.app.PrefManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AdminLoginActivity extends AppCompatActivity {

    @BindView(R.id.et_email) EditText etEmail;
    @BindView(R.id.et_password) EditText etPassword;
    @BindView(R.id.btn_admin_login)
    AppCompatImageButton adminLogin;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_admin_login);
        ButterKnife.bind(this);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        etPassword.setTransformationMethod(new PasswordTransformationMethod());

        if (mFirebaseUser != null){
            Log.i("xoxo", "onCreate: "+mFirebaseUser.getEmail());
        } else {
            Log.i("xoxo", "onCreate: user is null");
        }

        progressDialog();
    }

    private void progressDialog(){
//        pDialog = new SweetAlertDialogIosFont(AdminLoginActivity.this, SweetAlertDialog.PROGRESS_TYPE);
//        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
//        pDialog.setTitleText("Please wait...");
//        pDialog.setCancelable(false);
        ProgressDialog dialog = ProgressDialog.show(this, "Loading", "Please wait...", true);
        dialog.dismiss();
    }

    @OnClick(R.id.btn_admin_login)
    public void setAdminLogin(){
        emailSignInWithCredential();
    }

    private void emailSignInWithCredential(){

        String email = etEmail.getText().toString().trim();
        final String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Enter email address!!!");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Enter password!!!");
            return;
        }

        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception ex) {

        }

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AdminLoginActivity.this);

        AuthCredential mAuthCredential = EmailAuthProvider.getCredential(email, password);
        mFirebaseAuth.signInWithCredential(mAuthCredential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            PrefManager.getInstance(AdminLoginActivity.this).setKeyModule(false);
                            startActivity(new Intent(AdminLoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidUserException ex){
                                alertDialogBuilder.setMessage("You have to create account first!!!");
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
                            catch (FirebaseAuthInvalidCredentialsException ex){
                                alertDialogBuilder.setMessage("Please check you email and try again!!!");
                                alertDialogBuilder.setPositiveButton("Ok",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface arg0, int arg1) {
                                                finish();
                                            }
                                        });
                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();
                            } catch (FirebaseNetworkException ex){
                                alertDialogBuilder.setMessage("Please check your network connection!!!");
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
                            catch (Exception ex){
                                Log.i("xoxo", "ex: adminLogin: "+ex.toString());
                            }
                        }
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.fade_in, R.anim.slide_out_down);
    }
}
