package com.example.ssdeol.suggestdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PlacesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);
    }

    public void showDetails(View view) {
        Intent intent = new Intent(this, DetailsActivity.class);

        Button button = (Button) view;
        String message = button.getText().toString();

        intent.putExtra("Tag", message);
        startActivity(intent);
    }
}
