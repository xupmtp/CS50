package edu.harvard.cs50.pokedex;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class PokemonActivity extends AppCompatActivity {
    private TextView nameTextView;
    private TextView numberTextView;
    private TextView type1TextView;
    private TextView type2TextView;
    private String url;
    private RequestQueue requestQueue;
    private Button catchButton;
    private ImageView imageView;
    private TextView specieView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon);

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        url = getIntent().getStringExtra("url");
        nameTextView = findViewById(R.id.pokemon_name);
        numberTextView = findViewById(R.id.pokemon_number);
        type1TextView = findViewById(R.id.pokemon_type1);
        type2TextView = findViewById(R.id.pokemon_type2);
        catchButton = findViewById(R.id.pokedex_catch_button);
        imageView = findViewById(R.id.pokedex_image);
        specieView = findViewById(R.id.pokemon_description);

        load();
    }

    public void load() {
        type1TextView.setText("");
        type2TextView.setText("");

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    nameTextView.setText(response.getString("name"));
                    numberTextView.setText(String.format("#%03d", response.getInt("id")));
                    new DownloadSpriteTask().execute(response.getJSONObject("sprites").getString("front_default"));
                    loadSpecies(response.getJSONObject("species").getString("url"));

                    JSONArray typeEntries = response.getJSONArray("types");
                    for (int i = 0; i < typeEntries.length(); i++) {
                        JSONObject typeEntry = typeEntries.getJSONObject(i);
                        int slot = typeEntry.getInt("slot");
                        String type = typeEntry.getJSONObject("type").getString("name");

                        if (slot == 1) {
                            type1TextView.setText(type);
                        } else if (slot == 2) {
                            type2TextView.setText(type);
                        }
                    }

                    boolean isCatch = getPreferences(Context.MODE_PRIVATE).getBoolean(nameTextView.getText().toString(), true);
                    catchButton.setText(isCatch ? "Catch" : "Release");
                } catch (JSONException e) {
                    Log.e("cs50", "Pokemon json error", e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("cs50", "Pokemon details error", error);
            }
        });

        requestQueue.add(request);
    }

    private void loadSpecies(String url1) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url1, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray entries = response.getJSONArray("flavor_text_entries");
                    for (int i = 0; i < entries.length(); i++) {
                        JSONObject entry = entries.getJSONObject(i);
                        if ("en".equals(entry.getJSONObject("language").getString("name"))){
                            specieView.setText(entry.getString("flavor_text"));
                            System.out.println(entry.getString("flavor_text"));
                            break;
                        }
                    }
                } catch (JSONException e) {
                    Log.e("cs50", "Pokemon species error", e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("cs50", "Pokemon species Json error", error);
            }
        });
        requestQueue.add(request);
    }

    public void toggleCatch(View view) {
        String name = (String) nameTextView.getText();
        Button btn = (Button) view.findViewById(R.id.pokedex_catch_button);
        boolean isCatch = "Catch".equals(btn.getText());
        btn.setText(isCatch ? "Release" : "Catch");
        getPreferences(Context.MODE_PRIVATE).edit().putBoolean(name, !isCatch).apply();
    }

    private class DownloadSpriteTask extends AsyncTask<String, Void, Bitmap> {

        /**
         * 根據url下載圖片
         *
         * @param strings
         * @return
         */
        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                Log.i("cs50", "Start download image");
                return BitmapFactory.decodeStream(url.openStream());
            } catch (IOException e) {
                Log.e("cs50", "Download sprite error", e);
                return null;
            }
        }

        /**
         * 取得下載好的圖片並set到imageView
         *
         * @param bitmap 載好的圖片
         */
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            Log.i("cs50", "Start set image to view");
            imageView.setImageBitmap(bitmap);
        }
    }
}
