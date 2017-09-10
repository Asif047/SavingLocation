package com.example.admin.fcmwithappserver;

import android.*;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;


import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText nameET;
    private EditText emailET;
    private EditText passwordET;
    private TextView signInTV;
    private Button registerBtn;

    private String temp_name;

    private FirebaseAuth firebaseAuth;




    private ProgressDialog progressDialog;


    //new starts
    String app_server_url="https://asif047locationtracker.000webhostapp.com/fcmtest2/fcm_insert.php";


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
        setContentView(R.layout.activity_register);




        progressDialog=new ProgressDialog(this);

        nameET= (EditText) findViewById(R.id.edit_text_name);
        emailET= (EditText) findViewById(R.id.edit_text_email);
        passwordET= (EditText) findViewById(R.id.edit_text_password);
        signInTV= (TextView) findViewById(R.id.textview_register_sign_in);

        registerBtn= (Button) findViewById(R.id.button_register_user);
        firebaseAuth=FirebaseAuth.getInstance();




        if(firebaseAuth.getCurrentUser()!=null)
        {
            //profile activity here
            finish();
            startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
        }






        //new starts

        geocoder = new Geocoder(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for(Location location : locationResult.getLocations()){

                    //new starts
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

        //new ends



        registerBtn.setOnClickListener(this);
        signInTV.setOnClickListener(this);
    }





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
                    Toast.makeText(RegisterActivity.this, "Latitude: " + location.getLatitude() +
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

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }


    //new ends


    private void registerUser()
    {
        String name=nameET.getText().toString().trim();
        final String email=emailET.getText().toString().trim();
        String password=passwordET.getText().toString().trim();


        if(TextUtils.isEmpty(name))
        {

            //email is empty
            Toast.makeText(this,"Please enter the name",Toast.LENGTH_SHORT).show();
            return;
        }


        if(TextUtils.isEmpty(email))
        {

            //email is empty
            Toast.makeText(this,"Please enter the email",Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(password))
        {
            //password is empty
            Toast.makeText(this,"Please enter the password",Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.getTrimmedLength(password)<6)
        {
            Toast.makeText(this,"password should be at least 6 characters",Toast.LENGTH_SHORT).show();
            return;
        }



        progressDialog.setMessage("Registering user....");
        progressDialog.show();


        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful())
                        {
                            userProfile();

                            //profile activity here
                            finish();



                            //new starts

                            SharedPreferences sharedPreferences=getApplicationContext()
                                    .getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);

                            final String token=sharedPreferences.getString(getString(R.string.FCM_TOKEN),"");


                            StringRequest stringRequest=new StringRequest(Request.Method.POST, app_server_url,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {

                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                }
                            })

                            {
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {

                                    Map<String,String> params=new HashMap<String,String>();
                                    params.put("fcm_token",token);
                                    params.put("email",email);
                                    params.put("latitude",latitude);
                                    params.put("longitude",longitude);
                                    return params;
                                }
                            };
                            MySingleton.getmInstance(RegisterActivity.this).addToRequestque(stringRequest);


                            //new ends

                            temp_name=nameET.getText().toString().trim();
                            //Toast.makeText(MainActivity.this,temp_name, Toast.LENGTH_SHORT).show();
                            Intent i=new Intent(RegisterActivity.this, ProfileActivity.class);
                            i.putExtra("user_name",temp_name);

                            startActivity(i);


                        }

                        else
                        {
                            Toast.makeText(RegisterActivity.this,"Your email is not corrent",Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }

                    }
                });

    }



    private void userProfile()
    {
        FirebaseUser user=firebaseAuth.getCurrentUser();

        if(user!=null)
        {
            UserProfileChangeRequest profileUpdates=new UserProfileChangeRequest.Builder()
                    .setDisplayName(nameET.getText().toString().trim()).build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful())
                            {
                                Log.d("TESTING","User profile updated");
                                Toast.makeText(RegisterActivity.this,"User profile updated",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }



    }


    //if the validation is okk
    //will show the progress bar

    @Override
    public void onClick(View view) {

        if(view==registerBtn)
        {
            // user register button action
            registerUser();
        }

        if(view==signInTV)
        {
            //for the log in activity
            startActivity(new Intent(this,LogInActivity.class));
        }

    }
}
