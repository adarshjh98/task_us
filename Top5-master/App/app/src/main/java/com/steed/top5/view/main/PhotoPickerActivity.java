package com.steed.top5.view.main;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.steed.top5.BuildConfig;
import com.steed.top5.R;
import com.steed.top5.databinding.ActivityPhotoPickerBinding;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.steed.top5.util.Constants.SELECTED_PHOTO_EXTRA;


public class PhotoPickerActivity extends AppCompatActivity {
    private String TAG = "PhotoPickerActivity";
    private String photoPath;
    private final int CAMERA = 1;
    private final int GALLERY = 2;


    private Context mContext;
    private File photoFile = null;
    private ActivityPhotoPickerBinding binding;
    private Intent sourceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        binding = DataBindingUtil.setContentView(this, R.layout.activity_photo_picker);
        sourceIntent = getIntent();
        mContext = this;

        Log.d(TAG, "Showing profile photo dialog");


        final TextView headerTextView = binding.headerTextView;
        final ImageButton cameraButton = binding.cameraButton;
        final ImageButton galleryButton = binding.galleryButton;

        headerTextView.setText(R.string.title_edit_profile_photo);

        final View.OnClickListener buttonClickListener = v -> {
            final int id = v.getId();
            switch (id) {
                case R.id.galleryButton:
                    requestPhotoPermission(GALLERY);

                    Log.i(TAG, "Capturing photo from gallery");
                    break;
                case R.id.cameraButton:
                    requestPhotoPermission(CAMERA);

                    Log.i(TAG, "Capturing photo from camera");
                    break;
            }
        };


        cameraButton.setOnClickListener(buttonClickListener);
        galleryButton.setOnClickListener(buttonClickListener);



        //getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


    }




    /**
     * Request runtime permission to capture photos for Marshmallow devices.
     */
    @TargetApi(Build.VERSION_CODES.M)
    public void requestPhotoPermission(int sourceType) {
        int cameraPermissionCheck = ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.CAMERA);

        int writePermission = ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);


        if (cameraPermissionCheck != PackageManager.PERMISSION_GRANTED || writePermission != PackageManager.PERMISSION_GRANTED) {//Permission to use camera denied
            //Request the permission.

            Log.d(TAG, "Permission to capture image from camera denied, request permission");

            String[] cameraPermissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            String[] galleryPermissions = { Manifest.permission.WRITE_EXTERNAL_STORAGE};

//            if(sourceType == CAMERA){
//                permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
//            }

            //Log.d(TAG, "Requesting permission");
            ActivityCompat.requestPermissions(this,
                    cameraPermissions,
                    sourceType);


        } else {
            // permission was granted
            Log.d(TAG, "Permission to upload from camera already granted");

            switch (sourceType) {
                case CAMERA:
                    capturePhoto();
                    break;
                case GALLERY:
                    openGallery();
                    break;
            }


        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult--Photo upload");

        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            // permission was granted

            switch (requestCode) {
                case CAMERA:
                    capturePhoto();
                    break;
                case GALLERY:
                    openGallery();
                    break;
            }

        } else {

            // permission denied, boo! Disable the
            // functionality that depends on this permission.
            Toast.makeText(mContext, "Photo capture disabled", Toast.LENGTH_LONG).show();
        }


    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //File storageDir = Environment.getExternalStorageDirectory();

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }


    /**
     * Capture an image using a camera
     */
    private void capturePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);


        if (takePictureIntent.resolveActivity(mContext.getPackageManager()) != null) {

            try {
                photoFile = createImageFile();

                if (photoFile != null) {

                    //File photoFile1 = createImageFile();

                    String applicationId = BuildConfig.APPLICATION_ID;

                    Uri photoURI = FileProvider.getUriForFile(mContext,
                            applicationId + ".fileprovider",
                            photoFile);

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                        takePictureIntent.setClipData(ClipData.newRawUri("", photoURI));
                        takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }
                    startActivityForResult(takePictureIntent, CAMERA);
                }
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();


            }

        }

    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i(TAG, "onActivityResult");


        if (resultCode == RESULT_OK) {

            switch (requestCode) {
                case CAMERA:
                    photoPath = photoFile.getAbsolutePath();
                    break;


                case GALLERY:
                    Uri selectedImage = data.getData();
                    String[] filePath = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(selectedImage, filePath, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePath[0]);
                    photoPath = cursor.getString(columnIndex);

                    cursor.close();
                    break;

            }

            sourceIntent.putExtra(SELECTED_PHOTO_EXTRA, photoPath);
            setResult(RESULT_OK, sourceIntent);
            finish();


        }

    }



}
