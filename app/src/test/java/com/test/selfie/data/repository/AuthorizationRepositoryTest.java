package com.test.selfie.data.repository;

import com.test.selfie.data.datasource.authorization.AuthorizationDataSource;
import com.test.selfie.data.entity.AuthEntity;
import com.test.selfie.shared.GsonSingleton;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.LinkedHashMap;

import io.reactivex.Single;
import io.reactivex.observers.TestObserver;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AuthorizationRepositoryTest {

    @Mock
    private AuthorizationDataSource mRemoteDataSource;

    @Mock
    private AuthorizationDataSource mLocalDataSource;

    private AuthorizationRepositoryImpl mRepository;
    private TestObserver<AuthEntity> mTestObserver;

    private String mJsonRemoteEntity = "{\n" +
            "  \"access_token\": \"remote_access_token\",\n" +
            "  \"token_type\": \"remote_token_type\",\n" +
            "  \"refresh_token\": \"remote_refresh_token\",\n" +
            "  \"id_token\": \"remote_id_token\",\n" +
            "  \"expires_in\": 3600\n" +
            "}";

    private String mJsonLocalEntity = "{\n" +
            "  \"access_token\": \"local_access_token\",\n" +
            "  \"token_type\": \"local_token_type\",\n" +
            "  \"refresh_token\": \"local_refresh_token\",\n" +
            "  \"id_token\": \"local_id_token\",\n" +
            "  \"expires_in\": 3600\n" +
            "}";

    private String mJsonCachedEntity = "{\n" +
            "  \"access_token\": \"cached_access_token\",\n" +
            "  \"token_type\": \"cached_token_type\",\n" +
            "  \"refresh_token\": \"cached_refresh_token\",\n" +
            "  \"id_token\": \"cached_id_token\",\n" +
            "  \"expires_in\": 3600\n" +
            "}";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        mTestObserver = new TestObserver<>();

        // Created a spy in order to verify whether some real methods was called
        mRepository = spy((AuthorizationRepositoryImpl) AuthorizationRepositoryImpl
                .getInstance("", "", "",
                        mRemoteDataSource, mLocalDataSource));

        when(mRemoteDataSource
                .getAuthorization(anyString(), anyString(), anyString()))
                .thenReturn(Single.just(mJsonRemoteEntity));

        when(mLocalDataSource
                .getAuthorization(anyString(), anyString(), anyString()))
                .thenReturn(Single.just(""));

        // Json remote is always returning because storeLocalAuthorization() is only called
        // after getting data from remote
        when(mLocalDataSource
                .storeLocalAuthorization(anyString()))
                .thenReturn(Single.just(mJsonRemoteEntity));
    }

    @After
    public void destroySingletonInstance() {
        AuthorizationRepositoryImpl.destroyInstance();
    }

    @Test
    public void getAuthorizationFromRemote_saveInCacheAndLocal() {
        mRepository.getAuthorization().subscribe(mTestObserver);
        mTestObserver.assertValue(GsonSingleton.getGson()
                .fromJson(mJsonRemoteEntity, AuthEntity.class));

        verify(mLocalDataSource).getAuthorization(anyString(), anyString(), anyString());
        verify(mRemoteDataSource).getAuthorization(anyString(), anyString(), anyString());
        verify(mLocalDataSource).storeLocalAuthorization(anyString());

        assertThat(mRepository.mCache.get(AuthEntity.class.getName()), is(mJsonRemoteEntity));
    }

    @Test
    public void getAuthorizationFromCache_notExpired() {
        setCacheAvailable();
        setAuthExpired(false);

        mRepository.getAuthorization().subscribe(mTestObserver);
        mTestObserver.assertValue(GsonSingleton.getGson()
                .fromJson(mJsonCachedEntity, AuthEntity.class));

        verify(mLocalDataSource, never()).getAuthorization(anyString(), anyString(), anyString());
        verify(mRemoteDataSource, never()).getAuthorization(anyString(), anyString(), anyString());
        verify(mLocalDataSource, never()).storeLocalAuthorization(anyString());
    }

    @Test
    public void getAuthorizationFromCache_expired() {
        setCacheAvailable();
        setAuthExpired(true);

        mRepository.getAuthorization().subscribe(mTestObserver);
        mTestObserver.assertValue(GsonSingleton.getGson()
                .fromJson(mJsonRemoteEntity, AuthEntity.class));

        verify(mLocalDataSource, never()).getAuthorization(anyString(), anyString(), anyString());
        verify(mRemoteDataSource).getAuthorization(anyString(), anyString(), anyString());
        verify(mLocalDataSource).storeLocalAuthorization(anyString());

        assertThat(mRepository.mCache.get(AuthEntity.class.getName()), is(mJsonRemoteEntity));
    }

    @Test
    public void getAuthorizationFromLocal_notExpired() {
        setLocalAvailable();
        setAuthExpired(false);

        mRepository.getAuthorization().subscribe(mTestObserver);
        mTestObserver.assertValue(GsonSingleton.getGson()
                .fromJson(mJsonLocalEntity, AuthEntity.class));

        verify(mLocalDataSource).getAuthorization(anyString(), anyString(), anyString());
        verify(mRemoteDataSource, never()).getAuthorization(anyString(), anyString(), anyString());

        assertThat(mRepository.mCache.get(AuthEntity.class.getName()), is(mJsonLocalEntity));
    }

    @Test
    public void getAuthorizationFromLocal_expired() {
        setLocalAvailable();
        setAuthExpired(true);

        mRepository.getAuthorization().subscribe(mTestObserver);
        mTestObserver.assertValue(GsonSingleton.getGson()
                .fromJson(mJsonRemoteEntity, AuthEntity.class));

        verify(mLocalDataSource).getAuthorization(anyString(), anyString(), anyString());
        verify(mRemoteDataSource).getAuthorization(anyString(), anyString(), anyString());
        verify(mLocalDataSource).storeLocalAuthorization(anyString());

        assertThat(mRepository.mCache.get(AuthEntity.class.getName()), is(mJsonRemoteEntity));
    }

    private void setLocalAvailable() {
        when(mLocalDataSource.getAuthorization(anyString(), anyString(), anyString()))
                .thenReturn(Single.just(mJsonLocalEntity));
    }

    private void setCacheAvailable() {
        mRepository.mCache = new LinkedHashMap<>();
        mRepository.mCache.put(AuthEntity.class.getName(), mJsonCachedEntity);
    }

    private void setAuthExpired(boolean param) {
        doReturn(param).when(mRepository).isExpired(any());
    }
}