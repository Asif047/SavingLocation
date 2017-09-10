package com.example.admin.fcmwithappserver;

import android.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.example.admin.fcmwithappserver.helper.AppConfig;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit2.Call;
import retrofit2.Callback;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{

    String BASE_URL = "https://asif047locationtracker.000webhostapp.com";

    private TextView profileTV;
    private Button logoutBtn;
    private Button refreshBtn;

    private FirebaseAuth firebaseAuth;



    //new starts
    private String email;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private Geocoder geocoder;
    private List<Address> addressList;

    private static final String TAG = "Location";
    private static String latitude;
    private static String longitude;
    //new ends



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firebaseAuth=FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser()==null)
        {
            finish();
            startActivity(new Intent(this,LogInActivity.class));
        }

        final FirebaseUser user=firebaseAuth.getCurrentUser();


        profileTV= (TextView) findViewById(R.id.profile_textview);
        logoutBtn= (Button) findViewById(R.id.log_out_button);
        refreshBtn= (Button) findViewById(R.id.refresh_button);


        //String temp_name=getIntent().getStringExtra("user_name");
        //Toast.makeText(ProfileActivity.this,temp_name,Toast.LENGTH_LONG).show();
        if(user.getDisplayName()!=null)
            profileTV.setText("Welcome "+user.getDisplayName());
        if(user.getDisplayName()==null)
            profileTV.setText("Welcomeee "+getIntent().getStringExtra("user_name"));






        //new starts

        geocoder = new Geocoder(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for(Location location : locationResult.getLocations()){

                    //new starts
                    email=user.getEmail().toString().trim();
                    Toast.makeText(ProfileActivity.this,"Email:"+email,Toast.LENGTH_LONG).show();
                    latitude=""+location.getLatitude();
                    longitude=""+location.getLongitude();
                    //new ends


                    Log.e(TAG, "current Latitude: "+location.getLatitude());
                    Log.e(TAG, "current Longitude: "+location.getLongitude());
                    try {
                        addressList = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                        Log.e(TAG, "address line: "+addressList.get(0).getAddressLine(0));
                        Log.e(TAG, "city: "+addressList.get(0).getLocality());
                        Log.e(TAG, "country: "+addressList.get(0).getCountryName());
                        Log.e(TAG, "postal code: "+addressList.get(0).getPostalCode());
                        Log.e(TAG, "sublocality: "+addressList.get(0).getSubLocality());
                        Log.e(TAG, "phone: "+addressList.get(0).getPhone());



                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                fusedLocationProviderClient.removeLocationUpdates(locationCallback);
            }
        };
        getLastLocation();
        createLocationUpadates();



        update_data(email);

        //new ends




        logoutBtn.setOnClickListener(this);

    }


    //new starts

    public void update_data(String email) {

        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(BASE_URL) //Setting the Root URL
                .build();

        AppConfig.update api = adapter.create(AppConfig.update.class);

        api.updateData(


                latitude,
                longitude,
                email,
                new retrofit.Callback<retrofit.client.Response>() {
                    @Override
                    public void success(retrofit.client.Response result, retrofit.client.Response response) {

                        try {

                            BufferedReader reader = new BufferedReader(new InputStreamReader(result.getBody().in()));
                            String resp;
                            resp = reader.readLine();
                            Log.d("success", "" + resp);

                            JSONObject jObj = new JSONObject(resp);
                            int success = jObj.getInt("success");

                            if (success == 1) {
                                Toast.makeText(getApplicationContext(), "Successfully updated", Toast.LENGTH_SHORT).show();
                                //startActivity(new Intent(getApplicationContext(),DisplayData.class));
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "Update Failed", Toast.LENGTH_SHORT).show();
                            }

                        } catch (IOException e) {
                            Log.d("Exception", e.toString());
                        } catch (JSONException e) {
                            Log.d("JsonException", e.toString());
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Toast.makeText(ProfileActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }
        );
    }
    //new ends


    //new starts

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},100);
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location!=null)
                {
                    Toast.makeText(ProfileActivity.this, "Latitude: " + location.getLatitude() +
                            "Longitude: " + location.getLongitude(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "latitude"+location.getLatitude());
                    Log.e(TAG, "logintude"+location.getLongitude());

                }

            }
        });
    }





    private void createLocationUpadates() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }


    //new ends







    @Override
    public void onClick(View v) {
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(this,LogInActivity.class));
    }

    public void refresh_on_click(View view) {
        update_data(email);
    }
}
