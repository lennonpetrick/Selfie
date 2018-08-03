package com.test.selfie;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;

import com.test.selfie.utils.SdkUtils;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class GalleryActivity extends AppCompatActivity implements GalleryContract.View {

    private Uri mTempFileUri;

    private final static int CAMERA_PERMISSION_REQUEST_CODE = 1;
    private final static int CAMERA_REQUEST_CODE = 2;

    //@BindView(R.id.fabNewPicture_gallery) FloatingActionButton mFabNewPicture;

    private GalleryContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        ButterKnife.bind(this);

        mPresenter = new GalleryPresenter(this);
    }

    @Override
    protected void onDestroy() {
        mPresenter = null;
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (!SdkUtils.isNougat()) {
                    mTempFileUri = CropImage.getPickImageResultUri(this, data);
                }

                CropImage.activity(mTempFileUri).start(this);
            } else {
                getContentResolver()
                        .delete(mTempFileUri, null, null);
            }
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
                && resultCode == RESULT_OK) {
            mTempFileUri = CropImage.getActivityResult(data).getUri();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    startCameraActivity();
                }
                break;
            }
        }
    }

    @Override
    public void showError(String error) {
        Snackbar.make(getWindow().getDecorView(), error, Snackbar.LENGTH_LONG)
                .show();
    }

    @OnClick(R.id.fabNewPicture_gallery)
    public void checkForPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED

                || ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED

                || ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[] {
                            Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            startCameraActivity();
        }
    }

    private void startCameraActivity() {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getPackageManager()) != null) {

                if (SdkUtils.isNougat()) {
                    mTempFileUri = FileProvider.getUriForFile(this,
                            BuildConfig.APPLICATION_ID.concat(".provider"), createTempFile());
                } else {
                    mTempFileUri = CropImage.getCaptureImageOutputUri(this);
                }

                if (mTempFileUri != null) {
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mTempFileUri);
                    startActivityForResult(intent, CAMERA_REQUEST_CODE);
                }
            }
        } catch (IOException e) {
            showError(e.getMessage());
        }
    }

    private File createTempFile() throws IOException {
        File directory = new File(Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
                "Selfie");
        directory.mkdirs();

        final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        final  String fileName = "img_".concat(timeStamp);

        return File.createTempFile(fileName, ".jpg", directory);
    }

    private String prepareBitmapToSave(Bitmap bitmap) {

        ByteArrayOutputStream _byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 0, _byteArrayOutputStream);

        String _imageEncoded = Base64.encodeToString(_byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

        return _imageEncoded;
    }
}
