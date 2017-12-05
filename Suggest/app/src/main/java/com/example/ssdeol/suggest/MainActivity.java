package com.example.ssdeol.suggest;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.content.res.Configuration;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlacePicker;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ListView myDrawerList;
    private DrawerLayout myDrawerLayout;
    private ArrayAdapter<String> myAdapter;
    private ActionBarDrawerToggle myToggle;
    private String title;

    private static final int PLACE_PICKER_REQUEST = 1;

    private Context context;
    String[] nav = {"Home", "Places", "My List", "About", "Why Not?"};




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_gps);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        myDrawerList = (ListView)findViewById(R.id.navList);
        myDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        title = getTitle().toString();

        addDrawerItems();
        setupDrawer();
    }

    private void addDrawerItems(){
        myAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, nav);
        myDrawerList.setAdapter(myAdapter);

        myDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Toast.makeText(MainActivity.this, "This will move you around", Toast.LENGTH_SHORT).show();

                if (nav[0] == (String) parent.getItemAtPosition(position)) {
                    // Go to the maps
                    Intent intent = new Intent(MainActivity.this, MapActivity.class);
                    startActivity(intent);
                    myDrawerLayout.closeDrawers();
                } else if (nav[1] == (String) parent.getItemAtPosition(position)) {
                    // goes to Places Intent
                    // Google Places API (Directly from there) Pick Places
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    try {
                        Intent intent = builder.build(MainActivity.this);
                        startActivityForResult(intent, PLACE_PICKER_REQUEST);
                    } catch (GooglePlayServicesRepairableException e) {
                        Log.e("Places", "GooglePlayServicesRepairableException: " + e.getMessage());
                    } catch (GooglePlayServicesNotAvailableException e) {
                        Log.e("Places", "GooglePlayServicesNotAvailableException: " + e.getMessage());
                    }
                    myDrawerLayout.closeDrawers();
                } else if (nav[2] == (String) parent.getItemAtPosition(position)) {
                    // Go to my List Activity
                    Intent intent = new Intent(MainActivity.this, MyListActivity.class);
                    startActivity(intent);
                    myDrawerLayout.closeDrawers();
                } else if (nav[3] == (String) parent.getItemAtPosition(position)) {
                    // Go to About us Activity
                    Intent intent = new Intent(MainActivity.this, AboutUsActivity.class);
                    startActivity(intent);
                    myDrawerLayout.closeDrawers();
                } else if (nav[4] == (String) parent.getItemAtPosition(position)) {
                    // Go to About us Activity
                    Intent intent = new Intent(MainActivity.this, YouAreDoneActivity.class);
                    startActivity(intent);
                    myDrawerLayout.closeDrawers();
                }
            }
        });
    }

    private void setupDrawer(){
        myToggle = new ActionBarDrawerToggle(this, myDrawerLayout, R.string.drawer_open, R.string.drawer_close){

            public void onDrawerOpened(View drawerView){
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Suggest");
                invalidateOptionsMenu();
            }

            public void onDrawerClosed(View view){
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(title);
                invalidateOptionsMenu();
            }
        };

        myToggle.setDrawerIndicatorEnabled(true);
        myDrawerLayout.addDrawerListener(myToggle);
    }

    @Override
    protected void onPostCreate(Bundle instanceState){
        super.onPostCreate(instanceState);
        myToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration config){
        super.onConfigurationChanged(config);
        myToggle.onConfigurationChanged(config);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(myToggle.onOptionsItemSelected(item)){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void homeButtonPressed(View view) {
        Intent intent = new Intent(MainActivity.this, MapActivity.class);
        startActivity(intent);
    }

    public void placeButtonPressed(View view) {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            Intent intent = builder.build(MainActivity.this);
            startActivityForResult(intent, PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            Log.e("Places", "GooglePlayServicesRepairableException: " + e.getMessage());
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.e("Places", "GooglePlayServicesNotAvailableException: " + e.getMessage());
        }
    }

    public void listButtonPressed(View view) {
        Intent intent = new Intent(MainActivity.this, MyListActivity.class);
        startActivity(intent);
    }

    public void aboutUsButtonPressed(View view) {
        Intent intent = new Intent(MainActivity.this, AboutUsActivity.class);
        startActivity(intent);
    }

    public void whyNotButtonPressed(View view) {
        Intent intent = new Intent(MainActivity.this, YouAreDoneActivity.class);
        startActivity(intent);
    }

    /*
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_option_tab, menu);
        return true;
    }*/



}