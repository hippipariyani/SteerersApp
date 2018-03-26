package com.heisen_berg.steerersapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.heisen_berg.steerersapp.Manifest;
import com.heisen_berg.steerersapp.R;
import com.heisen_berg.steerersapp.app.PrefManager;
import com.heisen_berg.steerersapp.ui.ChangeToolbarFont;
import com.heisen_berg.steerersapp.ui.SweetAlertDialogIosFont;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

import javax.security.auth.login.LoginException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.zelory.compressor.Compressor;

import static com.theartofdev.edmodo.cropper.CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE;

public class AddPostActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.btn_add_post)
    AppCompatImageButton addPost;
    @BindView(R.id.btn_add_image)
    AppCompatImageButton addImage;
    @BindView(R.id.btn_remove_image)
    AppCompatImageButton removeImage;
    @BindView(R.id.iv_image)
    ImageView image;
    @BindView(R.id.et_description)
    EditText et_description;

    private PrefManager prefManager;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private StorageReference mStorageRef;
    private FirebaseStorage storage;

    private HashMap<String, Object> data;
    private HashMap<String, String> photos;
    private Uri cropPhotoUri = null;
    Uri photoPath;
    private String id;

    private SweetAlertDialog pDialog;

    private static final int PICK_IMAGE_REQUEST = 234;
    private static final int READ_STORAGE_PERMISSION_REQUEST_CODE = 1234;

    private String description, authorId, authorName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        ButterKnife.bind(this);
        prefManager = PrefManager.getInstance(AddPostActivity.this);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance();
        mStorageRef = storage.getReferenceFromUrl("gs://steerersapp.appspot.com");

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_arrow_back_white));
        ChangeToolbarFont.changeToolbarFont(toolbar, AddPostActivity.this);

        authorId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        authorName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

        if (!checkPermissionForReadExtertalStorage()){
            try {
                requestPermissionForReadExtertalStorage();
            } catch (Exception e) {
                Log.i("xolo", "onCreate: "+e.toString());
                return;
            }
        }

        pDialog = new SweetAlertDialogIosFont(AddPostActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setCancelable(false);
    }

    @OnClick(R.id.btn_add_post)
    public void setAddPost(){
        description = et_description.getText().toString();
        if (description.isEmpty()){
            alertDialog("Missing!!!", "Add some description first!!!", 0);
            return;
        }
        if (cropPhotoUri == null){
            alertDialog("Missing", "Add an image first!!!", 0);
            return;
        }

        createPost();
    }

    @OnClick(R.id.btn_add_image)
    public void setAddImage(){
        if (!checkPermissionForReadExtertalStorage()){
            try {
                requestPermissionForReadExtertalStorage();
            } catch (Exception e) {
                Log.i("xolo", "onCreate: "+e.toString());
                return;
            }
        }
        if (cropPhotoUri ==  null){
            CropImage.startPickImageActivity(AddPostActivity.this);
        }
    }

    @OnClick(R.id.btn_remove_image)
    public void setRemoveImage(){
        if (cropPhotoUri != null){
            cropPhotoUri = null;
            image.setImageDrawable(null);
        } else {
            Toast.makeText(this, "Add image to remove!!!", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean checkPermissionForReadExtertalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    public void requestPermissionForReadExtertalStorage() throws Exception {
        try {
            ActivityCompat.requestPermissions(AddPostActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_STORAGE_PERMISSION_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            photoPath = data.getData();
        }


        try {
            if (requestCode == PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK) {
                Uri imageUri = CropImage.getPickImageResultUri(AddPostActivity.this, data);
                if (CropImage.isReadExternalStoragePermissionsRequired(AddPostActivity.this, imageUri)) {
                    cropPhotoUri = imageUri;
                    try {
                        requestPermissionForReadExtertalStorage();
                    } catch (Exception ex){
                        Log.i("xoxo", "onActivityResult: "+ex.toString());
                        return;
                    }
                } else {
                    startCropImageActivity(imageUri);
                }
            }
        } catch (Exception ex){}

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                cropPhotoUri = resultUri;
                try {
                    loadImageInImageView(cropPhotoUri);
                } catch (Exception ex){}
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setAspectRatio(4, 3)
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .start(AddPostActivity.this);
    }

    void loadImageInImageView(Uri cropPhotoUri) {
        Glide.with(this)
                .load(cropPhotoUri)
                .into(image);
    }

    private void createPost(){

        data = new HashMap<>();
        data.put("description", description);
        data.put("authorid", authorId);
        data.put("timestamp", ServerValue.TIMESTAMP);
        data.put("authorname", authorName);

        Log.i("xoxo", "createPost: "+data.toString()+" "+cropPhotoUri);

        mDatabase = FirebaseDatabase.getInstance().getReference("posts");
        id = mDatabase.push().getKey();
        try {
            uploadImages();
        } catch (URISyntaxException e) {
            Log.i("xoxo", "createPost: "+e.toString());
            return;
        } catch (IOException e) {
            Log.i("xoxo", "createPost: "+e.toString());
            return;
        }
    }

    private void uploadImages() throws URISyntaxException, IOException {
        pDialog.setTitleText("Posting...");
        pDialog.show();
        if (cropPhotoUri != null) {
            File compressedImage = new Compressor(AddPostActivity.this)
                    .setMaxWidth(1000)
                    .setMaxHeight(1000)
                    .setQuality(75)
                    .setCompressFormat(Bitmap.CompressFormat.JPEG)
                    .compressToFile(new File(new URI(cropPhotoUri.toString())));

            photoPath = Uri.fromFile(compressedImage);
            StorageReference reference = mStorageRef.child("posts").child(id).child("1");
            reference.putFile(photoPath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            photos = new HashMap<>();
                            photos.put("1", downloadUrl.toString());
                            data.put("photos", photos);
                            mDatabase.child(id).child("data").setValue(data);
                            alertDialog("Posted Successfully", "You have successfully Posted", 1);
                            pDialog.dismiss();
                            et_description.setText("");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            pDialog.dismissWithAnimation();
                            alertDialog("Error!!!", "Some error occured, contact developer!!!", 0);
                            Log.i("xoxo", "onFailure: "+exception.toString());
                        }
                    });
        }
    }

    private void alertDialog(String title, String description, int success) {
        if(success>0){
            new SweetAlertDialogIosFont(AddPostActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText(title)
                    .setContentText(description)
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismissWithAnimation();
                        }
                    })
                    .show();
        }else{
            new SweetAlertDialogIosFont(AddPostActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText(title)
                    .setContentText(description)
                    .show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.fade_in, R.anim.slide_out_down);
    }
}
