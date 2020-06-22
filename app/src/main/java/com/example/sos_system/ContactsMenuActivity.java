package com.example.sos_system;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.os.Bundle;

public class ContactsMenuActivity extends AppCompatActivity
{
    Toolbar toolbar; //Creating toolbar instance

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts); //This activity linked to "activity_contacts.xml" - design of activity. ("AndroidManifest.xml" updated with second activity")

        //Toolbar
        toolbar = findViewById(R.id.toolbar); //Linking variable to toolbar object in "activity_main.xml" i.e. app
        setSupportActionBar(toolbar); //Setting it as preferred action bar
        getSupportActionBar().setTitle("SOS Emergency Safety System"); //Setting the title

    }

}
