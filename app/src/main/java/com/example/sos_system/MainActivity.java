package com.example.sos_system;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.provider.Settings;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_LOCATION = 1; //Variable used to request permission to send location
    final int SEND_SMS_PERMISSION_REQUEST_CODE = 1; //Variable used to request permission to send SMS

    LocationManager locationManager; //Provides access to teh system location services (https://developer.android.com/reference/android/location/LocationManager)
    String latitude; //Variable which will store the latitude co-ordinate
    String longitude; //Variable which will store the longitude co-ordinate
    Toolbar toolbar; //Creating toolbar instance
    Button SOS_Button; //Creating button instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //This activity linked to "activity_main.xml" - design of activity. ("AndroidManifest.xml" updated with second activity")

        //Toolbar
        toolbar = findViewById(R.id.toolbar); //Linking variable to toolbar object in "activity_main.xml" i.e. app
        setSupportActionBar(toolbar); //Setting it as preferred action bar
        getSupportActionBar().setTitle("SOS Emergency Safety System"); //Setting the title

        //SOS_Button
        SOS_Button = findViewById(R.id.SOS_Button); //Linking variable to button object in "activity_main.xml" i.e. app
        SOS_Button.setEnabled(false); //Initially, button disabled

        //If... granted SMS permissions, then enable button
        if (checkPermission(Manifest.permission.SEND_SMS))
        {
            SOS_Button.setEnabled(true); //Enable button
        }
        //Else... request SMS permissions
        else
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSION_REQUEST_CODE);
        }

        //Request location permission on app startup
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        //Function which performs the following... once "SOS_Button" is clicked
        SOS_Button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE); //Retrieving system location services

                //If SMS & Location permissions granted then do the following...
                if ((checkPermission(Manifest.permission.SEND_SMS)) && ((locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))))
                {
                    String location = getLocation(); //Retrieve location by calling "getLocation" function (returns a String of latitude + "," + longitude)
                    //Initialising all emergency contacts
                    String numbers[] = {
                            "Jordan", "0864474600",
                            "Derek", "0830441944",
                            "Irene", "0873217795",
                            "Sarah Jane", "0863753607",
                            "Serena", "0879079725",
                            "Raymond", "0863958901"};
                    //Initialising emergency SMS message and corresponding current location
                    String smsMessage = "EMERGENCY!!! Attend to Raymond & Sadie Reynolds Immediately!. \n\nTheir location is: https://www.google.com/maps/search/?api=1&query=" + location;

                    SmsManager smsManager = SmsManager.getDefault(); //Creating an "SmsManger" instance which allows us to send data, text and pdu SMS messages
                    //Iterating through all contact numbers and sending emergency SMS to all contacts
                    for (int i = 1; i < numbers.length; i = i + 2)
                    {
                        smsManager.sendTextMessage(numbers[i], null, smsMessage, null, null);
                    }
                    Toast.makeText(MainActivity.this, "SMS Alerts Sent!", Toast.LENGTH_SHORT).show(); //Toast (message) displayed: SMS alerts successfully sent

                    // Change button settings i.e. disabled for 5 seconds after click
                    SOS_Button.setBackgroundColor(Color.BLUE);
                    SOS_Button.setTextSize(60);
                    SOS_Button.setText("ALERT SENT!!!");
                    SOS_Button.setEnabled(false); //Disabling the button

                    //Handler used to schedule commands/messages/runnables to be executed at some point in the future ("SOS_Button" re-enabled after 5 seconds of click)
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable()
                    {
                        public void run()
                        {
                            //Re-enabling the button and returning settings after 5 seconds (prevents double click i.e. possible app crashing)
                            SOS_Button.setEnabled(true);
                            SOS_Button.setBackgroundColor(Color.RED);
                            SOS_Button.setTextSize(90);
                            SOS_Button.setText("SOS");
                        }
                    }, 5000); //5 seconds

                }
                //If either SMS and Location permissions denied then do the following...
                else
                {
                    OnGPS(); //Enable location (GPS) on your phone... this method will prompt you to activate location (GPS)
                    Toast.makeText(MainActivity.this, "SMS Alerts NOT Sent!", Toast.LENGTH_SHORT).show(); //Toast (message) displayed: SMS alerts NOT sent i.e. re-enable and re-try
                }
            }
        });
    }

    //Function that creates a menu for the toolbar
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu, menu); //This class "MenuInflater" is used to instantiate menu XML files into Menu objects
        return true;
    }

    //Function that creates menu items for the toolbar... currently our "Contacts Menu"
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId(); //Creating a item id instance
        //If menu item clicked is "Contacts Menu"
        if(id == R.id.contacts)
        {
            Toast.makeText(getApplicationContext(), "Contacts Menu Selected", Toast.LENGTH_SHORT).show(); //Toast (message) displayed: "Contacts Menu Selected"
            openContactsMenu(); //Access "Contacts Menu" when menu item is selected
        }
        return true;
    }

    //Function that opens "Contacts Menu" in a new activity i.e. another application page
    public void openContactsMenu()
    {
        Intent intent = new Intent(this, ContactsMenuActivity.class);
        startActivity(intent);
    }

    //Function that checks if SMS permission granted/denied
    public boolean checkPermission(String permission)
    {
        int check = ContextCompat.checkSelfPermission(this, permission);
        return (check == PackageManager.PERMISSION_GRANTED);
    }

    //Function that retrieves the devices location if permission granted
    private String getLocation()
    {
        String lat_long = null; //Creating a String to store latitude and longitude values (returned at the end of the method)

        //If... both "ACCESS_FINE_LOCATION" and "ACCESS_COARSE_LOCATION" granted then request those items
        if (ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) !=PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION); //Request FINE and COARSE locations
        }
        //Else... request the last known location
        else
        {
            Location LocationGps= locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location LocationNetwork=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Location LocationPassive=locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            //If... Location GPS co-ordinates (latitude and longitude) aren't "null" then retrieve those values
            if (LocationGps !=null)
            {
                double lat = LocationGps.getLatitude(); //Retrieve latitude
                double longi = LocationGps.getLongitude(); //Retrieve longitude

                latitude = String.valueOf(lat); //Cast double to string
                longitude = String.valueOf(longi); //Cast double to string

                lat_long = latitude + "," + longitude; //Latitude and Longitude values assigned to "lat_long" String variable
            }

            //If... Location Network co-ordinates (latitude and longitude) aren't "null" then retrieve those values
            else if (LocationNetwork !=null)
            {
                double lat=LocationNetwork.getLatitude(); //Retrieve latitude
                double longi=LocationNetwork.getLongitude(); //Retrieve longitude

                latitude=String.valueOf(lat); //Cast double to string
                longitude=String.valueOf(longi); //Cast double to string

                lat_long = latitude+ "," + longitude; //Latitude and Longitude values assigned to "lat_long" String variable
            }

            //If... Location Passive co-ordinates (latitude and longitude) aren't "null" then retrieve those values
            else if (LocationPassive !=null)
            {
                double lat=LocationPassive.getLatitude(); //Retrieve latitude
                double longi=LocationPassive.getLongitude(); //Retrieve longitude

                latitude=String.valueOf(lat); //Cast double to string
                longitude=String.valueOf(longi); //Cast double to string

                lat_long = latitude+ "," + longitude; //Latitude and Longitude values assigned to "lat_long" String variable
            }

            else
            {
                Toast.makeText(this, "Can't Get Your Location", Toast.LENGTH_SHORT).show(); //Toast (message) displayed: "Can't Get Your Location"
            }
        }
        return lat_long; //Return the latitude and longitude co-ordinates
    }

    //Function that prompts the user to enable their location (GPS) if permission denied
    private void OnGPS()
    {
        final AlertDialog.Builder builder= new AlertDialog.Builder(this); //Create a dialog box for this activity which request the users location to be enabled

        //If "YES" clicked in dialog box (permission granted) then start the activity
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("YES", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
            //If "NO" clicked in dialog box (permission denied) then stop/cancel the activity
        }).setNegativeButton("NO", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.cancel();
                }
            });
        final AlertDialog alertDialog=builder.create(); //Create the dialog box
        alertDialog.show(); //Show the dialog box
    }
}