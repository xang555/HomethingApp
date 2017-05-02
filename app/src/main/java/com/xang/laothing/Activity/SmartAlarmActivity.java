package com.xang.laothing.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.xang.laothing.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SmartAlarmActivity extends AppCompatActivity {

    @BindView(R.id.center_title)
    TextView centerTitle;
    @BindView(R.id.maintoolbar)
    Toolbar maintoolbar;
    @BindView(R.id.spinner_smart_sensor)
    Spinner spinnerSmartSensor;

    private ArrayAdapter<String> adapter;
    private List<String> spinner_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_alarm);
        ButterKnife.bind(this);

        centerTitle.setText(R.string.smart_slarm_title);
        maintoolbar.setTitle("");
        maintoolbar.setNavigationIcon(R.drawable.cancel);
        setSupportActionBar(maintoolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }


    @Override
    protected void onStart() {
        super.onStart();

        spinner_data = new ArrayList<String>();
        spinner_data.add("Gass Sensor 1");
        spinner_data.add("Gass Sensor 2");

        adapter = new ArrayAdapter<String>(SmartAlarmActivity.this,android.R.layout.simple_spinner_item,spinner_data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSmartSensor.setAdapter(adapter);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_for_gass_smartalarm,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


}
