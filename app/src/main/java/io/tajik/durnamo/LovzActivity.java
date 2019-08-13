package io.tajik.durnamo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.nfc.Tag;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.agora.durnamo.R;

public class LovzActivity extends AppCompatActivity
        implements LocationListener {

    public double latitude;
    public double longitude;
    public LocationManager locationManager;
    public Criteria criteria;
    public String bestProvider;
    private TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lovz);
        LinearLayout video = findViewById(R.id.video);
        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //отправляем вызов на сервер
                //открываем окно видео для ЛОВЗ
                startActivity(new Intent(getApplicationContext(), VideoChatViewActivity.class));
            }
        });

        LinearLayout analyze = findViewById(R.id.analyze);
        analyze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //отправляем вызов на сервер
                //открываем окно видео для ЛОВЗ
                startActivity(new Intent(getApplicationContext(), AnalyzeActivity.class));
            }
        });

        LinearLayout geo = findViewById(R.id.geo);
        geo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();

            }
        });
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    //String languageToLoad = "ru";
                    //Locale locale = new Locale(languageToLoad);
                    //int ttsLang = textToSpeech.setLanguage(new Locale("ru","Ru"));
                    int ttsLang = textToSpeech.setLanguage(Locale.US);
                    if (ttsLang == TextToSpeech.LANG_MISSING_DATA
                            || ttsLang == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "The Language is not supported!");
                    } else {
                        Log.i("TTS", "Language Supported.");
                    }
                    Log.i("TTS", "Initialization success.");
                } else {
                    Toast.makeText(getApplicationContext(), "TTS Initialization failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public static boolean isLocationEnabled(Context context) {
        //...............
        return true;
    }


    protected void getLocation() {
        if (isLocationEnabled(LovzActivity.this)) {
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            criteria = new Criteria();
            bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true)).toString();

            //You can still do this if you like, you might get lucky:
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

            Location location = locationManager.getLastKnownLocation(bestProvider);
            if (location != null) {
                Log.e("TAG", "GPS is on");
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                //Toast.makeText(LovzActivity.this, "latitude:" + latitude + " longitude:" + longitude, Toast.LENGTH_SHORT).show();
                //searchNearestPlace(voice2text);
                DownloadUrl downloadUrl = new DownloadUrl();
                String answer;
                try {
                    answer = downloadUrl.readUrl(getUrl(latitude, longitude));
                    answer = GetAdd(answer);


                } catch (IOException e) {
                    e.printStackTrace();
                    answer = "Что то пошло не так";
                }

                Toast.makeText(LovzActivity.this, answer, Toast.LENGTH_LONG).show();
                int speechStatus = textToSpeech.speak(answer, TextToSpeech.QUEUE_FLUSH, null);
                if (speechStatus == TextToSpeech.ERROR) {
                    Log.e("TTS", "Error in converting Text to Speech!");
                }
            } else {
                //This is what you need:
                locationManager.requestLocationUpdates(bestProvider, 1000, 0, this);
            }
        } else {
            //prompt user to enable location....
            //.................
        }
    }


    public String GetAdd(String JSON_DATA) {
        final JSONObject obj;
        String answer = "Не смог получить адресс";
        try {
            JSONObject jObj = new JSONObject(JSON_DATA);
            answer = jObj.getJSONArray("results").getJSONObject(0).getString("formatted_address");
        } catch (JSONException e) {
            e.printStackTrace();

        }

        return answer;
    }


    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private String getUrl(double latitude, double longitude) {
        String nbUrl = "https://maps.googleapis.com/maps/api/geocode/json?";
        StringBuilder googlePlacesUrl = new StringBuilder(nbUrl);
        googlePlacesUrl.append("latlng=" + String.valueOf(latitude) + "," + String.valueOf(longitude));
        googlePlacesUrl.append("&language=ru");
        googlePlacesUrl.append("&key=" + Constants.GOOGLE_PLACE_KEY);
        Log.d("getUrl", googlePlacesUrl.toString());
        return (googlePlacesUrl.toString());
    }


}
