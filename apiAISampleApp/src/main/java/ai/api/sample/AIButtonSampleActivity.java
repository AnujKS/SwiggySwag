/**
 * Copyright 2017 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ai.api.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.squareup.okhttp.OkHttpClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ai.api.android.AIConfiguration;
import ai.api.android.GsonFactory;
import ai.api.model.AIError;
import ai.api.model.AIResponse;
import ai.api.model.Metadata;
import ai.api.model.Result;
import ai.api.model.Status;
import ai.api.sample.adapter.ChatRecyclerAdapter;
import ai.api.sample.model.Message;
import ai.api.sample.model.OrderParameter;
import ai.api.sample.service.RetrofitService;
import ai.api.ui.AIButton;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

public class AIButtonSampleActivity extends BaseActivity implements AIButton.AIButtonListener {

    public static final String TAG = AIButtonSampleActivity.class.getName();

    private AIButton aiButton;
    private TextView resultTextView;
    String API_BASE_URL = "http://172.16.120.140:8080";
    RetrofitService mClient;
    private Gson gson = GsonFactory.getGson();
    RecyclerView recyclerView;
    ChatRecyclerAdapter chatRecyclerAdapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aibutton_sample);

        resultTextView = (TextView) findViewById(R.id.resultTextView);
        aiButton = (AIButton) findViewById(R.id.micButton);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        final AIConfiguration config = new AIConfiguration(Config.ACCESS_TOKEN,
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);

        config.setRecognizerStartSound(getResources().openRawResourceFd(R.raw.test_start));
        config.setRecognizerStopSound(getResources().openRawResourceFd(R.raw.test_stop));
        config.setRecognizerCancelSound(getResources().openRawResourceFd(R.raw.test_cancel));

        aiButton.initialize(config);
        aiButton.setResultsListener(this);

        RestAdapter.Builder builder =
                new RestAdapter.Builder()
                        .setEndpoint(API_BASE_URL)
                        .setClient(new OkClient(new OkHttpClient()));
        RestAdapter adapter = builder.build();
        mClient = adapter.create(RetrofitService.class);

        chatRecyclerAdapter = new ChatRecyclerAdapter(new ArrayList<Message>());
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(chatRecyclerAdapter);

        TTS.speak("welcome");
    }

    @Override
    protected void onPause() {
        super.onPause();

        // use this method to disconnect from speech recognition service
        // Not destroying the SpeechRecognition object in onPause method would block other apps from using SpeechRecognition service
        aiButton.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // use this method to reinit connection to recognition service
        aiButton.resume();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_aibutton_sample, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        final int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(AISettingsActivity.class);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResult(final AIResponse response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "onResult");

                //resultTextView.setText(gson.toJson(response));

                Log.i(TAG, "Received success response");

                // this is example how to get different parts of result object
                final Status status = response.getStatus();
                Log.i(TAG, "Status code: " + status.getCode());
                Log.i(TAG, "Status type: " + status.getErrorType());

                final Result result = response.getResult();
                Log.i(TAG, "Resolved query: " + result.getResolvedQuery());

                //add message
                chatRecyclerAdapter.addMessage(new Message(result.getResolvedQuery(), 1));
                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.scrollToPosition(chatRecyclerAdapter.getItemCount() - 1);
                    }
                }, 400);

                Log.i(TAG, "Action: " + result.getAction());
                final String speech = result.getFulfillment().getSpeech();
                Log.i(TAG, "Speech: " + speech);
                TTS.speak(speech);

                //add message
                chatRecyclerAdapter.addMessage(new Message(speech, 2));
                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.scrollToPosition(chatRecyclerAdapter.getItemCount() - 1);
                    }
                }, 400);

                final Metadata metadata = result.getMetadata();
                if (metadata != null) {
                    Log.i(TAG, "Intent id: " + metadata.getIntentId());
                    Log.i(TAG, "Intent name: " + metadata.getIntentName());
                }

                OrderParameter orderParameter = null;
                final HashMap<String, JsonElement> params = result.getParameters();
                if (params != null && !params.isEmpty()) {
                    Log.i(TAG, "Parameters: ");
                    orderParameter = new OrderParameter();
                    for (final Map.Entry<String, JsonElement> entry : params.entrySet()) {

                        if (entry.getKey().equals("food-type")) {
                            orderParameter.setFoodType(entry.getValue().getAsString());
                        }
                        if (entry.getKey().equals("restaurant")) {
                            orderParameter.setRestaurant(entry.getValue().getAsString());
                        }
                        if (entry.getKey().equals("date-time")) {
                            orderParameter.setDeliverAt(entry.getValue().getAsString());
                        }
                        if (entry.getKey().equals("username")) {
                            orderParameter.setUserName(entry.getValue().getAsString());
                        }
                        if (entry.getKey().equals("note")) {
                            orderParameter.setNote(entry.getValue().getAsString());
                        }
                        if (entry.getKey().equals("item")) {
                            orderParameter.setItem(entry.getValue().getAsString());
                        }

                        Log.i(TAG, String.format("%s: %s", entry.getKey(), entry.getValue().toString()));
                    }
                }

                if (orderParameter != null) {
                    mClient.postParameter(orderParameter, new Callback<JsonElement>() {
                        @Override
                        public void success(JsonElement jsonElement, Response response) {
                            Log.d("Order response", response.getStatus() + "");
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.d("Order response", error.getMessage() + "");
                        }
                    });
                }
            }

        });
    }

    @Override
    public void onError(final AIError error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "onError");
                resultTextView.setText(error.toString());
            }
        });
    }

    @Override
    public void onCancelled() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "onCancelled");
                resultTextView.setText("");
            }
        });
    }

    private void startActivity(Class<?> cls) {
        final Intent intent = new Intent(this, cls);
        startActivity(intent);
    }
}
