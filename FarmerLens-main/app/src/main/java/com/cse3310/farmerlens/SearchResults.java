package com.cse3310.farmerlens;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class SearchResults extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        PlantIdResponse plantIdResponse = getIntent().getParcelableExtra("PLANT_ID_RESPONSE");
    }
}