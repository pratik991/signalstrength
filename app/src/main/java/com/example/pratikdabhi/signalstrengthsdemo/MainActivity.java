package com.example.pratikdabhi.signalstrengthsdemo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    SignalStrengthListener signalStrengthListener;
    TextView signalStrengthTextView;
    TextView locationTextView;
    TelephonyManager telephonyManager;
    private DBHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        myDb = new DBHelper(this);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        signalStrengthTextView = (TextView) findViewById(R.id.signalText);

        locationTextView = (TextView) findViewById(R.id.locationText);


        //start the signal strength listener
        signalStrengthListener = new SignalStrengthListener();
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        Button start = (Button) findViewById(R.id.startSavingDataBtn);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                telephonyManager.listen(signalStrengthListener, SignalStrengthListener.LISTEN_SIGNAL_STRENGTHS);
            }
        });

        Button pause = (Button) findViewById(R.id.pauseSavingDataBtn);
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                telephonyManager.listen(signalStrengthListener, SignalStrengthListener.LISTEN_NONE);
            }
        });

        Button resume = (Button) findViewById(R.id.resumeSavingDataBtn);
        resume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                telephonyManager.listen(signalStrengthListener, SignalStrengthListener.LISTEN_SIGNAL_STRENGTHS);
            }
        });

        Button stop = (Button) findViewById(R.id.stopSavingDataBtn);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                telephonyManager.listen(signalStrengthListener, SignalStrengthListener.LISTEN_NONE);
            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> arrayOfData = myDb.getAllData();
                ArrayList<String> result = new ArrayList<String>();
                StringBuilder builder = new StringBuilder();
                for(String s : arrayOfData) {
                    builder.append(s);
                    builder.append("\n");
                }

                for (String data : arrayOfData) {
                    result.add(data);
                }
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto","pratik260@gmail.com", null));
                intent.putExtra(Intent.EXTRA_SUBJECT, "Signal Strength Data");
                intent.putExtra(Intent.EXTRA_TEXT, builder.toString());
                startActivity(Intent.createChooser(intent, "Choose an Email client :"));
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class SignalStrengthListener extends PhoneStateListener {
        @Override
        public void onSignalStrengthsChanged(android.telephony.SignalStrength signalStrength) {

            // get the signal strength (a value between 0 and 31)
            int strengthAmplitude = signalStrength.getGsmSignalStrength();
            Result result = getSignalStrength(signalStrength);

            //do something with it (in this case we update a text view)
            signalStrengthTextView.setText(String.valueOf(result.getNetwork() + " : " + result.getDb()));

            // instantiate the location manager, note you will need to request permissions in your manifest
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            // get the last know location from your location manager.
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            // now get the lat/lon from the location and do something with it.
            locationTextView.setText("Latitude:" + String.format("%.4f",location.getLatitude() )+ ", Longitude:" +  String.format("%.4f",location.getLongitude() ));
            String timeStamp = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss").format(new Date());
            myDb.insertData(timeStamp + " -> ", String.valueOf(result.getNetwork() + " : " + result.getDb()), " on Latitude:" + String.format("%.4f",location.getLatitude() )+ ", Longitude:" +  String.format("%.4f",location.getLongitude()));
            super.onSignalStrengthsChanged(signalStrength);
        }
    }

    protected Result getSignalStrength(SignalStrength signal) {

        Result result = new Result();

        String ssignal = signal.toString();
        String[] parts = ssignal.split(" ");

        int dB = -120; // No Signal Measured when returning -120 dB

        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if(info.getType() == ConnectivityManager.TYPE_WIFI){
            WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            result.setDb(wifiManager.getConnectionInfo().getRssi());
            result.setNetwork("WIFI");
        }
        if(info.getType() == ConnectivityManager.TYPE_MOBILE){
            int networkType = info.getSubtype();
            switch (networkType) {
                case TelephonyManager.NETWORK_TYPE_GPRS:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_CDMA:
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
                case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
                case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
                case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
                    int ltesignal = Integer.parseInt(parts[9]);
                    // check to see if it get's the right signal in dB, a signal below -2
                    if(ltesignal < -2) {
                        result.setDb(ltesignal);
                        result.setNetwork("LTE");
                    }
                default:
            }
        }
        return result;
    }
}


