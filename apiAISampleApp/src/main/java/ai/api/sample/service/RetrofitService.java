package ai.api.sample.service;

import com.google.gson.JsonElement;

import ai.api.sample.model.OrderParameter;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by anuj on 14/07/18.
 */
public interface RetrofitService {

    @POST("/orders")
    void postParameter(@Body OrderParameter orderParameter, Callback<JsonElement> callback);
}
