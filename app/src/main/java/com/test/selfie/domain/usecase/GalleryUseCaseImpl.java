package com.test.selfie.domain.usecase;

import com.test.selfie.data.entity.AuthEntity;
import com.test.selfie.domain.AuthorizationRepository;
import com.test.selfie.domain.GalleryRepository;
import com.test.selfie.domain.model.Picture;
import com.test.selfie.domain.model.mapper.PictureMapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import io.reactivex.Single;

public class GalleryUseCaseImpl implements GalleryUseCase {

    private GalleryRepository mRepository;
    private AuthorizationRepository mAuthRepository;

    public GalleryUseCaseImpl(GalleryRepository repository,
                              AuthorizationRepository authRepository) {
        this.mRepository = repository;
        this.mAuthRepository = authRepository;
    }

    @Override
    public Single<List<Picture>> fetchPictures() {
        return getAccessToken()
                .flatMap(accessToken -> mRepository
                        .fetchPictures(accessToken)
                        .map(PictureMapper::transform));
    }

    @Override
    public Single<Picture> uploadPicture(final String name, final InputStream stream) {
        return getAccessToken()
                .flatMap(accessToken -> mRepository
                        .uploadPicture(name, getBytes(stream), accessToken)
                        .map(PictureMapper::transform));
    }

    private Single<String> getAccessToken() {
        return mAuthRepository
                .getAuthorization()
                .map(AuthEntity::getAccessToken);
    }

    private byte[] getBytes(InputStream stream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = stream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }
}
