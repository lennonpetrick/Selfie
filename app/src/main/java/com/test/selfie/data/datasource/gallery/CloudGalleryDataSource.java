package com.test.selfie.data.datasource.gallery;

import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.test.selfie.application.AppController;
import com.test.selfie.data.entity.PictureEntity;
import com.test.selfie.data.entity.mapper.JsonMapper;
import com.test.selfie.utils.EndpointUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.Single;

import static com.android.volley.Request.Method.DELETE;
import static com.android.volley.Request.Method.POST;

public class CloudGalleryDataSource implements GalleryDataSource {

    private static final String ROUTE = "drive/v2/files";
    private static final String BOUNDARY = "selfie_boundary";

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
            /*
                Use this appDataFolder for create a hidden file into the Google Drive.
                It is only removed by accessing Manage Apps into Google Drive's Settings
                and uninstall the app the drive
            */
            JSONObject jsonParent = new JSONObject();
            jsonParent.put("id", "appDataFolder");

            JSONArray jsonParents = new JSONArray();
            jsonParents.put(jsonParent);

            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put("title", name);
            jsonRequest.put("mimeType", "image/jpeg");
            jsonRequest.put("parents", jsonParents);

            HashMap<String, Object> params = new HashMap<>();
            params.put("uploadType", "multipart");

            final String formattedBody = getFormattedBody(jsonRequest.toString(), imageData);
            final String url = EndpointUtils.getUrl("upload/" + ROUTE, params);

            JsonObjectRequest request = new JsonObjectRequest(POST, url, null,
                    response -> e.onSuccess(JsonMapper
                            .transformPictureEntity(response.toString())), e::tryOnError) {

                @Override
                public byte[] getBody() {
                    try {
                        return formattedBody.getBytes(getParamsEncoding());
                    } catch (UnsupportedEncodingException e) {
                        return null;
                    }
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = getAuthorizationHeader(accessToken);
                    params.put("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
                    return params;
                }
            };

            AppController.getInstance().addToRequestQueue(request);
        });
    }

    @Override
    public Completable deletePicture(final String id, final String accessToken) {
        return Completable.create(e -> {
            final String url = EndpointUtils.getUrl(ROUTE + "/" + id);
            StringRequest request = new StringRequest(DELETE, url,
                    response -> e.onComplete(), e::tryOnError) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return getAuthorizationHeader(accessToken);
                }
            };

            AppController.getInstance().addToRequestQueue(request);
        });
    }

    private String getFormattedBody(String json, byte[] imageData) {
        return "--" + BOUNDARY +
                "\n" +
                "Content-Type: application/json" +
                "\n\n" +
                json +
                "\n\n" +
                "--" + BOUNDARY +
                "\n" +
                "Content-Type: image/jpeg" +
                "\n" +
                "Content-Transfer-Encoding: base64" +
                "\n\n" +
                Base64.encodeToString(imageData, Base64.DEFAULT) +
                "\n" +
                "--" + BOUNDARY + "--";
    }

    private Map<String, String> getAuthorizationHeader(String accessToken) {
        Map<String, String> params = new HashMap<>();
        params.put("Authorization", "Bearer " + accessToken);
        return params;
    }
}
