package com.example.myparkingspot;

import android.Manifest;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Button ParkBtn;
    TextView locText;
    private RequestQueue requestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //global declarations
        ParkBtn = (Button) findViewById(R.id.ParkBtn);
        locText = (TextView) findViewById(R.id.LocationTextView);
        requestQueue = Volley.newRequestQueue(this);

        //final declarations


        requestQueue.start();
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);
        ParkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GPSTracker gpsTracker = new GPSTracker(getApplicationContext());
                Location location = gpsTracker.getLocation();
                if(location != null){
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    //Toast.makeText(getApplicationContext(), "LAT: " + latitude + "\nLONG: "+ longitude, Toast.LENGTH_LONG).show();

                    @Nullable ApplicationInfo applicationInfo = null;
                    try {
                        applicationInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
                        Bundle bundle = applicationInfo.metaData;
                        String key = bundle.getString("com.google.android.geo.API_KEY");
                        String url = String.format(Locale.US,"https://maps.googleapis.com/maps/api/geocode/json?latlng=%f,%f&key=%s",latitude,longitude,key);
                        JsonObjectRequest request = new JsonObjectRequest(url,null,new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject resp) {
                                try {
                                    if(resp!=null)
                                    {
                                        String addr = resp.getJSONArray("results").getJSONObject(0).getString("formatted_address");
                                        locText.setText(addr);
//                                        Log.i("MainActivity","Trying to get address: " + addr);
                                    }
                                }
                                catch (JSONException e){
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("MainActivity",error.getLocalizedMessage());
                                Log.e("MainActivity",error.toString());
                            }
                        } );
                      Log.i("MainActivity","Request as received: "+ request);
                      requestQueue.add(request);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        //outside button listener

//        public void saveData(){
//
//        }
//
//        public void loadData(){
//
//        }

    }
}
