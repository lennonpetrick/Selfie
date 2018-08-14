package com.test.selfie.gallery;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.test.selfie.BuildConfig;
import com.test.selfie.R;
import com.test.selfie.data.datasource.authorization.CloudAuthorizationDataSource;
import com.test.selfie.data.datasource.authorization.LocalAuthorizationDataSource;
import com.test.selfie.data.datasource.gallery.CloudGalleryDataSource;
import com.test.selfie.data.repository.AuthorizationRepositoryImpl;
import com.test.selfie.data.repository.GalleryRepositoryImpl;
import com.test.selfie.domain.AuthorizationRepository;
import com.test.selfie.domain.model.Picture;
import com.test.selfie.domain.usecase.GalleryUseCase;
import com.test.selfie.domain.usecase.GalleryUseCaseImpl;
import com.test.selfie.utils.MessageUtils;
import com.test.selfie.utils.SdkUtils;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GalleryActivity extends AppCompatActivity implements GalleryContract.View {

    public final static String OAUTH_CODE_EXTRA = "oauth_code_extra";

    private final static int CAMERA_PERMISSION_REQUEST_CODE = 1;
    private final static int CAMERA_REQUEST_CODE = 2;

    @BindView(R.id.recyclerPictures_gallery) RecyclerView mRecyclerPictures;
    @BindView(R.id.imgNoContent_gallery) ImageView mImgNoContent;

    private String mTempFileName;
    private Uri mTempFileUri;
    private ProgressDialog mProgress;

    private GalleryContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        ButterKnife.bind(this);
        setUpActionBar();
        setUpRecycleView();

        String oauthCode = null;
        Intent intent = getIntent();
        if (intent != null) {
            oauthCode = intent.getStringExtra(OAUTH_CODE_EXTRA);
        }

        AuthorizationRepository authRepository = AuthorizationRepositoryImpl
                .getInstance(
                        BuildConfig.SERVER_CLIENT_ID,
                        BuildConfig.SERVER_CLIENT_SECRET,
                        oauthCode,
                        new CloudAuthorizationDataSource(),
                        new LocalAuthorizationDataSource(this));

        GalleryUseCase useCase = new GalleryUseCaseImpl(
                new GalleryRepositoryImpl(new CloudGalleryDataSource()), authRepository);

        mPresenter = new GalleryPresenter(this, useCase);
        mPresenter.loadPictures();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        if (mProgress != null) {
            // To avoid window leak
            mProgress.dismiss();
            mProgress = null;
        }

        mPresenter.destroy();
        mPresenter = null;
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (!SdkUtils.isNougatOrBigger()) {
                    mTempFileUri = CropImage.getPickImageResultUri(this, data);
                }

                CropImage.activity(mTempFileUri).start(this);
            } else {
                getContentResolver()
                        .delete(mTempFileUri, null, null);
            }
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
                && resultCode == RESULT_OK) {
            try {
                mTempFileUri = CropImage
                        .getActivityResult(data)
                        .getUri();
                mPresenter.savePicture(mTempFileName,
                        getContentResolver().openInputStream(mTempFileUri));
            } catch (FileNotFoundException e) {
                showError(e.getMessage());
            }
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
    public void showError(@NonNull String error) {
        MessageUtils.showError(this, error);
    }

    @Override
    public void showProgress(int stringRes) {
        if (mProgress == null) {
            mProgress = new ProgressDialog(this);
        }

        mProgress.setCancelable(false);
        mProgress.setMessage(getString(stringRes));
        mProgress.show();
    }

    @Override
    public void hideProgress() {
        if (mProgress != null) {
            mProgress.dismiss();
        }
    }

    @Override
    public void showGalleryEmpty(boolean param) {
        if (param) {
            mRecyclerPictures.setVisibility(View.GONE);
            mImgNoContent.setVisibility(View.VISIBLE);
        } else {
            mImgNoContent.setVisibility(View.GONE);
            mRecyclerPictures.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void refreshGallery(@NonNull List<Picture> pictures) {
        ((GalleryAdapter) mRecyclerPictures.getAdapter())
                .setPictures(pictures);
    }

    @Override
    public void addInGallery(@NonNull Picture picture) {
        if (mRecyclerPictures.getVisibility() == View.GONE) {
            showGalleryEmpty(false);
        }

        ((GalleryAdapter) mRecyclerPictures.getAdapter())
                .addPicture(picture);
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

    private void setUpActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setUpRecycleView() {
        mRecyclerPictures.setHasFixedSize(false);
        mRecyclerPictures.addItemDecoration(new CustomItemDecorator(5));
        mRecyclerPictures.setLayoutManager(new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL));

        GalleryAdapter adapter = new GalleryAdapter(new ArrayList<>());
        adapter.setOnItemClickListener((picture, position) -> {
            //TODO implement something
        });
        mRecyclerPictures.setAdapter(adapter);
    }

    private void startCameraActivity() {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getPackageManager()) != null) {

                if (SdkUtils.isNougatOrBigger()) {
                    File tempFile = createTempFile();
                    mTempFileName = tempFile.getName();
                    mTempFileUri = FileProvider.getUriForFile(this,
                            BuildConfig.APPLICATION_ID.concat(".provider"), tempFile);
                } else {
                    mTempFileUri = CropImage.getCaptureImageOutputUri(this);
                    mTempFileName = createTempFileName();
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

        return File.createTempFile(createTempFileName(), ".jpg", directory);
    }

    private String createTempFileName() {
        final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        return "img_".concat(timeStamp);
    }
}
