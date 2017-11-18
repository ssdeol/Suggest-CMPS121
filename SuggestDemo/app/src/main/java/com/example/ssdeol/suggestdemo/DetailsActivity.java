package com.example.ssdeol.suggestdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class DetailsActivity extends AppCompatActivity {


    String buttonId;
    ListView infoList;
    List<String> list = new ArrayList<String>();
    ArrayAdapter<String> listAdapter;
    String data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        infoList = (ListView) findViewById(R.id.infoList);

        Intent intent = getIntent();
        buttonId = intent.getStringExtra("Tag");

        showDetails(buttonId);
    }

    public void showDetails(String buttonId){

        if (buttonId.equals("Foods")){
            Log.d("List", "I am getting the list for Foods");
            listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.food));
            infoList.setAdapter(listAdapter);
        } else if (buttonId.equals("Entertainment")){
            Log.d("List", "I am getting the list for Entertainment");
            listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.entertainment));
            infoList.setAdapter(listAdapter);
        } else if (buttonId.equals("Fun")){
            Log.d("List", "I am getting the list for Fun");
            listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.fun));
            infoList.setAdapter(listAdapter);
        } else if (buttonId.equals("More Food")){
            Log.d("List", "I am getting the list for Foods for entry: " + buttonId );
            Log.d("List", "I am getting the list for moreFoods");
            listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.moreFood));
            infoList.setAdapter(listAdapter);
        } else {
            Log.d("List", "I have no idea which button you pressed or the value that I got but here it is: " + buttonId );
        }
    }
}
