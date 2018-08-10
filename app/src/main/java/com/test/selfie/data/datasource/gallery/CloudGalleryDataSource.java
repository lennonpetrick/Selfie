package com.test.selfie.data.datasource.gallery;

import com.android.volley.AuthFailureError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.test.selfie.EndpointUtils;
import com.test.selfie.application.AppController;
import com.test.selfie.data.entity.PictureEntity;
import com.test.selfie.data.entity.mapper.JsonMapper;

import org.json.JSONException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Single;

public class CloudGalleryDataSource implements GalleryDataSource {

    private static final String ROUTE = "storage/v1/b/selfie-project-bucket/o";

    @Override
    public Single<List<PictureEntity>> fetchPictures(final String accessToken) {
        return Single.create(e -> {
            final String url = EndpointUtils.getUrl(ROUTE);
            JsonObjectRequest request = new JsonObjectRequest(url, null,
                    response -> {
                try {
                    e.onSuccess(JsonMapper
                            .transformPictureEntities(response.getString("items")));
                } catch (JSONException e1) {
                    e.tryOnError(e1);
                }
            }, e::tryOnError) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return getAuthorizationHeader(accessToken);
                }
            };

            AppController.getInstance().addToRequestQueue(request);
        });
    }

    @Override
    public Single<PictureEntity> uploadPicture(final String name,
                                               final byte[] imageData,
                                               final String accessToken) {
        return Single.create(e -> {
            final String method = "upload/";

            HashMap<String, Object> params = new HashMap<>();
            params.put("uploadType", "media");
            params.put("name", name);

            final String url = EndpointUtils.getUrl(method.concat(ROUTE), params);
            JsonObjectRequest request = new JsonObjectRequest(url, null,
                    response -> e.onSuccess(JsonMapper
                            .transformPictureEntity(response.toString())), e::tryOnError) {

                @Override
                public byte[] getBody() {
                    return imageData;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = getAuthorizationHeader(accessToken);
                    params.put("Content-Type", "image/jpeg");
                    return params;
                }
            };

            AppController.getInstance().addToRequestQueue(request);
        });
    }

    private Map<String, String> getAuthorizationHeader(String accessToken) {
        Map<String, String> params = new HashMap<>();
        params.put("Authorization", "Bearer " + accessToken);
        return params;
    }
}
