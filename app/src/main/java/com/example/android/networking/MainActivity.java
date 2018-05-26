package com.example.android.networking;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Path;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;

import com.bumptech.glide.Glide;

import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity{

    static final private String BASE_ALBUM_URL = "https://api.imgur.com/";
    static final private String CLIENT_ID = "e5128e510ce7eb1";
    static final private String ALBUM_ID = "sqZi0nZ";

    RecyclerView recyclerView;
    ImageAdaptor imageAdaptor;

    public interface ImgurService{
        @Headers("Authorization: Client-ID " + CLIENT_ID)
        @GET("/3/album/{id}")
        Call<JsonElement> getAlbumImages(@Path("id") String id);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openImageViewer(View view){
        Log.d("openImageViewer", "");
        Toast.makeText(this, "openImageViewer", Toast.LENGTH_SHORT).show();
        setContentView(R.layout.view_recycler);
        recyclerView = (RecyclerView) findViewById(R.id.view_recycler);

        // changes in content do not affect view size
        // this line improves performance
        recyclerView.setHasFixedSize(true);

        // set images adaptor
        imageAdaptor = new ImageAdaptor();
        recyclerView.setAdapter(imageAdaptor);

        // set the layout manager to arrange the items
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // connect to images album
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_ALBUM_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ImgurService imgurService = retrofit.create(ImgurService.class);

        Call<JsonElement> callBack = imgurService.getAlbumImages(ALBUM_ID);
        callBack.enqueue(new Callback<JsonElement>() {

            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                Toast.makeText(MainActivity.this, "onResponse", Toast.LENGTH_SHORT).show();
                onResponseHelper(call, response);
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Toast.makeText(MainActivity.this, "onFailure", Toast.LENGTH_SHORT).show();
                this.onFailure(call, t);
            }
        });
    }

    public void onResponseHelper(Call<JsonElement> call, Response<JsonElement> response){
        Log.d("retrofit response", String.valueOf(call) + " ,," + String.valueOf(response));
        JsonObject jsonObject = response.body().getAsJsonObject();
        JsonArray jsonArray = jsonObject.get("data").getAsJsonObject().get("images").getAsJsonArray();

        String[] urls = new String[14];
        int pos = 0;
        for (JsonElement json : jsonArray){
            urls[pos] = json.getAsJsonObject().get("link").getAsString();
            Log.d("URL link", urls[pos]);
            pos++;
        }
        new MyAsyncTask(this).execute(urls);
    }

    protected class MyAsyncTask extends AsyncTask<String, Void, Void>{
        private Context context;

        protected MyAsyncTask(Context context){
            this.context = context;
        }
        @Override
        protected Void doInBackground(String... urls){
            loadImages(urls);
            return null;
        }
        protected void onPostExecute(Void... nulls){
            imageAdaptor.notifyDataSetChanged();
            Log.d("onPostExecute", "Images loaded");
        }
        private void loadImages(String... urls){
            int pos = 0;
            for (String url: urls){
                try {
                    Bitmap bitmap = Glide.with(MainActivity.this)
                            .load(url).asBitmap().into(-1, -1).get();
                    imageAdaptor.setImage(bitmap, pos);
                    pos++;
                } catch (InterruptedException e){
                    Log.e("InterruptedException", String.valueOf(e));
                } catch (ExecutionException e){
                    Log.e("ExecutionException", String.valueOf(e));
                }
            }
        }
    }



}
